package test.basic.catalog

import RobotFactory.catalogRobot
import RobotFactory.categoriesRobot
import RobotFactory.deepLink
import RobotFactory.filtersRobot
import RobotFactory.filtersWorkflow
import RobotFactory.searchScreenRobot
import RobotFactory.sizeRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.GlobalAPI
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.searchApi
import commonUtil.extensions.adaptPrice
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.extensions.logAndReturnListSize
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.SkipRetryOnFailure
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.values.ElementByLanguage
import util.values.ElementByLanguage.Companion.HomeDecorTextileSize
import util.values.ElementByLanguage.Companion.materialTextileAcrylicText
import util.values.Personalization
import util.values.Visibility

@RunMobile
@Feature("Filters tests")
@LoginToMainThreadUser
class FiltersTests : BaseTest() {
    @BeforeMethod(description = "Delete user searches")
    fun deleteUserSearches() {
        loggedInUser.searchApi.deleteRecentSearches()
    }

    companion object {
        private val MIN_PRICE = "20.0".adaptPrice()
        private val MAX_PRICE = "40.0".adaptPrice()
    }

    @Test(description = "Check if it is possible to filter items by swap option")
    @TmsLink("82")
    fun testSwapOptionInFilterIsWorking() {
        deepLink.catalog
            .goToAllItems()
            .openCatalogFilters()
            .clickOnItemsForSwapSwitcher()
        filtersRobot.clickShowFilterResults()
        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.searchApi.getIsForSwapEnabledSearchesList().logAndReturnListSize() },
            expected = 1,
            errorMessage = "swap on searches"
        )
        catalogRobot
            .openCatalogFilters()
            .clearFilters()
        filtersRobot.clickShowFilterResults()
        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.searchApi.getIsForSwapEnabledSearchesList().logAndReturnListSize() },
            expected = 0,
            errorMessage = "swap on searches"
        )
    }

    @Test(description = "Check if it is possible to filter items by color")
    @TmsLink("82")
    fun testFilteringItemsByColor() {
        val firstColor = GlobalAPI.getColors(user = loggedInUser).first().title
        deepLink.catalog
            .goToBrowseTab()
            .openFirstCategory()
            .openCatalogFilters()
            .openColorsScreen()
            .selectColor(firstColor)
            .clickBack()
        filtersRobot.assertGivenOptionIsSelected(firstColor)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, firstColor)
            .openCatalogFilters()
            .clearFilters()
            .assertGivenOptionIsNotSelected(firstColor)
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, firstColor)
    }

    @Test(description = "Check if it is possible to filter items by selected condition")
    @TmsLink("82")
    fun testFilteringItemsByCondition() {
        val randomCondition = itemStatuses.random().title
        commonUtil.reporting.Report.addMessage(randomCondition)
        deepLink.catalog
            .goToAllItems()
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .openConditionsScreen()
            .selectCondition(randomCondition)
            .clickBack()
        filtersRobot.assertGivenOptionIsSelected(randomCondition)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, randomCondition)
            .openCatalogFilters()
            .clearFilters()
            .assertGivenOptionIsNotSelected(randomCondition)
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, randomCondition)
    }

    @Test(description = "Check if it is possible to filter items by selected catalog")
    @TmsLink("82")
    fun testFilteringItemsByCatalog() {
        val randomCatalog = GlobalAPI.getCatalogs(user = loggedInUser).take(4).random()
        deepLink.catalog
            .goToItemsByCatalogId(randomCatalog.id)
            .openCatalogFilters()
            .assertGivenOptionIsSelected(randomCatalog.title)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, randomCatalog.title, sleepTime = 400)
            .openCatalogFilters()
            .clearFilters()
            .assertGivenOptionIsNotSelected(randomCatalog.title)
        filtersWorkflow.clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, ElementByLanguage.noFiltersSubtitleText, sleepTime = 400)
    }

    @Test(description = "Check if it is possible to filter items by selected size")
    @TmsLink("82")
    fun testFilteringItemsBySize() {
        deepLink.catalog
            .goToBrowseTab()
            .openFirstCategory()
            .openCatalogFilters()
            .openSizesScreen()
        val randomSize = sizeRobot.getRandomSizeOnScreenFromApi()
        sizeRobot
            .selectSizeAndGoBackToFilters(randomSize)
            .assertGivenOptionIsSelected(randomSize)
            .clickShowFilterResults()
        deepLink.catalog
            .goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, randomSize, sleepTime = 400)
        searchScreenRobot
            .openSearch(0)
            .openCatalogFilters()
            .clearFilters()
            .assertGivenOptionIsNotSelected(randomSize)
        filtersWorkflow.clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, ElementByLanguage.noFiltersSubtitleText, sleepTime = 400)
    }

    @Test(description = "Check if it is possible to filter items by selected brand")
    @TmsLink("82")
    fun testFilteringItemsByBrand() {
        val itemBrand = GlobalAPI.getBrands(user = loggedInUser).random()
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, brand_id = itemBrand.id)
        val selectedBrand = GlobalAPI.getBrands(user = loggedInUser).first().title

        deepLink
            .item.goToItem(item)
            .clickOnItemBrand()
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .clearFilters()
            .openBrandsScreen()
        workflowRobot
            .searchAndSelectBrand(selectedBrand)
            .leaveBrandsScreen()
        filtersRobot
            .assertGivenOptionIsSelected(selectedBrand)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, selectedBrand)
            .openCatalogFilters()
            .clearFilters()
            .assertGivenOptionIsNotSelected(selectedBrand)
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, selectedBrand)
    }

    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Check if it is possible to filter items by selected price")
    @TmsLink("82")
    fun testFilteringItemsByPrice() {
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        deepLink.item.goToItem(item)
            .clickMore()
            .clickOnItemCategory()
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .insertPrice(MIN_PRICE, MAX_PRICE)
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchFilteredByPriceExists(loggedInUser, MIN_PRICE, MAX_PRICE, sleepTime = 400)
        catalogRobot.assertItemsListIsNotEmpty()
    }

    @SkipRetryOnFailure
    @Test(description = "Test horizontal filters")
    @TmsLink("82")
    fun testHorizontalFilters() {
        val brand = GlobalAPI.getBrands(user = loggedInUser).first().title
        val condition = itemStatuses.random().title
        val color = GlobalAPI.getColors(user = loggedInUser).first().title
        deepLink.catalog
            .goToAllItems()
            .openSizeFilter()
        categoriesRobot.selectCategory(Personalization.womenCategoryTitle)
        val size = sizeRobot.getRandomSizeOnScreenFromApi()
        sizeRobot.selectSize(size)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, size)
            .openBrandFilter()
            .searchAndSelectBrand(brand)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, size, brand)
            .openStatusFilter()
            .selectCondition(condition)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, size, brand, condition)
            .openColorFilter()
            .selectColor(color)
        filtersWorkflow
            .clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, size, brand, condition, color)
            .openPriceFilter()
        filtersRobot.insertPriceAndShowResults(MIN_PRICE, MAX_PRICE)
        AssertApi.assertSearchFilteredByPriceExists(loggedInUser, MIN_PRICE, MAX_PRICE, sleepTime = 400)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, size, brand, condition, color)
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_CZ_PL_US)
    @Test(description = "Test if material and bedding sizes are visible in home decor filters and work as expected")
    @TmsLink("5270")
    fun testHomeDecorFilters() {
        val homeCatalogId = GlobalAPI.getCatalogs(user = loggedInUser).first { it.code == "HOME" }.id
        deepLink.catalog
            .goToItemsByCatalogId(homeCatalogId)
            .assertHorizontalMaterialButtonVisibility(Visibility.Visible)
            .openCatalogFilters()
        filtersWorkflow.selectHomeDecorMaterialAndSizeAndAssertExpectedResultsAreVisible(materialTextileAcrylicText, HomeDecorTextileSize)
    }
}
