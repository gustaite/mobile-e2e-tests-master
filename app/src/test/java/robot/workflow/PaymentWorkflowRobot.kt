package robot.workflow

import RobotFactory.billingAddressRobot
import RobotFactory.paymentAccountDetailsRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.balance.BillingAddressRobot
import util.base.BaseTest

class PaymentWorkflowRobot : BaseRobot() {

    @Step("Add payment account full name, birthday and security number details")
    fun addPaymentAccountNameBirthdayAndSecurityNumberDetails(): BillingAddressRobot {
        paymentAccountDetailsRobot
            .enterFullName(BaseTest.loggedInUser.realName!!)
            .enterBirthday()
            .enterSecurityNumber()
        return billingAddressRobot
    }
}
