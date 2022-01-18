package robot.bumps

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.*
import util.values.ElementByLanguage.Companion.vintedBalanceText

class BumpsCheckoutRobot : BaseRobot() {

    private val bumpOrderDetailsElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("bump_order_details", "item_push_up_order_summary_cell_title")

    private val confirmOrderButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("order_submit"), iOSBy = VintedBy.accessibilityId("confirm"))

    private val termsAndConditionsNoteElementAndroid: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("vas_tnc_note"))

    private val paymentMethodInfoCellElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("order_pay_in_method_info"),
            iOSBy = VintedBy.accessibilityId("payment_method"),
        )

    private val payWithVintedWalletTextElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("pay_in_method_wallet_info_line_1")),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '$vintedBalanceText'")
        )

    @Step("Check if order details button is visible and click on it")
    fun assertOrderDetailsButtonIsVisibleAndClick(): BumpOrderDetailsRobot {
        VintedAssert.assertTrue(bumpOrderDetailsElement.isVisible(), "Order details button should be visible")
        bumpOrderDetailsElement.click()
        return RobotFactory.bumpOrderDetailsRobot
    }

    @Step("Check if payment method info cell is visible")
    fun assertPaymentMethodInfoCellIsVisible(): BumpsCheckoutRobot {
        VintedAssert.assertTrue(paymentMethodInfoCellElement.isVisible(), "Payment method info cell should be visible")
        return this
    }

    @Step("Check if terms and conditions note is visible in checkout screen (only for android)")
    fun assertTermsAndConditionsNoteIsVisible(): BumpsCheckoutRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(termsAndConditionsNoteElementAndroid.isVisible(), "Terms and conditions note should be visible")
        }
        return this
    }

    @Step("Check if pay with Vinted wallet option is visible in checkout screen")
    fun assertPayWithVintedWalletOptionIsVisible(): BumpsCheckoutRobot {
        VintedAssert.assertTrue(payWithVintedWalletTextElement.isVisible(), "Pay with Vinted Wallet option should be visible")
        return this
    }

    @Step("Click on confirm order")
    fun confirmOrder() {
        confirmOrderButton.click()
    }
}
