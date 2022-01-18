package util.driver

import commonUtil.asserts.VintedAssert
import commonUtil.extensions.removeSpecialSpaceAndMinus
import commonUtil.reporting.Report
import io.appium.java_client.MobileElement
import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.WebDriverWait
import util.*
import util.values.ScrollDirection
import util.values.Visibility

class VintedElement private constructor(lazyElement: Lazy<() -> MobileElement?>, lazyLocator: Lazy<() -> By?>) {
    private val element by lazyElement
    private val locatorBy by lazyLocator

    constructor(element: () -> MobileElement?) : this(lazyOf(element), lazyOf { null })
    constructor(element: () -> MobileElement?, locatorBy: () -> By?) : this(lazyOf(element), lazyOf(locatorBy))

    private fun getElement(): MobileElement {
        val el: MobileElement?
        try {
            el = element()!!
        } catch (e: NullPointerException) {
            throw NullPointerException("Failure on locating element by locator: ${locatorBy()}. Please check a screenshot for more details")
        }
        return el
    }

    private fun getLocator(): Pair<MobileSelector, String>? {
        val by = locatorBy().toString()
        return if (by.isEmpty()) {
            null
        } else {
            val split = by.split(":")
            val selector = split[0].trim()
            val locatorString = split[1].replaceFirst("(?s)(.*)\\]", "$1" + "").trim()

            when (selector) {
                "By.AccessibilityId", "By.name" -> Pair(MobileSelector.IosAccessibilityId, locatorString)
                "By.IosNsPredicate" -> Pair(MobileSelector.IosPredicateString, locatorString)
                else -> null
            }
        }
    }

    companion object {

        @Step("Wait for element to be visible for {waitSec}")
        fun isListVisible(vintedElements: () -> List<VintedElement>, waitSec: Long = 5): Boolean {
            val wait = WebDriverWait(WebDriverFactory.driver, waitSec)
            EnvironmentManager.ImplicitlyWaitTimeout = waitSec
            var isElement = false
            return try {
                Wait.turnOffImplicitlyWait()
                wait.until {
                    try {
                        isElement = vintedElements().let {
                            it.count() > 0 && (it.first().isDisplayed || it.first().isEnabled)
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is java.util.NoSuchElementException, is NoSuchElementException -> {
                                commonUtil.reporting.Report.addMessage("Exception in wait")
                                isElement
                            }
                            else -> throw e
                        }
                    }
                }
                isElement
            } catch (e: TimeoutException) {
                Wait.turnOnImplicitlyWait()
                isElement
            } finally {
                commonUtil.reporting.Report.addMessage("Element was visible: $isElement")
                EnvironmentManager.ImplicitlyWaitTimeout = 10
            }
        }
    }

    val text: String
        get() {
            Wait.forElementToHaveText(this)
            return (getElement().text ?: "").removeSpecialSpaceAndMinus()
        }

    fun click(): VintedElement {
        this.withWait().getElement().click()
        return this
    }

    fun tap() {
        val x: Int
        val y: Int
        this.withWait().getElement().let { x = it.center.x; y = it.center.y }
        VintedDriver.tap(x, y)
    }

    fun tapWithRetry() {
        commonUtil.Util.retryAction(
            {
                try {
                    tap(); true
                } catch (e: InvalidArgumentException) {
                    false
                }
            },
            {
                val screenSize = WebDriverFactory.driver.manage().window().size
                if (screenSize.height < getElement().center.y) VintedDriver.scrollDownABit()
                else VintedDriver.scrollUpABit()
            },
            3
        )
    }

    fun clickWithRetryOnException(count: Int) {
        Util.retryOnException({ getElement().click() }, count)
    }

    @Step("Send keys to element")
    fun sendKeys(vararg keysToSend: CharSequence?) {
        IOS.doIfiOS { getElement().click() }
        getElement().sendKeys(*keysToSend)
    }

    @Step("Send keys in individually")
    fun sendKeysIndividually(vararg keysToSend: CharSequence?) {
        val element = getElement()

        IOS.doIfiOS { element.click() }
        keysToSend.forEach {
            element.sendKeys(it)
        }
    }

    @Step("Clear element")
    fun clear(): VintedElement {
        Wait.forElementToBeClickable(this)
        getElement().clear()
        return this
    }

