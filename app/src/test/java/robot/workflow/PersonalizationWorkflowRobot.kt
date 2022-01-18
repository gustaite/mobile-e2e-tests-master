package robot.workflow

import RobotFactory.catalogRobot
import RobotFactory.categoriesRobot
import RobotFactory.deepLink
import RobotFactory.filtersRobot
import RobotFactory.filtersWorkflow
import RobotFactory.navigationRobot
import RobotFactory.personalizationRobot
import RobotFactory.workflowRobot
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.data.enums.VintedCatalogs
import io.qameta.allure.Step
import robot.BaseRobot
import robot.browse.FiltersRobot
import robot.personalization.PersonalizationRobot
import commonUtil.extensions.removeListSurroundingsAndReturnString
import util.values.*

class PersonalizationWorkflowRobot : BaseRobot() {

    @Step("Check that personalization sizes are applied in the filters")
    fun checkThatPersonalizationSizesAreAppliedInFilters() {
        openBrowseScreenAndTabForEachCatalog(
            womenCatalogBlock = { selectAllCategoriesOpenFiltersAndCheckSizes(Personalization.womenSizeTitles) },
            menCatalogBlock = { selectAllCategoriesOpenFiltersAndCheckSizes(Personalization.menSizeTitles) },
            babiesCatalogBlock = { selectAllCategoriesOpenFiltersAndCheckSizes(Personalization.babiesSizeTitles) }
        )
    }

    @Step("Check that right personalization sizes are applied after changing catalog in filters screen")
    fun checkIfPersonalSizesChangesAfterChangingCatalog(womenSizeTitles: List<String>, menSizeTitles: List<String>, babiesSizeTitles: List<String>) {
        openBrowseScreenAndTabForEachCatalog(
            womenCatalogBlock = { changeCatalogAndCheckSizes(Personalization.menCategoryTitle, menSizeTitles) },
            menCatalogBlock = { changeCatalogAndCheckSizes(Personalization.womenCategoryTitle, womenSizeTitles) },
            babiesCatalogBlock = { changeCatalogAndCheckSizes(Personalization.babiesCategoryTitle, babiesSizeTitles) }
        )
    }

    @Step("Check if personal sizes are applied in filters after opening subcategory")
    fun openSubcategoryAndCheckPersonalSizesInFilters(womenSizeTitles: List<String>, menSizeTitles: List<String>) {
        openBrowseScreenAndTabForEachCatalog(
            womenCatalogBlock = {
                categoriesRobot.selectCategoryAndSubcategoryInFiltersAndBrowseScreen(ElementByLanguage.getCategoriesAndSubcategories(0))
                catalogRobot.openCatalogFilters()
                checkSizes(womenSizeTitles, menSizeTitles)
            },
            menCatalogBlock = {
                categoriesRobot.selectCategoryAndSubcategoryInFiltersAndBrowseScreen(ElementByLanguage.getCategoriesAndSubcategories(1))
                catalogRobot.openCatalogFilters()
                checkSizes(menSizeTitles, womenSizeTitles)
            }
        )
    }

    @Step("Check if personal sizes are not applied after clearing filters")
    fun checkIfSizesAreNotAppliedAfterAfterClearingFilters(womenSizeTitles: List<String>, menSizeTitles: List<String>, babiesSizeTitles: List<String>) {
        goThroughAllCatalogs(
            womenCatalogBlock = {
                deepLink.catalog.goToBrowseTab()
                    .openTab(Personalization.womenCategoryTitle)
                filtersWorkflow
                    .selectAllCategoriesAndOpenFilters()
                    .assertGivenOptionIsSelected(Personalization.womenCategoryTitle)
                    .assertSizesInFilters(womenSizeTitles)
                    .clearFilters()
                    .assertGivenOptionIsNotSelected(Personalization.womenCategoryTitle)
                    .assertSizesAreNotSelectedInFilters(womenSizeTitles)
            },
            menCatalogBlock = {
                changeCatalogInFilters(Personalization.menCategoryTitle)
                    .assertSizesAreNotSelectedInFilters(menSizeTitles)
            },
            babiesCatalogBlock = {
                changeCatalogInFilters(Personalization.babiesCategoryTitle)
                    .assertSizesAreNotSelectedInFilters(babiesSizeTitles)
            }
        )
    }

