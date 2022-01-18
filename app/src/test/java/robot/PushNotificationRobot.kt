package robot

import commonUtil.Util.Companion.sleepWithinStep
import io.qameta.allure.Step
import robot.inbox.conversation.ConversationRobot
import util.VintedDriver
import util.driver.*

class PushNotificationRobot : BaseRobot() {
    private fun messageNotificationElement(text: String) =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
        )

    @Step("Open notification shade on Android device")
    fun openNotifications(): PushNotificationRobot {
        (WebDriverFactory.driver.asAndroidDriver()).openNotifications()
        sleepWithinStep(300)
        return this
    }

    @Step("Check that message {text} appeared")
    fun isMessageAppeared(text: String, secondsToWait: Long = 60): Boolean {
        return messageNotificationElement(text).isVisible(secondsToWait)
    }

    @Step("Check that message {text} did not appear")
    fun isMessageNotAppeared(text: String): Boolean {
        return !messageNotificationElement(text).isVisible(30)
    }

    @Step("Open push notification by text {text}")
    fun openPushNotificationByText(text: String): ConversationRobot {
        messageNotificationElement(text).click()
        return ConversationRobot()
    }
}
