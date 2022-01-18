package robot.payments

import RobotFactory.blikAndDotPayRobot
import RobotFactory.bumpsCheckoutRobot
import RobotFactory.iDealRobot
import RobotFactory.newCreditCardRobot
import RobotFactory.payPalRobot
import RobotFactory.sofortRobot
import util.EnvironmentManager.isiOS
import io.qameta.allure.Step
import robot.BaseRobot
import robot.bumps.BumpsCheckoutRobot
import robot.profile.NewCreditCardRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class PaymentMethodsRobot : BaseRobot() {

    private val confirmPaymentMethodButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_pay_in_methods_submit"),
            iOSBy = VintedBy.accessibilityId("pay_in_confirm")
        )

    private val savedCreditCardElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "credit_card_list_item_container",
            "payment_method_selection_credit_card_format"
        )

    private val paymentOptionsTitleElementIos: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_list_item_container"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && name == '${IOS.getElementValue("payment_options_title")}'")
        )

    private val payPalElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payment_option_mango_paypal",
                Android.CELL_TITLE_LINE_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("PayPal")
        )

    private val blikElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payment_option_blik",
                Android.CELL_TITLE_LINE_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("Blik")
        )

    private val dotPayElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payment_option_dotpay",
                Android.CELL_TITLE_LINE_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("Dotpay")
        )

    private val iDealElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payment_option_ideal",
                Android.CELL_TITLE_LINE_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("iDeal")
        )

    private val sofortElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "payment_option_sofort",
                Android.CELL_TITLE_LINE_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("Online-Banking per Sofort")
        )

    @Step("Select credit card payment method")
    fun selectCreditCardPaymentMethod(): NewCreditCardRobot {
        if (confirmPaymentMethodButton.isVisible()) {
            confirmPaymentMethodButton.click()
        }
        return newCreditCardRobot
    }

    @Step("Select new credit card payment option for VAS")
    fun selectNewCreditCardPaymentOptionForVAS(): NewCreditCardRobot {
        selectCreditCardPaymentMethod()
        return newCreditCardRobot
    }

    @Step("Select saved credit card")
    fun clickOnSavedCreditCardCell(): BumpsCheckoutRobot {
        if (isiOS && paymentOptionsTitleElementIos.isVisible()) {
            savedCreditCardElement.click()
            confirmPaymentMethodButton.click()
        }
        return bumpsCheckoutRobot
    }

    @Step("Select third-party payment method {paymentMethod}")
    fun selectThirdPartyPaymentMethod(paymentMethod: PaymentMethods) {
        val paymentMethodElement =
            when (paymentMethod) {
                PaymentMethods.PAYPAL -> payPalElement
                PaymentMethods.BLIK -> blikElement
                PaymentMethods.DOTPAY -> dotPayElement
                PaymentMethods.IDEAL -> iDealElement
                PaymentMethods.SOFORT -> sofortElement
            }
        paymentMethodElement.click()
        confirmPaymentMethodButton.click()
    }

    @Step("Pay using selected third-party payment method")
    fun payUsingSelectedThirdPartyPaymentMethod(paymentMethod: PaymentMethods) {
        when (paymentMethod) {
            PaymentMethods.PAYPAL -> payPalRobot.payWithPayPal()
            PaymentMethods.BLIK -> blikAndDotPayRobot.payWithBlikOrDotPay()
            PaymentMethods.DOTPAY ->
                blikAndDotPayRobot
                    .clickFirstPaymentMethodInDotPay()
                    .payWithBlikOrDotPay()
            PaymentMethods.IDEAL -> iDealRobot.payWithIDeal()
            PaymentMethods.SOFORT -> sofortRobot.payWithSofort()
        }
    }
}

enum class PaymentMethods {
    PAYPAL,
    BLIK,
    DOTPAY,
    IDEAL,
    SOFORT
}
