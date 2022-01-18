package robot.item

import RobotFactory.feedbackFormRobot
import RobotFactory.rateAppRobot
import io.qameta.allure.Step
import robot.FeedbackFormRobot
import util.Android
import api.data.models.VintedUser

class SwapRobot : ReserveSellBaseRobot() {

    @Step("Sell item for user {user.username}")
    fun swap(user: VintedUser): FeedbackFormRobot {
        selectUser(user)
        Android.doIfAndroid {
            rateAppRobot.clickRateAppLater()
        }
        return feedbackFormRobot
    }
}