    fun clearOldIos(): VintedElement {
        // Use this only if regular clear does not work because this is not efficient
        if (Session.isOldIos) {
            getElement().click()
            repeat(15) {
                val delButton = VintedDriver.findElement(null, VintedBy.iOSClassChain("**/XCUIElementTypeKey[`name == 'delete'`]"))
                delButton.click()
            }
        }
        return this
    }

    fun withWait(waitFor: WaitFor = WaitFor.Click, seconds: Long = 5): VintedElement {
        when (waitFor) {
            WaitFor.Click -> Wait.forElementToBeClickable(this, seconds)
            WaitFor.Visible -> Wait.forElementToBeVisible(this, seconds)
        }
        return this
    }

    private fun isFullyVisible(): Boolean {
        var message = "Element was invisible"

        return if (!this.isVisible(1)) false.also { Report.addMessage(message) }
        else {
            val windowSize = WebDriverFactory.driver.manage().window().size.height
            val verticalPositionOfElement = this.center.y
            message =
                "Element's vertical position was: $verticalPositionOfElement. Window size was: $windowSize. $message"

            // Logic here is that element can be treated as displayed, however the center of the element is not visible thus it is not clickable.
            // Appium checks where vertical position of element is and then if it is below bottom of the screen it will have greater value than
            //  window's height.
            if (windowSize < verticalPositionOfElement) false.also {
                message = message.replace("invisible", "not fully visible")
            }
            else true.also { message.replace("invisible", "fully visible") }
        }
    }

    fun withScrollIos(): VintedElement {
        IOS.doIfiOS {
            if (!this.isFullyVisible()) {
                val locator = getLocator()
                Report.addMessage("Locator was: $locator")
                IOS.iOSScrollDownToElementNewWay(locator)
            }
        }
        return this
    }

    @Step("Simple scroll down by 0.1 until element is visible")
    fun withScrollDownSimple(tryForSeconds: Long = 60): VintedElement {
        if (isVisible(1)) {
            Report.addMessage("Element is visible no need to scroll")
            return this
        }

        commonUtil.Util.retryUntil(
            block = {
                VintedDriver.scrollDownABit(0.8, 0.7)
                isVisible(1)
            },
            tryForSeconds = tryForSeconds
        )
        return this
    }

    private fun scrollByDirectionUntilElementIsInTopThirdOfScreen(direction: ScrollDirection): VintedElement {
        val screenSize = WebDriverFactory.driver.manage().window().size
        var previousY = screenSize.height
        commonUtil.Util.retryUntil(
            block = {
                when (direction) {
                    ScrollDirection.DOWN -> VintedDriver.scrollDownABit(0.8, 0.7)
                    ScrollDirection.UP -> VintedDriver.scrollUpABit(0.7, 0.8)
                }

                if (isVisible(1)) {
                    // y == previousY means that screen reached end of scroll
                    val isInTopThird = this.center.y.let { y -> y <= screenSize.height * 0.3 || y == previousY }
                    previousY = this.center.y
                    Report.addMessage("After scroll Y was: $previousY")
                    isInTopThird
                } else false
            },
            tryForSeconds = 60
        )
        return this
    }

    @Step("Simple scroll down by 0.1 until element is in the top third of the screen")
    fun withScrollDownUntilElementIsInTopThirdOfScreen(): VintedElement {
        return scrollByDirectionUntilElementIsInTopThirdOfScreen(ScrollDirection.DOWN)
    }

    @Step("Simple scroll up by 0.1 until element is in the top third of the screen")
    fun withScrollUpUntilElementIsInTopThirdOfScreen(): VintedElement {
        return scrollByDirectionUntilElementIsInTopThirdOfScreen(ScrollDirection.UP)
    }

    fun tapRightBottomCorner(xStep: Int, yOnce: Int, visibilityCheck: () -> Boolean) {
        val x: Int
        val y: Int
        val width: Int
        val height: Int
        getElement().let {
            x = it.location.getX()
            y = it.location.getY()
            width = it.size.width
            height = it.size.height
        }

        var i = 10
        commonUtil.Util.retryUntil(
            block = {
                // Start tapping from the right side
                val coordinateX = x + width - i
                val coordinateY = y + height + yOnce
                VintedAssert.assertTrue(coordinateX >= 0, "X reached end of the screen")
                VintedAssert.assertTrue(coordinateY >= 0, "Y reached end of the screen")
                VintedDriver.tap(coordinateX, coordinateY)
                i += xStep
                visibilityCheck()
            },
            tryForSeconds = 30
        )
    }

