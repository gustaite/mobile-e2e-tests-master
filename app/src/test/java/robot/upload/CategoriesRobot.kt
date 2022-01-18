package robot.upload

import RobotFactory.uploadItemRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import robot.browse.FiltersRobot
import util.EnvironmentManager.isAndroid
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage

class CategoriesRobot : BaseRobot() {

    private val categoryFirstItem: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_cell_body_container"),
            // ToDo maybe we should have accessibilityIdentifier here?
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS 'catalog_0'")
        )

    private val allCategoriesElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "view_search_catalog_category_leaf_suffix",
            "catalog_filter_catalog_all"
        )

    private fun categoryTitleElement(categoryName: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidText(categoryName),
            iOSBy = VintedBy.iOSTextByBuilder(text = categoryName, searchType = Util.SearchTextOperator.EXACT)
        )

    @Step("Select {categoriesList} category and subcategory")
    fun selectCategoryAndSubcategory(categoriesList: List<String>): UploadItemRobot {
        selectCategoriesFromList(categoriesList)
        return uploadItemRobot
    }

    @Step("Select {categoriesList} category and subcategory in filters and browse screen")
    fun selectCategoryAndSubcategoryInFiltersAndBrowseScreen(categoriesList: List<String>) {
        selectCategoriesFromList(categoriesList)
    }

    @Step("Select category and subcategory which has no shipping option")
    fun selectCategoryAndSubcategoryThatHasNoShippingOption(): UploadItemRobot {
        val list = ElementByLanguage.categoryKidsNoShippingItemText
        selectCategoryWorkflow(list, repeatTime = 1)
        return uploadItemRobot
    }

    @Step("Select home decor books subcategory")
    fun selectHomeDecorBooksSubcategory(): UploadItemRobot {
        val list = ElementByLanguage.categoryHomeBooksItemText
        selectCategoryWorkflow(list)
        return uploadItemRobot
    }

    @Step("Select home decor subcategory")
    fun selectHomeDecorSubcategory(selectedCategoryList: List<String>): UploadItemRobot {
        selectCategoryWorkflow(selectedCategoryList)
        return uploadItemRobot
    }

    @Step("Select beauty subcategory")
    fun selectBeautySubcategory(selectedCategoryList: List<String>): UploadItemRobot {
        val repeatTime = if (isAndroid) 2 else 1
        selectCategoryWorkflow(selectedCategoryList, repeatTime)
        return uploadItemRobot
    }

    private fun selectCategoryWorkflow(list: List<String>, repeatTime: Int = 2) {
        commonUtil.reporting.Report.addMessage("$list")
        if (list.isEmpty()) {
            VintedAssert.fail("this portal: $portal has no such category or it is not implemented")
        }
        selectCategoriesFromList(list)
        repeat(repeatTime) { categoryFirstItem.click() }
    }

    private fun selectCategoriesFromList(categoriesList: List<String>) {
        var previousCategory = categoriesList[0]
        for (category in categoriesList) {
            sleepWithinStep(300)
            try {
                categoryTitleElement(category).click()
            } catch (e: NullPointerException) {
                commonUtil.reporting.Report.addMessage("category $category was not found")
                commonUtil.reporting.Report.addMessage("Click the category $previousCategory for the second time")
                VintedDriver.findElementByText(previousCategory).tap()
            }
            previousCategory = category
        }
    }

    @Step("Select category {selectedCategory}")
    fun selectCategory(selectedCategory: String): CategoriesRobot {
        categoryTitleElement(selectedCategory).click()
        return this
    }

    @Step("Select category all")
    fun selectCategoryAll(): FiltersRobot {
        VintedAssert.assertTrue(allCategoriesElement.isVisible(), "AllCategoriesElement should be visible")
        allCategoriesElement.click()
        return FiltersRobot()
    }
}
