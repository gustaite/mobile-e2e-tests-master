package test.basic.notifications

import RobotFactory.deepLink
import RobotFactory.notificationRobot
import RobotFactory.notificationSettingsRobot
import api.factories.UserFactory
import api.controllers.ConversationAPI
import api.controllers.user.notificationSettingsApi
import api.data.models.isNotNull
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.*
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedPlatform.ANDROID
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.mobile.RunMobile
import util.base.BaseTest

@RunMobile
@Feature("Push notification settings tests")
@LoginToMainThreadUser
class PushNotificationSettingsTests : BaseTest() {

    @BeforeMethod
    fun enablePushNotifications() {
        loggedInUser.notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Turn off push notifications, get a message and check that push notification does not appear")
    fun testTurnOffPushesAndGetMessage() {
        val sender = UserFactory.createRandomUser()
        val initialMessage = "Test message 123"

        deepLink.setting
            .goToPushNotificationSettings()
            .turnOffNotifications(loggedInUser)

        Android.clickHome()

        notificationRobot
            .openNotifications()

        ConversationAPI.createConversation(sender = sender, recipient = loggedInUser, message = initialMessage)

        val isNotificationNotAppeared = notificationRobot.isMessageNotAppeared(initialMessage)
        VintedAssert.assertTrue(isNotificationNotAppeared, "Push notification should not be visible")
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Check if push notification settings do not go back after changing them")
    fun testPushNotificationSettingsDoNotGoBack() {
        deepLink
            .goToSettings()
            .openPushNotificationSettings()

        notificationSettingsRobot
            .turnOffNotifications(loggedInUser)
            .assertNotificationsStaysOff(VintedNotificationSettingsTypes.PUSH)
            .turnOnNotifications(loggedInUser)
            .assertNotificationsStaysOn(VintedNotificationSettingsTypes.PUSH)
    }

    @AfterMethod(description = "Click home")
    fun cleanup() {
        loggedInUser.isNotNull().notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }
}
