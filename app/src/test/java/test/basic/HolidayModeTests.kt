package test.basic

import RobotFactory.deepLink
import RobotFactory.holidayBannerRobot
import RobotFactory.navigationRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.LoginToNewUser
import api.factories.UserFactory
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.testng.*
import util.values.Visibility

@RunMobile
@Feature("Holiday mode tests")
class HolidayModeTests : BaseTest() {
    @LoginToMainThreadUser
    @Test(description = "Enable holiday mode and check that item is hidden")
    @TmsLink("91")
    fun testEnablingHolidayModeAndCheckingThatItemIsHidden() {
        val item = ItemAPI.uploadItem(itemOwner = loggedInUser, type = ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        navigationRobot
            .openProfileTab()
            .openHolidayModeScreen()
            .toggleHolidayMode()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.userApi.getInfo().isOnHoliday },
            expected = true,
            errorMessage = "User's holiday mode should be on"
        )

        deepLink
            .item.goToItem(item)
            .assertHiddenLabelVisibility(Visibility.Visible)
    }

    @LoginToNewUser
    @Test(description = "Enable holiday mode and check that banner appears")
    @TmsLink("91")
    fun testHolidayModeBanner() {
        navigationRobot
            .openProfileTab()
            .openHolidayModeScreen()
            .toggleHolidayMode()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.userApi.getInfo().isOnHoliday },
            expected = true,
            errorMessage = "User's holiday mode should be on"
        )

        navigationRobot.clickBack()
        holidayBannerRobot.assertHolidayBannerIsVisible()
    }

    @Test(description = "Disable holiday mode and check that item is visible")
    @TmsLink("91")
    fun testDisablingHolidayModeAndCheckingThatItemIsVisible() {
        // Need to enable holiday mode before logging in.
        // Otherwise, app won't fetch updated user info and won't see that holiday mode is on.
        val user = UserFactory.createRandomUser()
        val item = ItemAPI.uploadItem(itemOwner = user, type = ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        user.userApi.enableHolidays()
        workflowRobot.signIn(user)

        navigationRobot
            .openProfileTab()
            .openHolidayModeScreen()
            .toggleHolidayMode()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.userApi.getInfo().isOnHoliday },
            expected = false,
            errorMessage = "User's holiday mode should be off"
        )

        deepLink
            .item.goToItem(item)
            .assertHiddenLabelVisibility(Visibility.Invisible)
    }
}