    @Step("Select one more size and check them in filters")
    fun checkSizesAfterSelectingOneMoreInFilters() {
        openBrowseScreenAndTabForEachCatalog(
            womenCatalogBlock = {
                val expectedSizes = checkSizesBeforeAndAfterSelectingAdditionalSize(Personalization.womenSizeTitles, Personalization.womenSizeTitleM[0])
                workflowRobot.openCategoriesAndSelectSubcategory(Personalization.womenCategoryTitle, ElementByLanguage.getCategoriesAndSubcategories(0))
                filtersRobot.assertGivenOptionIsSelected(expectedSizes)
            },
            menCatalogBlock = {
                val expectedSizes = checkSizesBeforeAndAfterSelectingAdditionalSize(Personalization.menSizeTitles, Personalization.menSizeTitleL[0])
                workflowRobot.openCategoriesAndSelectSubcategory(Personalization.menCategoryTitle, ElementByLanguage.getCategoriesAndSubcategories(1))
                filtersRobot.assertGivenOptionIsSelected(expectedSizes)
            },
            babiesCatalogBlock = {
                checkSizesBeforeAndAfterSelectingAdditionalSize(Personalization.babiesSizeTitles, Personalization.secondBabiesSizeTitle[0])
            }
        )
    }

    @Step("Check sizes after selecting subcategory in filters")
    fun selectSubcategoryInFiltersAndCheckSizes(womenSizeTitles: List<String>, menSizeTitles: List<String>) {
        goThroughAllCatalogs(
            womenCatalogBlock = {
                deepLink.catalog.goToBrowseTab()
                    .openTab(Personalization.womenCategoryTitle)
                filtersWorkflow.selectAllCategoriesAndOpenFilters()
                checkSizes(womenSizeTitles, menSizeTitles)
                filtersRobot
                    .openCatalogsAndCategoriesScreen()
                    .selectCategoryAndSubcategoryInFiltersAndBrowseScreen(ElementByLanguage.getCategoriesAndSubcategories(0))
                checkSizes(womenSizeTitles, menSizeTitles)
            },
            menCatalogBlock = {
                filtersRobot
                    .openCatalogsAndCategoriesScreen()
                    .selectCategoryAndSubcategoryInFiltersAndBrowseScreen(ElementByLanguage.getCategoriesAndSubcategories(1))
                checkSizes(menSizeTitles, womenSizeTitles)
            }
        )
    }

    @Step("Change catalog in filters screen")
    fun changeCatalogInFilters(catalogName: String): FiltersRobot {
        filtersRobot
            .openCatalogsAndCategoriesScreen()
            .selectCategory(catalogName)
            .selectCategoryAll()
            .assertGivenOptionIsSelected(catalogName)
        return filtersRobot
    }

    @Step("Check sizes for each catalog")
    fun checkEachCatalogSizes(womenSizeTitles: List<String>, menSizeTitles: List<String>, babiesSizeTitles: List<String>) {
        goThroughAllCatalogs(
            womenCatalogBlock = { filtersRobot.assertSizesInFilters(womenSizeTitles) },
            menCatalogBlock = { filtersRobot.assertSizesInFilters(menSizeTitles) },
            babiesCatalogBlock = { filtersRobot.assertSizesInFilters(babiesSizeTitles) }
        )
    }

