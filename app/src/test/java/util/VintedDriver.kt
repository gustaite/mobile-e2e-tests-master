package util

import commonUtil.asserts.VintedAssert
import io.appium.java_client.MobileElement
import io.appium.java_client.PerformsTouchActions
import io.appium.java_client.TouchAction
import io.appium.java_client.touch.LongPressOptions
import io.appium.java_client.touch.TapOptions
import io.appium.java_client.touch.WaitOptions
import io.appium.java_client.touch.offset.ElementOption
import io.appium.java_client.touch.offset.PointOption
import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriverException
import util.EnvironmentManager.ImplicitlyWaitTimeout
import util.EnvironmentManager.isAndroid
import util.driver.*
import java.time.Duration
import java.util.concurrent.TimeUnit

class VintedDriver {
    companion object {

        fun elementByIdAndTranslationKey(
            androidElement: () -> VintedElement,
            iosTranslationKey: String
        ): VintedElement {
            try {
                WebDriverFactory.driver.manage().timeouts().implicitlyWait(ImplicitlyWaitTimeout, TimeUnit.SECONDS)
                return when {
                    isAndroid -> androidElement()
                    else -> IOS.findElementByTranslationKey(iosTranslationKey)
                }
            } catch (e: NoSuchElementException) {
                commonUtil.reporting.Report.addMessage("$e")
                throw e
            } finally {
                Wait.turnOnImplicitlyWait()
            }
        }

        fun elementByIdAndTranslationKey(androidId: String, iosTranslationKey: String): VintedElement {
            return elementByIdAndTranslationKey(
                androidElement = { findElement(VintedBy.id(androidId)) },
                iosTranslationKey = iosTranslationKey
            )
        }

        @Step("Find element list androidBy: {androidBy} iosBy: {iOSBy}")
        fun findElementList(androidBy: By? = null, iOSBy: By? = null): List<VintedElement> {
            val by = if (isAndroid) androidBy else iOSBy
            return WebDriverFactory.driver.findElementListWithPolling(by!!, ImplicitlyWaitTimeout).map { VintedElement({ it }, { by }) }
        }

        @Step("Find element")
        fun findElement(androidBy: By? = null, iOSBy: By? = null): VintedElement {
            val by = if (isAndroid) androidBy else iOSBy
            return VintedElement(
                {
                    WebDriverFactory.driver.findElementWithPolling(by!!, ImplicitlyWaitTimeout)
                },
                { by }
            )
        }

        @Step("Find element")
        fun findElement(androidBy: By? = null, iosElement: () -> VintedElement): VintedElement {
            return VintedElement(
                {
                    if (isAndroid) {
                        WebDriverFactory.driver.findElementWithPolling(androidBy!!, ImplicitlyWaitTimeout)
                    } else iosElement().mobileElementForVisibilityCheck
                },
                { getLocatorByPlatform(LocatorStrategy(androidBy = androidBy, iosElement = iosElement)) }
            )
        }

        @Step("Find element")
        fun findElement(androidElement: () -> VintedElement?, iOSBy: By? = null, expectedText: String = ""): VintedElement {
            return VintedElement(
                {
                    var element: MobileElement? = null
                    if (isAndroid) element = androidElement()?.mobileElementForVisibilityCheck else {
                        commonUtil.Util.retryAction(
                            {
                                element = WebDriverFactory.driver.findElementWithPolling(iOSBy!!, ImplicitlyWaitTimeout)
                                commonUtil.reporting.Report.addMessage("FindElementMethod: Element text was: ${element?.text}. Searched for text: $expectedText")
                                if (expectedText == "") true else element != null && element!!.text.contains(expectedText)
                            },
                            {}, 3
                        )
                    }
                    element
                },
                { getLocatorByPlatform(LocatorStrategy(androidElement = androidElement, iOSBy = iOSBy)) }
            )
        }

        @Step("Find element")
        fun findElement(androidElement: () -> VintedElement?, iosElement: () -> VintedElement?): VintedElement {
            return VintedElement(
                {
                    if (isAndroid) androidElement()?.mobileElementForVisibilityCheck else iosElement()?.mobileElementForVisibilityCheck
                },
                { getLocatorByPlatform(LocatorStrategy(androidElement = androidElement, iosElement = iosElement)) }
            )
        }

        @Step("Find element list without polling androidBy: {androidBy} iosBy: {iOSBy}")
        fun findElementListWithoutPolling(androidBy: By?, iOSBy: By?): List<VintedElement> {
            // Use this only for very big elements lists which cannot be found differently
            val by = if (isAndroid) androidBy else iOSBy
            return WebDriverFactory.driver.findElementListWithoutPolling(by!!, ImplicitlyWaitTimeout).map { VintedElement({ it }, { by }) }
        }

        fun findElementByText(
            text: String,
            searchType: Util.SearchTextOperator = Util.SearchTextOperator.EXACT
        ): VintedElement {
            val by = if (isAndroid) VintedBy.androidTextByBuilder(
                text = text,
                searchType = searchType
            ) else VintedBy.iOSTextByBuilder(text = text, searchType = searchType)
            return VintedElement({ WebDriverFactory.driver.findElementWithPolling(by, duration = ImplicitlyWaitTimeout) }, { by })
        }

        fun findElementListByText(
            text: String,
            searchType: Util.SearchTextOperator = Util.SearchTextOperator.EXACT
        ): List<VintedElement> {
            val by = if (isAndroid) VintedBy.androidTextByBuilder(
                text = text,
                scroll = true,
                searchType = searchType
            ) else VintedBy.iOSTextByBuilder(text = text, searchType = searchType)
            return WebDriverFactory.driver.findElementListWithPolling(by).map { VintedElement({ it }, { by }) }
        }

        @Step("Tap x: {x}, y: {y}")
        fun tap(x: Int, y: Int) {
            PlatformTouchAction(WebDriverFactory.driver)
                .tap(TapOptions().withPosition(PointOption.point(x, y)))
                .perform()
        }

        @Step("Pull down to refresh")
        fun pullDownToRefresh(relativeBeginYOffset: Double = 0.2, relativeEndYOffset: Double = 0.8) {
            val screenSize = WebDriverFactory.driver.manage().window().size
            scroll(
                beginXOffset = screenSize.getWidth() / 2, beginYOffset = (screenSize.height * relativeBeginYOffset).toInt(),
                endXOffset = screenSize.getWidth() / 2, endYOffset = (screenSize.height * relativeEndYOffset).toInt()
            )
        }

        @Step("Scroll down")
        fun scrollDown() {
            val screenSize = WebDriverFactory.driver.manage().window().size
            scroll(
                beginXOffset = screenSize.getWidth() / 2, beginYOffset = (screenSize.height * 0.8).toInt(),
                endXOffset = screenSize.getWidth() / 2, endYOffset = (screenSize.height * 0.2).toInt()
            )
        }

        @Step("Scroll down a bit")
        fun scrollDownABit(beginY: Double = 0.8, endY: Double = 0.6) {
            val screenSize = WebDriverFactory.driver.manage().window().size
            scroll(
                beginXOffset = screenSize.getWidth() / 2, beginYOffset = (screenSize.height * beginY).toInt(),
                endXOffset = screenSize.getWidth() / 2, endYOffset = (screenSize.height * endY).toInt()
            )
        }

        @Step("Scroll up a bit")
        fun scrollUpABit(beginY: Double = 0.5, endY: Double = 0.8) {
            val screenSize = WebDriverFactory.driver.manage().window().size
            scroll(
                beginXOffset = screenSize.getWidth() / 2, beginYOffset = (screenSize.height * beginY).toInt(),
                endXOffset = screenSize.getWidth() / 2, endYOffset = (screenSize.height * endY).toInt()
            )
        }

        private fun scroll(beginXOffset: Int, beginYOffset: Int, endXOffset: Int, endYOffset: Int) {
            PlatformTouchAction(WebDriverFactory.driver)
                .press(PointOption.point(beginXOffset, beginYOffset))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                .moveTo(PointOption.point(endXOffset, endYOffset))
                .release()
                .perform()
        }

        private fun swipe(element: MobileElement?, startX: Int, endX: Int) {
            val y = element!!.center.y

            commonUtil.reporting.Report.addMessage("starx: $startX, endX: $endX, y: $y")

            PlatformTouchAction(WebDriverFactory.driver).press(PointOption.point(startX, y))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
                .moveTo(PointOption.point(endX, y))
                .release().perform()
        }

        fun swipeLeft(element: MobileElement?) {
            swipe(element, element!!.location.getX() + element.size.getWidth() - 10, element.location.getX() + 2)
        }

        fun swipeRight(element: MobileElement?) {
            swipe(element, element!!.location.getX() + 2, element.location.getX() + element.size.getWidth() - 10)
        }

        fun performLongPress(element: MobileElement?, durationInSeconds: Long = 1) {
            try {
                PlatformTouchAction(WebDriverFactory.driver)
                    .longPress(
                        LongPressOptions()
                            .withElement(ElementOption.element(element!!))
                            .withDuration(Duration.ofSeconds(durationInSeconds))
                    )
                    .release()
                    .perform()
            } catch (e: WebDriverException) {
                if (e.localizedMessage.contains("Original error: Error: socket hang up")) {
                    commonUtil.reporting.Report.addMessage("Crash: $e")
                    VintedAssert.fail("Long press crashed")
                } else throw e
            }
        }

        private fun getLocatorByPlatform(strategy: LocatorStrategy): By? {
            return if (isAndroid) {
                strategy.androidBy ?: strategy.androidElement()!!.getLocatorByValue()()
            } else {
                strategy.iOSBy ?: strategy.iosElement()!!.getLocatorByValue()()
            }
        }

        @Step("Perform drag and drop")
        fun performDragAndDrop(elementFrom: MobileElement?, elementTo: VintedElement?) {
            Android.doIfAndroid {
                PlatformTouchAction(WebDriverFactory.driver).longPress(PointOption.point(elementFrom!!.center.x, elementFrom.center.y))
                    .moveTo(PointOption.point(elementTo!!.center.x, elementTo.center.y))
                    .release().perform()
            }

            IOS.doIfiOS {
                PlatformTouchAction(WebDriverFactory.driver).press(PointOption.point(elementFrom!!.center.x, elementFrom.center.y))
                    .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(10)))
                    .moveTo(PointOption.point(elementTo!!.center.x, elementTo.center.y))
                    .release().perform()
            }
        }

        @Step("Send app to background and open it again")
        fun sendAppToBackgroundAndOpenAgain() {
            WebDriverFactory.driver.runAppInBackground(Duration.ofSeconds(5))
        }
    }

    private class PlatformTouchAction(performsTouchActions: PerformsTouchActions) : TouchAction<PlatformTouchAction>(performsTouchActions)
    private data class LocatorStrategy(
        val androidBy: By? = null, val iOSBy: By? = null, val androidElement: () -> VintedElement? = { VintedElement { null } },
        val iosElement: () -> VintedElement? = { VintedElement { null } }
    )
}
