package robot.profile

import RobotFactory.checkoutRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.CheckoutRobot
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.vintedBalanceText

class PaymentsScreenRobot : BaseRobot() {
    private val addNewCreditCardButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_add"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'add_cc_cell' || name CONTAINS '${ElementByLanguage.addNewCardIosText}'")
        )

    private val walletPaymentMethodElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("payment_option_wallet"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '$vintedBalanceText'")
        )

    private val actionDeleteButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("credit_card_list_item_delete"),
            iOSBy = VintedBy.accessibilityId("delete")
        )

    private val creditCardList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("checkout_pay_in_methods"),
            iOSBy = VintedBy.iOSNsPredicateString(
                "name CONTAINS '${IOS.getElementValue("payment_method_selection_credit_card_format")
                    .replace("%{card_brand}", "").replace("%{card_last_digits}", "").trim()}'"
            )
        )

    private val confirmButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("pay_in_confirm"))

    @Step("Open new credit card screen")
    fun openNewCreditCardScreen(): NewCreditCardRobot {
        addNewCreditCardButton.withWait(waitFor = WaitFor.Visible, seconds = 10).click()
        return NewCreditCardRobot()
    }

    @Step("Check if credit card delete button is visible")
    fun checkIfCreditCardDeleteButtonIsVisible(): PaymentsScreenRobot {
        VintedAssert.assertTrue(actionDeleteButton.withWait(seconds = 10).isVisible(), "delete button should be visible when credit card is added ")
        return this
    }

    @Step("Delete added credit card")
    fun clickDeleteCreditCard(): PaymentsScreenRobot {
        actionDeleteButton.click()
        if (isAndroid) closeModal() else confirmModalOniOS()
        return this
    }

    @Step("Check if credit card is deleted")
    fun checkIfCreditCardDeleted() {
        VintedAssert.assertFalse(actionDeleteButton.isVisible(), "delete button should not be visible when credit card is deleted")
    }

    @Step("Click first credit card and open checkout")
    fun clickFirstCreditCardAndOpenCheckout(): CheckoutRobot {
        if (VintedElement.isListVisible({ creditCardList })) {
            creditCardList.first().click()
            if (isiOS) confirmButtonIos.click()
        }
        return checkoutRobot
    }

    @Step("Select wallet payment option (iOS)")
    fun selectWalletPaymentOptionForIos(): PaymentsScreenRobot {
        IOS.doIfiOS {
            walletPaymentMethodElement.click()
            confirmButtonIos.click()
        }
        return this
    }
}
