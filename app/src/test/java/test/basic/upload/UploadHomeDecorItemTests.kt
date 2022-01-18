package test.basic.upload

import RobotFactory.categoriesRobot
import RobotFactory.deepLink
import RobotFactory.itemRobot
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import RobotFactory.workflowRobot
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedCountry
import util.values.ElementByLanguage.Companion.categoryHomeAccessoriesItemText
import util.values.ElementByLanguage.Companion.categoryHomeTablewareItemText
import util.values.ElementByLanguage.Companion.categoryHomeTextilesItemText
import io.qameta.allure.TmsLink
import commonUtil.testng.mobile.RunMobile
import util.values.ElementByLanguage.Companion.materialCeramicText
import util.values.ElementByLanguage.Companion.materialMetalText
import util.values.ElementByLanguage.Companion.materialTextileAcrylicText

@LoginToMainThreadUser
@RunMobile(country = VintedCountry.INT, message = "Tests for INT country which has home decor category")
@Feature("Upload home decor item tests")
class UploadHomeDecorItemTests : BaseTest() {

    @Test(description = "Upload a book in home decor category")
    @TmsLink("287")
    fun testUploadABookInHomeDecor() {
        deepLink.item.goToUploadForm()
        val isbn = "9780545010221"
        val author = "J. K. Rowling"
        val title = "Harry Potter And The Deathly Hallows (book 7)"

        uploadItemRobot
            .selectDefaultPhotoTitleDescriptionValuesOnItemUpload()
            .openCategoriesSection()
            .selectHomeDecorBooksSubcategory()
            .assertCategoryIsVisible()
            .assertBrandsSizesAndColorsSectionsAreNotVisible()
            .openISBNSection()
            .enterISBNAndSubmit(isbn)
            .assertISBNIsVisible()
            .assertAuthorIsVisible(author)
            .assertTitleIsVisible(title)
            .selectAndAssertFirstConditionInUploadForm()

        workflowRobot.selectDefaultPriceShippingValuesOnItemUploadAndGoToItem()

        itemRobot
            .clickMore()
            .assertISBN(isbn)
            .assertAuthor(author)
            .assertTitle(title)
    }

    @Test(description = "Upload a textile in home decor category")
    @TmsLink("288")
    fun testUploadATextileInHomeDecor() {
        deepLink.item.goToUploadForm()
        uploadItemRobot
            .selectDefaultPhotoTitleDescriptionValuesOnItemUpload()
            .openCategoriesSection()
            .selectHomeDecorSubcategory(categoryHomeTextilesItemText)
            .assertCategoryIsVisible()
        uploadFormWorkflowRobot
            .selectAndAssertNoBrandInUploadForm()
        uploadItemRobot
            .openSizesSection()
            .selectFirstHomeDecorItemSize()
            .assertHomeDecorTextileSizeIsVisible()
            .openMaterialsSection()
            .selectFirstMaterial()
            .assertMaterialIsVisible()
            .openConditionsSection()
            .assertOnlyNewWithTagsConditionIsVisible()
            .selectFirstCondition()
            .assertConditionIsVisible()
        uploadFormWorkflowRobot
            .selectAndAssertColorsInUploadForm()

        workflowRobot.selectDefaultPriceShippingValuesOnItemUploadAndGoToItem()

        itemRobot
            .clickMore()
            .assertMaterial(materialTextileAcrylicText)
    }

    @Test(description = "Test material and size fields for home decor subcategories")
    @TmsLink("288")
    fun testMaterialAndSizeFieldsInHomeDecor() {
        deepLink.item.goToUploadForm()
        uploadItemRobot
            .openCategoriesSection()
            .selectHomeDecorSubcategory(categoryHomeAccessoriesItemText)
            .assertCategoryIsVisible()
            .assertSizeSectionIsNotVisible()
            .openConditionsSection()
            .assertMoreThanOneConditionIsVisible()
            .selectFirstCondition()
            .openMaterialsSection()
            .selectMaterial(materialMetalText)
            .assertMaterialIsVisible()
            .openCategoriesSection()
        categoriesRobot.selectHomeDecorSubcategory(categoryHomeTablewareItemText)
            .assertCategoryIsVisible()
            .assertColorsAndSizesSectionsAreNotVisible()
            .openConditionsSection()
            .assertMoreThanOneConditionIsVisible()
            .selectFirstCondition()
            .openMaterialsSection()
            .selectMaterial(materialCeramicText)
            .assertMaterialIsVisible()
    }
}
