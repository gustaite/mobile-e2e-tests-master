package robot.profile.balance

import RobotFactory.walletRobot
import commonUtil.asserts.VintedSoftAssert
import commonUtil.extensions.adaptPrice
import commonUtil.extensions.removeCurrencySymbols
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class WithdrawalProgressRobot : BaseRobot() {

    private val withdrawalAmountElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "balance_payment_status_amount_container", "balance_payment_status_amount"
            ),
            iOSBy = VintedBy.accessibilityId("balance_payment_status_amount_number")
        )

    private val withdrawalStatusProgressElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("balance_payment_status_progress_section"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("balance_payout_progress_section_title"))
        )

    private val withdrawalScreenNameTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("balance_payout_progress_title")),
            iOSBy = VintedBy.iOSTextByBuilder(IOS.getElementValue("balance_payout_progress_title"))
        )

    @Step("Assert withdrawal progress elements are visible")
    fun assertWithdrawalProgressElementsAreVisible(amount: String): WalletRobot {
        val withdrawalAmountElementWithoutCurrency = withdrawalAmountElement.text.removeCurrencySymbols()
        val amountWithAdaptedPrice = amount.adaptPrice()

        val softAssert = VintedSoftAssert()

        softAssert.assertEquals(getActionBarTitle(), withdrawalScreenNameTextElement.text, "Screen name does not match.")
        softAssert.assertTrue(
            withdrawalAmountElementWithoutCurrency.contains(amountWithAdaptedPrice),
            "Withdrawal amount should contain $amountWithAdaptedPrice but was $withdrawalAmountElementWithoutCurrency"
        )
        softAssert.assertTrue(
            withdrawalStatusProgressElement.isVisible(),
            "Withdrawal status progress element should be visible"
        )
        softAssert.assertAll()
        return walletRobot
    }
}
