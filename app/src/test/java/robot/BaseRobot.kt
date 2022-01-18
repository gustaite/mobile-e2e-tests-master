package robot

import commonUtil.asserts.VintedAssert
import io.appium.java_client.*
import io.qameta.allure.Step
import org.openqa.selenium.WebDriverException
import util.*
import util.EnvironmentManager.isiOS
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WebDriverFactory

abstract class BaseRobot {

    private val modalConfirmButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("confirm"))

    private val backButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_button"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'back' || name == 'cancel'")
        )

    private val actionBarLabel: VintedElement
        get() =
            {
                val actionBarElement = VintedDriver.findElement(
                    VintedBy.scrollableId("actionbar_label"),
                    iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeAny[`type != 'XCUIElementTypeButton' AND visible == 1`][-1]")
                )
                if (
                    actionBarElement.isVisible()
                ) {
                    actionBarElement
                } else {
                    VintedDriver.findElement(
                        VintedBy.scrollableId("actionbar_label"),
                        iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeAny[`type != 'XCUIElementTypeButton'`][-1]")
                    )
                }
            }()

    val modalOkButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("modal_primary_button"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS 'close'")
        )

    private val permissionAlertContinueElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("Continue"))

    val driver: AppiumDriver<MobileElement>?
        get() = WebDriverFactory.driver

    @Step("Allow logging in with Facebook or Google")
    fun allowLoggingInWithFacebookOrGoogle() {
        if (isiOS) permissionAlertContinueElementIos.click()
    }

    @Step("Click back button")
    fun clickBack() {
        backButton.click()
    }

    @Step("Click confirm on iOS")
    fun confirmModalOniOS() {
        modalConfirmButtonIos.click()
    }

    @Step("Assert error modal appears")
    fun assertErrorModalAppears() {
        VintedAssert.assertTrue(isModalPresent(), "modal should be visible")
    }

    @Step("Close modal")
    fun closeModal() {
        modalOkButton.click()
    }

    @Step("Check if modal is invisible")
    fun isModalInvisible(): Boolean {
        return modalOkButton.isInvisible(1)
    }

    @Step("Switch context to {contextName}")
    fun switchContextTo(contextName: String) {
        val maxTry = 10
        var allContexts: MutableSet<String>

        for (i in 1..maxTry) {
            try {
                allContexts = driver!!.contextHandles
                commonUtil.reporting.Report.addMessage("Available contexts in $i try: $allContexts")
                val targetContext = allContexts.first { it.contains(contextName) }
                driver!!.context(targetContext)
                commonUtil.reporting.Report.addMessage("Switched to $targetContext")
                break
            } catch (e: WebDriverException) {
                commonUtil.reporting.Report.addMessage("Failed to get contexts, $e")
            } catch (e: NoSuchElementException) {
                commonUtil.reporting.Report.addMessage("Context not found using predicate: $contextName, $e")
            } finally {
                if (i == maxTry) VintedAssert.fail("Failed to get contexts or context was not found using predicate '$contextName' in $i tries")
            }
        }
    }

    @Step("Get action bar title")
    fun getActionBarTitle(): String {
        return actionBarLabel.text
    }

    protected fun isModalPresent(): Boolean {
        return modalOkButton.isVisible()
    }
}
