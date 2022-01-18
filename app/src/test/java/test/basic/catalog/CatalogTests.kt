package test.basic.catalog

import RobotFactory.brandActionsRobot
import RobotFactory.catalogRobot
import RobotFactory.deepLink
import RobotFactory.filtersRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.GlobalAPI
import api.controllers.user.*
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.extensions.logAndReturnListSize
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.testng.*
import util.values.CatalogFilterButton
import util.values.ElementByLanguage
import util.values.Visibility

@RunMobile
@Feature("Catalog no reset tests")
class CatalogTests : BaseTest() {

    @BeforeMethod(description = "Delete user searches")
    fun deleteUserSearches() {
        loggedInUser.searchApi.deleteRecentSearches()
    }

    @LoginToMainThreadUser
    @Test(description = "Test if sort and filter, size, brand, condition, color, price buttons are visible in catalog")
    fun testThatFiltersAndSortingButtonsAreVisibleInCatalog() {
        val buttons = listOf(
            CatalogFilterButton.Filter, CatalogFilterButton.Size, CatalogFilterButton.Brand,
            CatalogFilterButton.Condition, CatalogFilterButton.Color, CatalogFilterButton.Price
        )
        deepLink.catalog.goToAllItems()
            .assertFilteringButtonsAreVisible(buttons)
            .assertSortingButtonIsVisible()
    }

    @LoginToMainThreadUser
    @Test(description = "Test horizontal filters visibility when scrolling up and down in catalog")
    fun testHorizontalFiltersVisibilityWhenScrolling() {
        val randomCondition = itemStatuses.random().title

        deepLink.catalog
            .goToAllItems()
            .assertCatalogLayoutIsVisible()
        workflowRobot.assertHorizontalFiltersBarFilterAndSizeButtonsVisibilityWhenScrolling()
        catalogRobot
            .openCatalogFilters()
            .openConditionsScreen()
            .selectCondition(randomCondition)
            .clickBack()
        filtersRobot
            .assertGivenOptionIsSelected(randomCondition)
        filtersRobot
            .clickShowFilterResults()
            .assertHorizontalFiltersBarFilterAndSizeButtonsVisibility(Visibility.Visible)
        workflowRobot.assertHorizontalFiltersBarFilterAndSizeButtonsVisibilityWhenScrolling()
    }

    @LoginToMainThreadUser
    @Test(description = "Test if sorting button displays sorting options and if it's possible to select them")
    fun testSortingInCatalog() {
        val randomSortingOption = ElementByLanguage.SortingOptions.random()
        commonUtil.reporting.Report.addMessage("Selected random sorting option is $randomSortingOption")
        deepLink.catalog.goToAllItems()
            .clickOnSortingButton()
            .assertSortingOptionsModalIsVisible()
            .selectSortingOption(sortingOption = randomSortingOption)
            .openCatalogFilters()
            .assertSelectedSortingOptionIsDisplayedInFilter(sortingOption = randomSortingOption)
    }

    @LoginToMainThreadUser
    @Test(description = "Test if subscribe button in catalog subscribes / unsubscribes search")
    @TmsLink("84")
    fun testSubscribeAndUnsubscribeSearchWithSubscribeButton() {
        deepLink.catalog.goToAllItems()
            .subscribeSearchInCatalog()
            .assertSearchSubscribedModalIsDisplayed()
            .closeSearchSubscribedModalIfVisible()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.searchApi.getSubscribedSearchesList().logAndReturnListSize() },
            expected = 1,
            errorMessage = "saved searches"
        )
        deepLink.catalog.goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .assertRecentSearchesCount(1)
            .openSearch(0)
            .unsubscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.searchApi.getSubscribedSearchesList().size },
            expected = 0,
            errorMessage = "saved searches"
        )

        deepLink.catalog.goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .assertRecentSearchesCount(1)
        workflowRobot.subscribeAndUnsubscribeSearchFromSearchScreen()
    }

    @LoginToMainThreadUser
    @RunMobile(platform = VintedPlatform.IOS, message = "Test for iOS only")
    @Test(description = "Test if brand banner is not visible when two brands are selected")
    fun testIfBrandBannerIsNotDisplayed() {
        val brands = GlobalAPI.getBrands(user = loggedInUser).map { it.title }.sorted()
        val firstBrand = brands.first()
        val secondBrand = brands.last()
        val twoBrandsString = "$firstBrand, $secondBrand"
        deepLink.catalog
            .goToAllItems()
            .openCatalogFilters()
        workflowRobot.openBrandSelectionAndSelectBrand(firstBrand)
        workflowRobot.openBrandSelectionAndSelectBrand(secondBrand)
            .assertGivenOptionIsSelected(twoBrandsString)
        filtersRobot
            .clickShowFilterResults()
            .assertBrandBannerIsNotVisible()
    }

    @LoginToNewUser
    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Check if brand follow button works in catalog screen")
    fun testBrandLikeButtonInCatalogScreen() {
        val brand = "nike"
        deepLink.catalog.goToCatalogWithSearchValue(brand)
        brandActionsRobot.followBrand()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedBrands()!!.filter { it.title.equals(brand, true) }.logAndReturnListSize() },
            expected = 1,
            errorMessage = "Brand $brand should be in followed list",
            retryCount = 40,
            sleepTime = 350
        )

        brandActionsRobot.assertUnfollowButtonIsVisible()
    }

    @LoginToNewUser
    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Check if brand unfollow button works in catalog screen")
    fun testBrandUnfollowButtonInCatalogScreen() {
        val brand = "zara"
        val brandId = GlobalAPI.getBrands(loggedInUser).first { it.title.matches(brand.toRegex(RegexOption.IGNORE_CASE)) }.id
        loggedInUser.personalizationApi.followBrand(brandId)
        deepLink.catalog.goToItemsFilteredByBrandId(brandId)
        brandActionsRobot.unfollowBrand()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedBrands()!!.filter { it.title.equals(brand, true) }.logAndReturnListSize() },
            expected = 0,
            errorMessage = "Brand $brand should no longer be in followed brands list.",
            retryCount = 40,
            sleepTime = 350
        )

        brandActionsRobot.assertFollowButtonIsVisible()
    }
}
