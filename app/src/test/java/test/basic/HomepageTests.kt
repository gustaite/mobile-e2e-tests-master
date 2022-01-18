package test.basic

import RobotFactory.workflowRobot
import RobotFactory.feedRobot
import commonUtil.testng.*
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.ElementByLanguage

@RunMobile
@Feature("Homepage tests")
@LoginToNewUser
class HomepageTests : BaseTest() {

    @Test(description = "Test if homepage is displayed for new user")
    @TmsLinks(TmsLink("20925"), TmsLink("24557"))
    fun testNewUserHomepage() {
        workflowRobot
            .assertBrandSearchingFromFeedWorks()
        workflowRobot
            .checkIfMainHomepageBlocksAreDisplayed()
    }

    @Test(description = "Test if each category in homepage leads to right catalog")
    @TmsLink("24558")
    fun testCategoriesInHomepage() {
        workflowRobot
            .assertEachCategoryInHomepageWorks()
    }

    @Test(description = "Test if favorite items block appears in Homepage")
    @TmsLink("24555")
    fun testFavoritesBlockHomepage() {
        workflowRobot
            .favoriteTwoItemsAndCheckIfFavoriteItemsBlockDisplayed()
            .assertFavoritesSeeAllButtonRedirect()
    }

    @Test(description = "Test popular items block in Homepage")
    @TmsLink("24556")
    fun testPopularItemsBlockHomepage() {
        val newestFirstOption = ElementByLanguage.SortingOptions.last()
        feedRobot.assertPopularItemsAreDisplayed()
        workflowRobot
            .assertPopularItemsSeeAllButtonRedirect()
            .openCatalogFilters()
            .assertSelectedSortingOptionIsDisplayedInFilter(sortingOption = newestFirstOption)
    }
}
