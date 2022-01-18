package test.basic.profile

import RobotFactory.deepLink
import RobotFactory.inAppNotificationRobot
import RobotFactory.profileAboutTabRobot
import RobotFactory.userProfileRobot
import api.controllers.user.notificationSettingsApi
import api.data.models.isNotNull
import commonUtil.data.enums.VintedNotificationSettingsTypes
import io.qameta.allure.Feature
import org.testng.annotations.Test
import robot.profile.FollowType
import robot.profile.FollowUser
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import org.testng.annotations.AfterMethod

@LoginToDefaultUser
@Feature("User profile default user tests")
class UserProfileDefaultTests : BaseTest() {
    @RunMobile(country = VintedCountry.ALL_EXCEPT_NEWLY_ADDED_TO_INTERNATIONAL_PLATFORM, neverRunOnSandbox = true)
    @Issue("AS-368")
    @Test(
        description = "In user profile closet tab check if all verification information is visible and follower " +
            "and following is clickable"
    )
    @TmsLink("267")
    fun testClosetVerificationInformationCheck() {
        deepLink.profile.goToMyProfile()

        userProfileRobot.closetScreen.userInfo
            .assertEmailIsVerified()
            .assert1FollowerAnd2FollowingAreVisible()

        userProfileRobot.closetScreen.userInfo
            .clickOnFollowers()
            .assertFollowers(1, FollowType.Followers, FollowUser.VintedGintare)
            .clickBack()
        userProfileRobot.closetScreen.userInfo
            .clickOnFollowing(true)
            .assertFollowers(2, FollowType.Following, FollowUser.Skorp32, FollowUser.VintedGintare)
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_NEWLY_ADDED_TO_INTERNATIONAL_PLATFORM, neverRunOnSandbox = true)
    @Test(
        description = "In user profile about tab check if all verification information is visible and follower " +
            "and following is clickable"
    )
    @TmsLink("267")
    fun testAboutVerificationInformationCheck() {
        defaultUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)

        deepLink.profile.goToMyProfile()

        userProfileRobot.openAboutTab()
        inAppNotificationRobot.closeInAppNotificationIfExists()
        profileAboutTabRobot.scrollDown().userInfo
            .assertEmailIsVerified()
            .assert1FollowerAnd2FollowingAreVisible()
        userProfileRobot.aboutScreen.assertLastUserLoginIsVisible()
        userProfileRobot.aboutScreen.userInfo
            .clickOnFollowers()
            .assertFollowers(1, FollowType.Followers, FollowUser.VintedGintare)
            .clickBack()
        userProfileRobot.aboutScreen.scrollDown().userInfo
            .clickOnFollowing(false)
            .assertFollowers(2, FollowType.Following, FollowUser.Skorp32, FollowUser.VintedGintare)
    }

    @RunMobile
    @Test(description = "Test if share dialog opens on your own profile")
    @TmsLink("265")
    fun testShareDialog() {
        deepLink.profile.goToMyProfile()

        userProfileRobot
            .clickSettingsButton()
            .clickShareButton()
            .assertSharingOptionsAreVisible()
    }

    @AfterMethod(description = "Enable push notification settings for default user")
    fun afterMethod() {
        defaultUser.isNotNull().notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
    }
}