    val location: Point get() = getElement().location
    val rect: Rectangle get() = getElement().rect
    val center: Point get() = getElement().center
    val size: Dimension get() = getElement().size
    val isDisplayed: Boolean get() = getElement().isDisplayed
    val isEnabled: Boolean get() = getElement().isEnabled

    @Step("Wait for element to be invisible for {waitSec}")
    fun isInvisible(waitSec: Long = 5): Boolean {
        // waitSect must be greater than 0
        val wait = WebDriverWait(WebDriverFactory.driver, waitSec)
        EnvironmentManager.ImplicitlyWaitTimeout = waitSec
        return try {
            Wait.turnOffImplicitlyWait()
            wait.until {
                try {
                    element().let { it == null || !it.isDisplayed }
                } catch (e: NoSuchElementException) {
                    commonUtil.reporting.Report.addMessage("NoSuchElementException in wait")
                    true
                } catch (e: StaleElementReferenceException) {
                    commonUtil.reporting.Report.addMessage("StaleElementReferenceException in wait")
                    true
                }
            }
            true
        } catch (e: TimeoutException) {
            Wait.turnOnImplicitlyWait()
            false
        } finally {
            EnvironmentManager.ImplicitlyWaitTimeout = 10
        }
    }

    @Step("Wait for element to be visible for {waitSec}")
    fun isVisible(waitSec: Long = 5): Boolean {
        return matchesVisibilityState(visibility = Visibility.Visible, waitSec)
    }

    @Step("Wait for element to match visibility state {visibility.value} for {waitSec}")
    fun matchesVisibilityState(visibility: Visibility, waitSec: Long = 5): Boolean {
        val wait = WebDriverWait(WebDriverFactory.driver, waitSec)
        EnvironmentManager.ImplicitlyWaitTimeout = waitSec
        var isElement = false
        return try {
            Wait.turnOffImplicitlyWait()
            wait.until {
                try {
                    isElement = element().let { it != null && (it.isDisplayed || it.isEnabled) == visibility.value }
                } catch (e: NoSuchElementException) {
                    Report.addMessage("NoSuchElementException in wait")
                    isElement
                } catch (e: StaleElementReferenceException) {
                    Report.addMessage("StaleElementReferenceException in wait")
                    isElement
                } catch (e: java.lang.NullPointerException) {
                    Report.addMessage("No such element found")
                    isElement
                }
            }
            isElement
        } catch (e: TimeoutException) {
            Wait.turnOnImplicitlyWait()
            isElement
        } finally {
            Report.addMessage("Element was visible: $isElement")
            EnvironmentManager.ImplicitlyWaitTimeout = 10
        }
    }

    fun performLongPress(durationInSeconds: Long = 1) {
        VintedDriver.performLongPress(getElement(), durationInSeconds)
    }

    fun performDragAndDrop(elementTo: VintedElement) {
        VintedDriver.performDragAndDrop(getElement(), elementTo)
    }

    fun swipeLeft() {
        VintedDriver.swipeLeft(getElement())
    }

    fun swipeRight() {
        VintedDriver.swipeRight(getElement())
    }

    @Step("Click if exists")
    fun clickIfExists() {
        if (isVisible()) {
            click()
        }
    }

    @Step("Check if checkbox element is checked")
    fun isElementChecked(): Boolean {
        return mobileElement.getAttribute("checked")!!.toBoolean()
    }

    @Step("Check if toggle element is enabled")
    fun isElementEnabled(): Boolean {
        return mobileElement.getAttribute("enabled")!!.toBoolean()
    }

    val mobileElement: MobileElement get() = getElement()
    val mobileElementForVisibilityCheck: MobileElement? get() = element()

    fun getLocatorByValue(): () -> By? {
        return locatorBy
    }

    @Step("Check if locator is scrollable")
    fun isScrollable(): Boolean {
        return this.getLocatorByValue().toString().contains("scrollIntoView")
    }

    @Step("Get value attribute")
    fun getValueAttribute(): String {
        return mobileElement.getAttribute("value")
    }
}

enum class MobileSelector {
    IosAccessibilityId,
    IosPredicateString
}
