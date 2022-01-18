package robot.workflow

import RobotFactory.browseRobot
import RobotFactory.catalogRobot
import RobotFactory.filtersRobot
import RobotFactory.personalizationWorkflowRobot
import api.AssertApi
import api.data.models.VintedUser
import io.qameta.allure.Step
import robot.BaseRobot
import robot.browse.CatalogRobot
import robot.browse.FiltersRobot
import util.base.BaseTest
import util.values.ElementByLanguage
import util.values.Personalization
import util.values.Visibility

class FiltersWorkflow : BaseRobot() {

    @Step("Select all categories and open catalog filters")
    fun selectAllCategoriesAndOpenFilters(): FiltersRobot {
        browseRobot
            .openAllCategories()
            .openCatalogFilters()
        return FiltersRobot()
    }

    @Step("Select home decor material {material} and size {beddingSize}, assert expected filters and items are visible")
    fun selectHomeDecorMaterialAndSizeAndAssertExpectedResultsAreVisible(material: String, beddingSize: String) {
        selectMaterialAssertItIsVisibleInFiltersAndSearch(material)
        selectBeddingSizeAssertItIsVisibleInFiltersAndSearch(material, beddingSize)
        changeCategoryAssertMaterialAndBeddingSizeFiltersAreNotVisible(Personalization.womenCategoryTitle, beddingSize)
    }

    @Step("Select material {material}, assert it is visible in filters and search")
    private fun selectMaterialAssertItIsVisibleInFiltersAndSearch(material: String): FiltersWorkflow {
        filtersRobot
            .openMaterialsScreen()
            .selectMaterial(material)
            .clickBack()
        filtersRobot.assertGivenOptionIsSelected(material)
        clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(BaseTest.loggedInUser, material)
        return this
    }

    @Step("Select bedding size {beddingSize}, assert it is visible in filters and search")
    private fun selectBeddingSizeAssertItIsVisibleInFiltersAndSearch(material: String, beddingSize: String): FiltersWorkflow {
        catalogRobot.openCatalogFilters()
            .openSizesScreen()
            .assertBeddingSizesAreVisible()
            .selectSizeAndGoBackToFilters(beddingSize)
        filtersRobot.assertGivenOptionIsSelected(beddingSize)
        clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(BaseTest.loggedInUser, material, beddingSize)
        return this
    }

    @Step("Change filters category to {categoryName}, assert material and bedding size filters are not visible")
    private fun changeCategoryAssertMaterialAndBeddingSizeFiltersAreNotVisible(categoryName: String, beddingSize: String): FiltersWorkflow {
        catalogRobot.openCatalogFilters()
        personalizationWorkflowRobot
            .changeCatalogInFilters(categoryName)
            .assertMaterialSectionIsNotVisible()
            .assertGivenOptionIsNotSelected(beddingSize)
            .openSizesScreen()
            .assertSizesVisibility(ElementByLanguage.Size, Visibility.Visible)
        return this
    }

    @Step("Click show filter results and assert search with selected filter exists")
    fun clickShowFilterResultsAndAssertSearchWithSelectedFilterExists(
        user: VintedUser, vararg selectedOption: String, expectedNumberOfSearches: Int = 1, sleepTime: Long = 200
    ): CatalogRobot {
        filtersRobot.clickShowFilterResults()
        AssertApi.assertSearchWithSelectedFilterExists(
            user, *selectedOption, expectedNumberOfSearches = expectedNumberOfSearches, sleepTime = sleepTime
        )
        return catalogRobot
    }
}
