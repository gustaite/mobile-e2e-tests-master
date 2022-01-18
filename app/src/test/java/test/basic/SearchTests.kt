package test.basic

import RobotFactory.catalogRobot
import RobotFactory.deepLink
import RobotFactory.filtersRobot
import RobotFactory.filtersWorkflow
import RobotFactory.navigationRobot
import RobotFactory.searchScreenRobot
import RobotFactory.sizeRobot
import RobotFactory.workflowRobot
import api.*
import api.controllers.GlobalAPI
import api.controllers.user.*
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.section.UserShortInfoSectionRobot
import util.base.BaseTest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks

@RunMobile
@Feature("Search tests")
@LoginToMainThreadUser
class SearchTests : BaseTest() {

    @BeforeMethod
    fun cleanup() {
        loggedInUser.personalizationApi
            .unfavoriteAllBrands()
            .searchApi.deleteRecentSearches()
    }

    @Test(description = "Test if item search is working and if brand banner is visible in catalog")
    @RunMobile(neverRunOnSandbox = true)
    @TmsLink("24463")
    fun testBrandBannerIsDisplayed() {
        val brandValue = "Nike"
        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .searchFor(brandValue)
        catalogRobot
            .assertBrandBannerIsVisible()
            .assertSearchedBrandNameIsVisibleInCatalog(brandValue)
    }

    @Test(description = "Test if members search works and if results leads to profile")
    @TmsLink("86")
    fun testMembersSearchIsWorking() {
        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .clickMembersTab()
            .searchFor(otherUser.username)
        searchScreenRobot
            .openProfileThatMatchesSearchedValue(otherUser.username)
        UserShortInfoSectionRobot().assertUsername(otherUser.username)
    }

