package robot.item

import RobotFactory.feedbackFormRobot
import io.qameta.allure.Step
import robot.FeedbackFormRobot
import util.Android
import api.data.models.VintedUser

class SellRobot : ReserveSellBaseRobot() {

    @Step("Sell item for user {user.username}")
    fun sell(user: VintedUser): FeedbackFormRobot {
        selectUser(user)
        Android.doIfAndroid {
            RateAppRobot().clickRateAppLater()
        }
        return feedbackFormRobot
    }
}