    @Step("Check sizes for each catalog after keyword search")
    fun checkEachCatalogSizesAfterKeywordSearch(womenSizeTitles: List<String>, menSizeTitles: List<String>, babiesSizeTitles: List<String>) {
        goThroughAllCatalogs(
            womenCatalogBlock = { if (!womenSizeTitles.isNullOrEmpty()) filtersRobot.assertSizesAreNotSelectedInFilters(womenSizeTitles) },
            menCatalogBlock = { if (!menSizeTitles.isNullOrEmpty()) filtersRobot.assertSizesAreNotSelectedInFilters(menSizeTitles) },
            babiesCatalogBlock = { if (!babiesSizeTitles.isNullOrEmpty()) filtersRobot.assertSizesAreNotSelectedInFilters(babiesSizeTitles) }
        )
    }

    private fun selectAllCategoriesOpenFiltersAndCheckSizes(sizes: List<String>) {
        filtersWorkflow.selectAllCategoriesAndOpenFilters()
        if (sizes.isNotEmpty()) {
            filtersRobot.assertSizesInFilters(sizes)
        }
    }

    fun checkSizes(selectedCatalogSizesTitles: List<String>, otherCatalogSizesTitles: List<String>) {
        if (selectedCatalogSizesTitles.isNotEmpty()) {
            filtersRobot.assertSizesInFilters(selectedCatalogSizesTitles)
        } else {
            filtersRobot.assertSizesAreNotSelectedInFilters(otherCatalogSizesTitles)
        }
    }

    fun checkSizesAfterKeywordSearch(otherCatalogSizesTitles: List<String>) {
        filtersRobot.assertSizesAreNotSelectedInFilters(otherCatalogSizesTitles)
    }

    private fun changeCatalogAndCheckSizes(catalogName: String, sizes: List<String>) {
        filtersWorkflow
            .selectAllCategoriesAndOpenFilters()
            .openCatalogsAndCategoriesScreen()
            .selectCategory(catalogName)
            .selectCategoryAll()
        if (sizes.isNotEmpty()) {
            filtersRobot.assertGivenOptionIsSelected(catalogName)
            filtersRobot.assertSizesInFilters(sizes)
        }
    }

    private fun checkSizesBeforeAndAfterSelectingAdditionalSize(catalogSizeTitles: List<String>, newSize: String): String {
        selectAllCategoriesOpenFiltersAndCheckSizes(catalogSizeTitles)
        filtersRobot.openSizesScreen()
            .selectSizeAndGoBackToFilters(newSize)
        val expectedSizes = catalogSizeTitles.removeListSurroundingsAndReturnString() + ", " + newSize
        filtersRobot.assertGivenOptionIsSelected(expectedSizes)
        return expectedSizes
    }

    private fun goThroughAllCatalogs(womenCatalogBlock: () -> Unit, menCatalogBlock: () -> Unit, babiesCatalogBlock: (() -> Unit)? = null) {
        for (catalog in portal.catalogs) {
            when (catalog) {
                VintedCatalogs.WOMEN -> {
                    womenCatalogBlock()
                }
                VintedCatalogs.MEN -> {
                    menCatalogBlock()
                }
                VintedCatalogs.BABIES -> {
                    babiesCatalogBlock?.let { it() }
                }
            }
        }
    }

    private fun openBrowseScreenAndTabForEachCatalog(womenCatalogBlock: () -> Unit, menCatalogBlock: () -> Unit, babiesCatalogBlock: (() -> Unit)? = null) {
        goThroughAllCatalogs(
            womenCatalogBlock = {
                deepLink.catalog.goToBrowseTab()
                    .openTab(Personalization.womenCategoryTitle)
                womenCatalogBlock()
            },
            menCatalogBlock = {
                deepLink.catalog.goToBrowseTab()
                    .openTab(Personalization.menCategoryTitle)
                menCatalogBlock()
            },
            babiesCatalogBlock = {
                deepLink.catalog.goToBrowseTab()
                    .openTab(Personalization.babiesCategoryTitle)
                babiesCatalogBlock?.let { it() }
            }
        )
    }

    @Step("Open personalization settings")
    fun openPersonalizationSettings(): PersonalizationRobot {
        navigationRobot
            .openProfileTab()
            .openPersonalizationScreen()
        return personalizationRobot
    }
}
