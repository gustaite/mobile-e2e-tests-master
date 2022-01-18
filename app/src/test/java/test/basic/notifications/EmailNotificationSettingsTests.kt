package test.basic.notifications

import RobotFactory.deepLink
import RobotFactory.notificationSettingsRobot
import api.controllers.user.notificationSettingsApi
import api.data.models.isNotNull
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToDefaultUser
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink

@RunMobile
@LoginToDefaultUser
@Feature("Email notification settings tests")
class EmailNotificationSettingsTests : BaseTest() {

    @Test(description = "Check if email notification settings do not go back after changing them")
    @TmsLink("264")
    fun testThatEmailNotificationSettingsDoNotGoBackAfterChangingThem() {
        deepLink
            .goToSettings()
            .openEmailNotificationSettings()

        notificationSettingsRobot
            .turnOffNotifications(defaultUser)
            .assertNotificationsStaysOff(VintedNotificationSettingsTypes.EMAIL)
            .turnOnNotifications(defaultUser)
            .assertNotificationsStaysOn(VintedNotificationSettingsTypes.EMAIL)
    }

    @AfterMethod(description = "Enable email notification settings for default user")
    fun afterMethod() {
        defaultUser.isNotNull().notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.EMAIL)
    }
}
