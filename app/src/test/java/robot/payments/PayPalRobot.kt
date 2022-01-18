package robot.payments

import RobotFactory.workflowRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.workflow.WorkflowRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.defaultEmailAddressText
import util.values.ElementByLanguage.Companion.haveTroubleLoggingInText
import util.values.ElementByLanguage.Companion.payPalConfirmButtonText
import util.values.ElementByLanguage.Companion.payPalLoginButtonAfterRetryText
import util.values.ElementByLanguage.Companion.payPalLoginButtonText

class PayPalRobot : BaseRobot() {

    private val emailElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"email\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextField")
        )

    private val havingTroubleLinkElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().className(\"android.view.View\").text(\"$haveTroubleLoggingInText\")"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeLink' && name == '$haveTroubleLoggingInText'")
        )

    private val defaultEmailAddressElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(defaultEmailAddressText),
            iOSBy = VintedBy.iOSTextByBuilder(defaultEmailAddressText)
        )

    private val confirmButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"btnNext\")"),
            iOSBy = VintedBy.accessibilityId(payPalConfirmButtonText)
        )

    private val passwordElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"password\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeSecureTextField")
        )

    private val loginButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"btnLogin\")"),
            iOSBy = VintedBy.iOSNsPredicateString("name == '$payPalLoginButtonText' || name == '$payPalLoginButtonAfterRetryText'")
        )

    private val submitPaymentButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"payment-submit-btn\")"),
            iOSBy = VintedBy.accessibilityId("Pay Now")
        )

    private val shipToElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().text(\"Ship to\")"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'Ship to'")
        )

    private val payWithElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().text(\"Pay with\")"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'Pay with'")
        )

    private val payPalUsername = "testuoklis@vinted.com"
    private val payPalPassword = "testuoklis"
    private val imageElementsAndroid: List<VintedElement> get() = VintedDriver.findElementList(androidBy = VintedBy.className("android.widget.Image"))

    @Step("Pay with PayPal")
    fun payWithPayPal(): WorkflowRobot {
        enterEmailAndConfirm()
        enterPasswordAndClickLogin()
        waitUntilSubmitPaymentButtonIsAvailableAndClickIt()
        return workflowRobot
    }

    @Step("Enter email and click confirm")
    private fun enterEmailAndConfirm() {
        VintedAssert.assertTrue(emailElement.isVisible(25), "Email element should be visible")
        PayPalCookiesRobot().acceptCookies()
        if (havingTroubleLinkElement.isInvisible(10) || defaultEmailAddressElement.isInvisible()) {
            emailElement.let { email -> email.isVisible(10); email.clear().sendKeys(payPalUsername) }
            confirmButton.click()
        }
    }

    @Step("Enter password and click login")
    private fun enterPasswordAndClickLogin() {
        VintedAssert.assertTrue(passwordElement.isVisible(), "Password element should be visible")
        passwordElement.sendKeys(payPalPassword)
        loginButton.click()
    }

    @Step("Wait until submit payment button is available and click it")
    private fun waitUntilSubmitPaymentButtonIsAvailableAndClickIt() {
        VintedAssert.assertTrue(shipToElement.isVisible(20), "Ship to element should be visible")
        waitForLoadingImagesToDisappear()
        waitForSubmitPaymentElementToBeEnabled()
        VintedAssert.assertTrue(payWithElement.isVisible(20), "Pay with element should be visible")
        submitPaymentButton.withScrollIos().tap()
    }

    @Step("Wait for loading images to disappear (only Android)")
    private fun waitForLoadingImagesToDisappear() {
        Android.doIfAndroid {
            commonUtil.Util.retryUntil(block = { sleepWithinStep(1000); imageElementsAndroid.count() < 3 }, tryForSeconds = 20)
        }
    }

    @Step("Wait for 'Submit Payment' button to be available")
    private fun waitForSubmitPaymentElementToBeEnabled() {
        commonUtil.Util.retryUntil(block = { submitPaymentButton.mobileElement.let { element -> element.isEnabled && element.isDisplayed } }, tryForSeconds = 5)
    }
}
