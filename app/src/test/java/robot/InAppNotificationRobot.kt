package robot

import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.inbox.conversation.ConversationRobot
import util.Android
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class InAppNotificationRobot : BaseRobot() {

    private val inAppNotificationTextElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(
                "view_notification_text"
            )
        )

    private val inAppNotificationElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(
                "view_notification_container"
            )
        )

    @Step("Assert in app notification is visible and click it (only Android)")
    fun assertInAppNotificationIsVisibleAndClickItAndroid(): ConversationRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(inAppNotificationElementAndroid.isVisible(60), "In app notification should be visible")
            inAppNotificationElementAndroid.click()
        }
        return conversationRobot
    }

    @Step("Assert in app notification is {expectedText}")
    fun assertInAppNotificationText(expectedText: String): InAppNotificationRobot {
        inAppNotificationElementAndroid.isVisible(60)
        VintedAssert.assertEquals(inAppNotificationTextElementAndroid.text, expectedText, "In app notification text does not match expected")

        return this
    }

    @Step("Click on in app notification")
    fun clickOnInAppNotification() {
        inAppNotificationElementAndroid.click()
    }

    @Step("Swipe left in app notification")
    fun closeInAppNotificationIfExists() {
        Android.doIfAndroid {
            Util.retryOnException(
                block = {
                    if (inAppNotificationElementAndroid.isVisible(1)) {
                        inAppNotificationTextElementAndroid.swipeLeft()
                        commonUtil.reporting.Report.addMessage("In app notification was closed")
                    }
                },
                count = 3
            )
        }
    }
}