    @Test(description = "Test if subscribed search with filters stays the same and new search is created after editing filters")
    @TmsLinks(TmsLink("166"), TmsLink("177"))
    fun testEditingSubscribedSearchWithBrandFilter() {
        val firstBrandTitle = GlobalAPI.getBrands(user = loggedInUser)[0].title
        val secondBrandTitle = GlobalAPI.getBrands(user = loggedInUser)[1].title
        val thirdBrandTitle = GlobalAPI.getBrands(user = loggedInUser)[2].title

        deepLink.catalog
            .goToAllItems()
            .openCatalogFilters()
        workflowRobot
            .openBrandSelectionAndSelectBrand(firstBrandTitle)
        workflowRobot
            .openBrandSelectionAndSelectBrand(secondBrandTitle)
            .assertGivenOptionIsSelected("$firstBrandTitle, $secondBrandTitle")
        filtersRobot.clickShowFilterResults()
        catalogRobot.assertSubscribeButtonIsVisible()
            .subscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
        AssertApi.checkCountOfSubscribedSearches(loggedInUser)
        workflowRobot
            .goToBrowseTabAndOpenSearch(0)
            .openCatalogFilters()
        workflowRobot
            .openBrandSelectionAndSelectBrand(firstBrandTitle) // this brand is already selected so this action is used to unselect it
        workflowRobot
            .openBrandSelectionAndSelectBrand(thirdBrandTitle)
            .assertGivenOptionIsSelected("$secondBrandTitle, $thirdBrandTitle")
        filtersRobot.clickShowFilterResults()
        navigationRobot
            .openBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.checkCountOfSubscribedSearches(loggedInUser)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, "$secondBrandTitle, $thirdBrandTitle")
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, "$firstBrandTitle, $secondBrandTitle")
        searchScreenRobot
            .openSearch(1)
            .openCatalogFilters()
            .clearFilters()
        filtersRobot.clickShowFilterResults()
        navigationRobot
            .openBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.checkCountOfSubscribedSearches(loggedInUser)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, "$firstBrandTitle, $secondBrandTitle")
        searchScreenRobot
            .assertRecentSearchesCount(3)
    }

    @Test(description = "Test if recent search is updated after editing filters")
    @TmsLinks(TmsLink("139"), TmsLink("153"))
    fun testIfRecentSearchIsUpdatedAfterEditingFilters() {
        val firstColor = GlobalAPI.getColors(user = loggedInUser)[0].title
        val secondColor = GlobalAPI.getColors(user = loggedInUser)[1].title
        val colorsString = "$firstColor, $secondColor"

        deepLink.catalog
            .goToAllItems()
            .openCatalogFilters()
            .openColorsScreen()
            .selectColor(firstColor)
            .selectColor(secondColor)
            .clickBack()
        filtersRobot
            .assertGivenOptionIsSelected(colorsString)
        filtersRobot.clickShowFilterResults()
        catalogRobot.assertCatalogLayoutIsVisible()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, colorsString)
        workflowRobot
            .goToBrowseTabAndOpenSearch(0)
            .openCatalogFilters()
            .openColorsScreen()
            .unselectColor(secondColor)
            .clickBack()
        filtersRobot
            .assertGivenOptionIsSelected(firstColor)
        filtersRobot
            .clickShowFilterResults()
            .assertCatalogLayoutIsVisible()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstColor)
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, colorsString)
        workflowRobot
            .goToBrowseTabAndOpenSearch(0)
            .openCatalogFilters()
            .clearFilters()
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, firstColor)
    }

    @Test(description = "Test if subscribed search with keyword is not updated after adding some filters")
    @TmsLink("5267")
    fun testIfSubscribedSearchWithKeywordIsNotChangedAfterAddingFilters() {
        val brandTitle = GlobalAPI.getBrands(user = loggedInUser).random().title
        val firstColorTitle = GlobalAPI.getColors(user = loggedInUser).first().title
        val secondColorTitle = GlobalAPI.getColors(user = loggedInUser)[1].title

        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .searchFor(brandTitle)
            .openCatalogFilters()
            .openColorsScreen()
            .selectColor(firstColorTitle)
            .clickBack()
        filtersRobot
            .clickShowFilterResults()
            .subscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
        AssertApi.checkCountOfSubscribedSearches(loggedInUser)
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, brandTitle)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstColorTitle)
        workflowRobot
            .goToBrowseTabAndOpenSearch(0)
            .openCatalogFilters()
            .openColorsScreen()
            .unselectColor(firstColorTitle)
            .selectColor(secondColorTitle)
            .clickBack()
        filtersWorkflow.clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(loggedInUser, firstColorTitle)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, secondColorTitle)
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, brandTitle, expectedNumberOfSearches = 2)
        AssertApi.checkCountOfSubscribedSearches(loggedInUser)
    }

    @Test(description = "Test subsequent creation of subscribed search")
    @TmsLink("4640")
    fun testCreatingTwoSubscribedSearches() {
        val firstCatalogTitle = GlobalAPI.getCatalogs(loggedInUser).first().title

        deepLink.catalog
            .goToBrowseTab()
            .openFirstCategory()
            .assertSubscribeButtonIsVisible()
            .subscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
            .openSizeFilter()
        val randomSize = sizeRobot.getRandomSizeOnScreenFromApi()
        sizeRobot
            .selectSize(randomSize)
        filtersRobot
            .clickShowFilterResults()
            .assertSubscribeButtonIsVisible()
            .subscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstCatalogTitle, expectedNumberOfSearches = 2)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstCatalogTitle, randomSize)
        AssertApi.checkCountOfSubscribedSearches(loggedInUser, 2)
    }

    @Test(description = "Test updating recent search with keyword by adding filters")
    @TmsLink("193")
    fun testUpdatingRecentSearchWithKeyword() {
        val randomColorTitle = GlobalAPI.getColors(loggedInUser).random().title
        val randomBrand = GlobalAPI.getBrands(loggedInUser).random().title

        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .searchFor(randomColorTitle)
        navigationRobot
            .openBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomColorTitle)
        searchScreenRobot
            .openSearch(0)
            .openBrandFilter()
            .searchAndSelectBrand(randomBrand)

        filtersRobot.clickShowFilterResults()
        deepLink.catalog
            .goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomColorTitle)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, randomBrand)
        AssertApi.checkCountOfRecentSearches(loggedInUser)
    }

    @Test(description = "Test if recent search with keyword and filter is updated after clearing filters")
    @TmsLink("382")
    fun testUpdatingRecentSearchWithKeywordAndFilters() {
        val randomColorTitle = GlobalAPI.getColors(loggedInUser).random().title
        val randomBrand = GlobalAPI.getBrands(loggedInUser).random().title

        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .searchFor(randomColorTitle)
        catalogRobot
            .openBrandFilter()
            .searchAndSelectBrand(randomBrand)
        filtersRobot.clickShowFilterResults()
        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomColorTitle)
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, randomBrand)
        searchScreenRobot
            .openSearch(0)
            .openCatalogFilters()
            .clearFilters()
            .clickShowFilterResults()
        deepLink.catalog
            .goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomColorTitle)
        AssertApi.assertSearchWithSelectedFilterDoesNotExist(loggedInUser, randomBrand)
        AssertApi.checkCountOfRecentSearches(loggedInUser, 1)
    }

    @Test(description = "Test if subscribed search with keyword and filters stays the same and new search is created after clearing filters")
    @TmsLink("389")
    fun testEditingSubscribedSearchWithKeywordAndFilters() {
        val firstColorTitle = GlobalAPI.getColors(loggedInUser).first().title
        val randomBrand = GlobalAPI.getBrands(loggedInUser).random().title

        workflowRobot
            .goToBrowseTabAndClickOnSearchField()
            .searchFor(randomBrand)
            .openColorFilter()
            .selectColor(firstColorTitle)
        filtersRobot
            .clickShowFilterResults()
            .assertSubscribeButtonIsVisible()
            .subscribeSearchInCatalog()
            .closeSearchSubscribedModalIfVisible()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstColorTitle)
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomBrand)
        AssertApi.checkCountOfSubscribedSearches(loggedInUser, 1)
        catalogRobot
            .openCatalogFilters()
            .clearFilters()
        filtersRobot.clickShowFilterResults()
        navigationRobot
            .openBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
        AssertApi.assertSearchWithSelectedFilterExists(loggedInUser, firstColorTitle)
        AssertApi.assertThereIsSearchFilteredByKeyword(loggedInUser, randomBrand, 2)
        AssertApi.checkCountOfSubscribedSearches(loggedInUser, 1)
    }
}
