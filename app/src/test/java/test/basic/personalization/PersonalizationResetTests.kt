package test.basic.personalization

import RobotFactory.brandActionsRobot
import RobotFactory.categoriesAndSizesRobot
import RobotFactory.deepLink
import RobotFactory.feedRobot
import RobotFactory.filtersWorkflow
import RobotFactory.navigationRobot
import RobotFactory.personalizationBrandRobot
import RobotFactory.personalizationRobot
import RobotFactory.personalizationWorkflowRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.factories.UserFactory
import api.data.models.VintedUser
import api.controllers.GlobalAPI
import commonUtil.data.enums.VintedPortal
import commonUtil.extensions.logAndReturnListSize
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.personalization.PersonalizationBrandAction
import api.controllers.absfeatures.VintedAbTest
import api.controllers.user.personalizationApi
import api.values.PersonalizationSizes
import util.values.Personalization
import commonUtil.testng.config.PortalFactory
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.base.BaseTest

@RunMobile
@Feature("Personalization reset tests")
class PersonalizationResetTests : BaseTest() {
    private var user: VintedUser by thread.lateinit()
    private val womenSizesTitles get() = Personalization.womenSizeTitles
    private val menSizesTitles get() = Personalization.menSizeTitles
    private val babiesSizesTitles get() = Personalization.babiesSizeTitles
    private val womenSizeSTitle get() = Personalization.womenSizeSTitle
    private val menSizeSTitle get() = Personalization.menSizeSTitle

    @BeforeMethod
    fun signIn() {
        user = UserFactory.createRandomUser()
        user.personalizationApi.updatePersonalizationSizes(listOf(PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_1))

        workflowRobot.signIn(user)
    }

