package util.deepLinks

import RobotFactory.deepLink
import io.qameta.allure.Step

class Payment {
    @Step("Open 'Add Credit card' screen")
    fun goToAddCreditCard() {
        deepLink.openURL("add_new_card")
    }

    @Step("Open 'Activate Balance' screen")
    fun goToActivateBalance() {
        deepLink.openURL("balance_activation")
    }

    @Step("Open 'Payments Identity' screen")
    fun goToPaymentsIdentity() {
        deepLink.openURL("payments_identity")
    }

    @Step("Open 'Wallet' screen")
    fun goToWallet() {
        deepLink.openURL("wallet")
    }
}
