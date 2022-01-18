package util.deepLinks

import RobotFactory.deepLink
import io.qameta.allure.Step

class Verification {

    @Step("Open 'Email change confirmation' screen")
    fun goToEmailChangeConfirmationScreen(userId: Long) {
        deepLink.openURL("enter_new_email?user_id=$userId&code=1a2b3c")
    }

    @Step("Open 'phone verification' screen")
    fun goToPhoneVerificationScreen() {
        deepLink.openURL("verification/phone")
    }

    @Step("Open 'phone change' screen")
    fun goToPhoneChangeScreen() {
        deepLink.openURL("edit/phone")
    }
}
