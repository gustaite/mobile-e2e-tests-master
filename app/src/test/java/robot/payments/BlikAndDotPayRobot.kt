package robot.payments

import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class BlikAndDotPayRobot : BaseRobot() {

    private val acceptButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"submit_success\")"),
            iOSBy = VintedBy.accessibilityId("accept")
        )

    private val firstPaymentMethodElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"channel_image_1\")"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeImage[`name CONTAINS 'Logo mTransfer'`]")
        )

    @Step("Pay with Blik or Dotpay")
    fun payWithBlikOrDotPay() {
        clickAcceptButton()
    }

    @Step("Click accept button")
    private fun clickAcceptButton() {
        acceptButtonElement.click()
    }

    @Step("Click on the first payment method in DotPay")
    fun clickFirstPaymentMethodInDotPay(): BlikAndDotPayRobot {
        firstPaymentMethodElement.click()
        return this
    }
}
