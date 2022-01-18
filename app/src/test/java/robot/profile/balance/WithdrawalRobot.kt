package robot.profile.balance

import RobotFactory.withdrawalSettingsRobot
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class WithdrawalRobot : BaseRobot() {

    private val newPayoutBankAccountCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("new_payout_add_bank_account_cell"),
            iOSBy = VintedBy.accessibilityId("new_payout_add_bank_account")
        )

    private val withdrawToBankAccountButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("new_payout_submit"),
            iOSBy = VintedBy.accessibilityId("make_payout_button_title")
        )

    @Step("Click to add new payout bank account")
    fun clickAddNewPayoutBankAccount(): WithdrawalSettingsRobot {
        newPayoutBankAccountCellElement.click()
        return withdrawalSettingsRobot
    }

    @Step("Click to withdraw money to bank account")
    fun clickWithdrawMoneyToBankAccount(): WithdrawalProgressRobot {
        withdrawToBankAccountButtonElement.click()
        return WithdrawalProgressRobot()
    }
}
