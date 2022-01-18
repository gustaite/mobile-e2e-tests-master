package test.basic.personalization

import RobotFactory.brandActionsRobot
import RobotFactory.personalizationRobot
import RobotFactory.personalizationWorkflowRobot
import api.AssertApi
import commonUtil.testng.config.ConfigManager.portal
import api.controllers.user.personalizationApi
import api.values.PersonalizationSizes
import commonUtil.data.enums.VintedCatalogs
import commonUtil.data.enums.VintedPortal
import commonUtil.extensions.logList
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import robot.personalization.PersonalizationBrandAction
import util.base.BaseTest
import util.values.Action

@RunMobile
@Feature("Personalization no reset tests")
@LoginToMainThreadUser
class PersonalizationNoResetTests : BaseTest() {

    @Test(description = "Apply sizes in personalization settings")
    @TmsLink("272")
    fun testApplyingSizesInPersonalizationSettings() {
        personalizationWorkflowRobot.openPersonalizationSettings()
        personalizationRobot
            .openCategoriesAndSizes()
            .selectAndAssertPersonalizationSizes()
    }

    @Test(description = "Test if personal sizes can be unselected")
    @TmsLink("25084")
    fun testPersonalSizesCanBeUnselected() {
        val randomCatalog: VintedCatalogs = portal.catalogs.random()
        val sizesByRandomCatalog = PersonalizationSizes.getCategorySizeIds(randomCatalog)
        loggedInUser.personalizationApi.updateSizes(sizesByRandomCatalog)

        personalizationWorkflowRobot.openPersonalizationSettings()
        personalizationRobot
            .openCategoriesAndSizes()
            .executeActionOnSpecificCategory(randomCatalog, Action.UNSELECT)

        personalizationWorkflowRobot.openPersonalizationSettings()
        personalizationRobot
            .openCategoriesAndSizes()
            .assertSizeVisibilityByCatalog(randomCatalog, Action.UNSELECT)
    }

    @Test(description = "Check if user can search for brand and favorite it")
    @TmsLink("272")
    fun testPersonalizationBrandSearchAndFollowIt() {
        val brand = when (portal) {
            VintedPortal.SB_CZ, VintedPortal.SB_PL -> "ivo nikkolo"
            else -> "nike"
        }
        personalizationWorkflowRobot.openPersonalizationSettings()

        personalizationRobot
            .clickBrandSection()
            .searchForBrand(brand)
            .setFirstBrandAs(PersonalizationBrandAction.FOLLOW)
        brandActionsRobot.assertUnfollowButtonIsVisible()
        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedBrands()!!.logList().filter { it.title.equals(brand, ignoreCase = true) }.size },
            expected = 1,
            errorMessage = "Followed brand list should have 1 brand - $brand"
        )
    }

    @Test(description = "Test if user unfollowing is working")
    @TmsLink("269")
    fun testUserUnfollowing() {
        loggedInUser.personalizationApi.followOtherUser(otherUser)
        personalizationWorkflowRobot
            .openPersonalizationSettings()
        personalizationRobot
            .openFollowedMembers()
            .assertFollowedUserIsVisibleAndUnfollow(otherUser)

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedUsers().size },
            expected = 0,
            errorMessage = "Followed users list should be empty"
        )
    }
}
