package test.basic.profile

import RobotFactory.deepLink
import RobotFactory.userProfileClosetWorkflowRobot
import RobotFactory.userProfileClosetRobot
import api.controllers.item.*
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.ElementByLanguage.Companion.closetFilterCategoryParkasText
import util.values.ElementByLanguage.Companion.getExpectedProfileId

@LoginToNewUser
@Feature("User profile closet tests")
class UserProfileClosetTests : BaseTest() {

    @RunMobile(env = VintedEnvironment.SANDBOX)
    @Test(description = "Filter items in user closet")
    @TmsLink("25672")
    fun testFilteringItemsInUsersCloset() {
        deepLink.profile.goToUserProfile(withItemsUser.id)
        userProfileClosetRobot
            .assertClosetItemsCountWithApi(10)
        userProfileClosetWorkflowRobot
            .openClosetFilteringAndAssertDefaultValues()
            .chooseCategoryToFilter(closetFilterCategoryParkasText)
            .clickOnFilterShowResults()
            .assertClosetFilterHeaderItemsCount("1")
            .openItemAndAssertCategory(PARKAS)
    }

    @RunMobile(env = VintedEnvironment.PRODUCTION, neverRunOnSandbox = true)
    @Test(description = "Filter items in user closet on prod")
    @TmsLink("25672")
    fun testFilteringItemsInUsersClosetProd() {
        deepLink.profile.goToUserProfile(getExpectedProfileId)
        userProfileClosetRobot
            .assertClosetFilterHeaderItemsCount("3")
        userProfileClosetWorkflowRobot
            .openClosetFilteringAndAssertDefaultValues()
            .chooseCategoryToFilter(closetFilterCategoryParkasText)
            .clickOnFilterShowResults()
            .assertClosetFilterHeaderItemsCount("1")
            .openItemAndAssertCategory(PARKAS)
    }
}
