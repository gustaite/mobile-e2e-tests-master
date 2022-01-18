package test.basic.profile

import RobotFactory.deepLink
import RobotFactory.cameraAndGalleryWorkflowRobot
import RobotFactory.userProfileEditRobot
import RobotFactory.userProfileEditWorkflowRobot
import RobotFactory.userProfileRobot
import RobotFactory.workflowRobot
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import util.base.BaseTest
import util.image.ImageFactory
import util.values.ToggleValue

@RunMobile
@Feature("Edit profile tests")
class UserProfileEditTests : BaseTest() {

    companion object {
        const val REAL_NAME = "John Doe"
        const val ABOUT_ME = "Super awesome user"
    }

    @LoginToMainThreadUser
    @Issue("MARIOS-540")
    @Test(description = "Change user real name")
    fun testChangingRealName() {
        workflowRobot
            .navigateToAccountSettings()
            .enterRealName(REAL_NAME)
            .saveSection.clickSave()

        userProfileRobot.waitUntilSuccessNotificationDisappears()
        workflowRobot
            .navigateToAccountSettings()
            .assertRealName(REAL_NAME)
    }

    @LoginToMainThreadUser
    @Test(description = "Change user about me info")
    fun testChangingAboutMeInformation() {
        workflowRobot.navigateToProfileDetails()

        userProfileEditRobot
            .enterAboutMe(ABOUT_ME)
            .saveSection.clickSave()

        userProfileRobot.waitUntilSuccessNotificationDisappears()
        workflowRobot.navigateToProfileDetails()

        userProfileEditRobot.assertAboutMe(ABOUT_ME)
    }

    @LoginToNewUser
    @Test(description = "Change user profile photo")
    @TmsLink("78")
    fun testChangeUserProfilePhoto() {
        deepLink.profile.goToEditProfileAndChangePhoto()

        cameraAndGalleryWorkflowRobot.selectPhotosFromGallery()
        userProfileEditRobot
            .saveSection.clickSave()
        userProfileRobot.aboutScreen
            .assertProfilePhotoHasChanged(ImageFactory.DEFAULT_AVATAR)
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_LT_DE_UK)
    @LoginToNewUser
    @Test(description = "Setting users city and asserting its visibility in profile based on toggle")
    @TmsLink("56")
    fun testSettingUserCityAndSwitchingToggle() {
        workflowRobot.navigateToProfileDetails()
        userProfileEditWorkflowRobot
            .clickOnMyLocationAndSelectCountry()
            .selectCityInMyLocation()
            .assertMyLocationToggleValueAndLocationCitySubtitleVisibility(toggleValue = ToggleValue.ON)

        userProfileRobot.waitUntilSuccessNotificationDisappears()

        userProfileEditWorkflowRobot
            .openAboutTabAndAssertCityIsVisible()

        workflowRobot.navigateToProfileDetails()

        userProfileEditRobot
            .clickOnLocationSwitch(portal.country.title)
        userProfileEditWorkflowRobot
            .assertMyLocationToggleValueAndLocationCitySubtitleVisibility(toggleValue = ToggleValue.OFF)

        userProfileRobot.waitUntilSuccessNotificationDisappears()

        userProfileEditWorkflowRobot
            .openAboutTabAndAssertCityIsNotVisible()
    }
}