    @RunMobile(country = VintedCountry.ALL_EXCEPT_US, message = "Test for all except US as it needs to be run on US IP addresses")
    @Test(description = "Check if item changes (feed refreshes) after setting new personalization sizes")
    fun testFeedRefreshAfterSettingPersonalizationSizes() {
        val oldSizeText = Personalization.getSizesTitles(listOf(PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_1)).single()
        val newSizeText = Personalization.getOneSizeTitleByCountry()

        VintedAbTest.run {
            if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.LT)) {
                // issue https://github.com/vinted/android/issues/13156 won't be fixed
                navigationRobot.openFeedTab().scrollDownToSkipHomepageElements()
            }
        }

        feedRobot.assertItemCountWithSizeNotLessThan(sizeText = oldSizeText, expectedMinItemCount = 2)

        user.personalizationApi.updatePersonalizationSizes(emptyList())

        personalizationWorkflowRobot
            .openPersonalizationSettings()
            .openCategoriesAndSizes()
            .selectPersonalizationSizesAndGoBackToFeed(newSizeText)

        feedRobot.assertItemCountWithSizeNotLessThan(sizeText = newSizeText, expectedMinItemCount = 2)
        feedRobot.assertItemCountWithSize(sizeText = oldSizeText, expectedItemCountMatchingSize = 0)
    }

    @Test(description = "Check if personalization sizes are applied in the filters")
    fun testPersonalizationSizesAreAppliedInFilters() {
        user.personalizationApi.updatePersonalizationSizes(PersonalizationSizes.applicablePortalSizeIds)
        personalizationWorkflowRobot.checkThatPersonalizationSizesAreAppliedInFilters()
    }

    @Test(description = "Check if user can follow and unfollow brand")
    fun testBrandFollowAndUnfollow() {
        personalizationWorkflowRobot.openPersonalizationSettings()

        personalizationRobot
            .clickBrandSection()
            .setFirstBrandAs(PersonalizationBrandAction.FOLLOW)
        brandActionsRobot.assertUnfollowButtonIsVisible()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedBrands()!!.logAndReturnListSize() },
            expected = 1,
            errorMessage = "There should be 1 brand in followed brands list"
        )

        personalizationBrandRobot.setFirstBrandAs(PersonalizationBrandAction.UNFOLLOW)

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.personalizationApi.getFollowedBrands()!!.logAndReturnListSize() },
            expected = 0,
            errorMessage = "There should be 0 brands in followed brands list"
        )
    }

    @Test(description = "Test if right sizes are displayed after changing catalog in filters")
    @TmsLink("242")
    fun testAssertRightSizesAreDisplayedAfterChangingCatalog() {
        user.personalizationApi.updatePersonalizationSizes(Personalization.oneForEachCatalogSizeIds)
        personalizationWorkflowRobot.checkIfPersonalSizesChangesAfterChangingCatalog(Personalization.womenSizeSTitle, Personalization.menSizeSTitle, babiesSizesTitles)
    }

    @Test(description = "Test if personal sizes are not applied after clearing filters")
    @TmsLink("239")
    fun testIfPersonalSizesAreNotAppliedAfterClearingFilters() {
        user.personalizationApi.updatePersonalizationSizes(PersonalizationSizes.applicablePortalSizeIds)
        personalizationWorkflowRobot.checkIfSizesAreNotAppliedAfterAfterClearingFilters(womenSizesTitles, menSizesTitles, babiesSizesTitles)
    }

    @Test(description = "Test if right sizes are displayed when personal sizes are selected for only one catalog")
    @TmsLink("243")
    fun testSizesWhenPersonalSizesAreSelectedForOneCatalog() {
        val personalSizesIdsList = listOf(PersonalizationSizes.MEN_CLOTHES_SIZE_ID_1)

        user.personalizationApi.updatePersonalizationSizes(personalSizesIdsList)
        deepLink.catalog
            .goToBrowseTab()
            .openTab(Personalization.menCategoryTitle)
        filtersWorkflow
            .selectAllCategoriesAndOpenFilters()
            .assertSizesInFilters(Personalization.menSizeSTitle)
        personalizationWorkflowRobot
            .changeCatalogInFilters(Personalization.womenCategoryTitle)
            .assertSizesAreNotSelectedInFilters(Personalization.menSizeSTitle)
    }

    @Test(description = "Test sizes after selecting them both in personalization and in filters")
    fun testIfSizesAddedInPersonalizationAndFiltersAreApplied() {
        user.personalizationApi.updatePersonalizationSizes(PersonalizationSizes.applicablePortalSizeIds)
        personalizationWorkflowRobot.checkSizesAfterSelectingOneMoreInFilters()
    }

    @Test(description = "Check personal sizes after searching for keyword when personal sizes selected for all catalogs")
    @TmsLink("240")
    fun testIfRightSizesAreDisplayedAfterKeywordSearch() {
        val randomBrand = GlobalAPI.getBrands(user = loggedInUser).random().title

        user.personalizationApi.updatePersonalizationSizes(PersonalizationSizes.applicablePortalSizeIds)
        deepLink.catalog.goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .searchFor(randomBrand)
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .openSizesScreen()
        personalizationWorkflowRobot.checkEachCatalogSizesAfterKeywordSearch(womenSizesTitles, menSizesTitles, babiesSizesTitles)
        categoriesAndSizesRobot.clickBack()
        personalizationWorkflowRobot.changeCatalogInFilters(Personalization.womenCategoryTitle)
        personalizationWorkflowRobot.checkSizesAfterKeywordSearch(menSizesTitles)
    }

    @Test(description = "Check personal sizes after searching for keyword when personal sizes are selected for one catalog")
    fun testIfRightSizesAreDisplayedAfterKeywordSearchWithOnePersonalSize() {
        val randomBrand = GlobalAPI.getBrands(user = loggedInUser).random().title
        val personalSizesIds = listOf(PersonalizationSizes.MEN_CLOTHES_SIZE_ID_1)
        val emptyList: List<String> = emptyList()

        user.personalizationApi.updatePersonalizationSizes(personalSizesIds)
        deepLink.goToFeed()
        deepLink.catalog.goToBrowseTab()
            .clickOnSearchFieldInBrowseScreen()
            .searchFor(randomBrand)
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .openSizesScreen()
        personalizationWorkflowRobot.checkEachCatalogSizesAfterKeywordSearch(emptyList, menSizeSTitle, emptyList)
        categoriesAndSizesRobot.clickBack()
        personalizationWorkflowRobot.changeCatalogInFilters(Personalization.womenCategoryTitle)
        personalizationWorkflowRobot.checkSizes(emptyList, menSizeSTitle)
    }

    @Test(description = "Check personal sizes after opening catalog and subcategory")
    @TmsLink("236")
    fun testIfPersonalSizesAreAppliedInSelectedSubcategory() {
        user.personalizationApi.updatePersonalizationSizes(Personalization.oneForEachCatalogSizeIds)
        personalizationWorkflowRobot.openSubcategoryAndCheckPersonalSizesInFilters(womenSizeSTitle, menSizeSTitle)
    }

    @Test(description = "Check sizes after selecting subcategory in filters with personal sizes selected for all catalogs")
    fun testSizesAfterSelectingSubcategoryInFilters() {
        user.personalizationApi.updatePersonalizationSizes(PersonalizationSizes.applicablePortalSizeIds)
        personalizationWorkflowRobot.selectSubcategoryInFiltersAndCheckSizes(womenSizesTitles, menSizesTitles)
    }

    @Test(description = "Check sizes after selecting subcategory in filters when personal sizes are selected for one catalog")
    fun testSizesAfterSelectingSubcategoryInFiltersWithOnePersonalSize() {
        val personalSizesIds = listOf(PersonalizationSizes.WOMEN_CLOTHES_SIZE_ID_1)
        val emptyList: List<String> = emptyList()

        user.personalizationApi.updatePersonalizationSizes(personalSizesIds)
        personalizationWorkflowRobot.selectSubcategoryInFiltersAndCheckSizes(womenSizeSTitle, emptyList)
    }
}
