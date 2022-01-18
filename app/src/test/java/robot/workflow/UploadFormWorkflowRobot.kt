package robot.workflow

import RobotFactory.brandAuthenticationRobot
import RobotFactory.actionBarRobot
import RobotFactory.brandRobot
import RobotFactory.colorRobot
import RobotFactory.fullImageRobot
import RobotFactory.itemRobot
import RobotFactory.sizeRobot
import RobotFactory.uploadItemRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.Image
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.UploadItemRobot
import util.image.ImageFactory
import util.image.ImageRecognition
import util.values.ElementByLanguage
import util.values.Visibility

class UploadFormWorkflowRobot : BaseRobot() {

    @Step("Select No brand in upload form and assert No brand was selected")
    fun selectAndAssertNoBrandInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openBrandsSection()
            .selectNoBrand()
            .assertBrandIsVisible()
            .assertBrandNameMatches(ElementByLanguage.noBrandText)
        return this
    }

    @Step("Select {customBrand} in upload form and assert {customBrand} was selected")
    fun selectAndAssertCustomBrandInUploadForm(customBrand: String): UploadFormWorkflowRobot {
        uploadItemRobot
            .openBrandsSection()
            .searchBrand(customBrand)
            .assertCustomBrandSuggestionVisibility(Visibility.Visible, customBrand)
            .createCustomBrand()
            .assertBrandIsVisible()
            .assertBrandNameMatches(customBrand)
        return this
    }

    @Step("Select {brand} brand in upload form and assert {brand} was selected")
    fun selectGivenBrandInUploadForm(brand: String): UploadFormWorkflowRobot {
        uploadItemRobot
            .openBrandsSection()
            .searchBrand(brand)
            .selectBrand(brand)
        uploadItemRobot
            .assertBrandIsVisible()
            .assertBrandNameMatches(brand)
        return this
    }

    @Step("Select Category in upload form and assert Category was selected")
    fun selectAndAssertCategoryInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openCategoriesSection()
            .selectCategoryAndSubcategory(ElementByLanguage.getCategoriesAndSubcategories(0))
            .assertCategoryIsVisible()
        return this
    }

    @Step("Select Beauty category {subcategory} in upload form and assert Category was selected")
    fun selectAndAssertBeautyCategoryInUploadForm(subcategory: List<String>): UploadFormWorkflowRobot {
        uploadItemRobot
            .openCategoriesSection()
            .selectBeautySubcategory(subcategory)
            .assertCategoryIsVisible()
        return this
    }

    @Step("Select Category with No shipping in upload form and assert it was selected")
    fun selectAndAssertNoShippingCategoryInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openCategoriesSection()
            .selectCategoryAndSubcategoryThatHasNoShippingOption()
            .assertCategoryIsVisible()
        return this
    }

    @Step("Select First Size in upload form and assert size was selected")
    fun selectAndAssertFirstSizeInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openSizesSection()
        sizeRobot
            .clickOnSizesGuideLinkAndAssertWebViewIsVisible()
            .selectFirstSize()
            .assertSizeIsVisible(ElementByLanguage.Size)
        return this
    }

    @Step("Select First Condition in upload form and assert condition was selected")
    fun selectAndAssertFirstConditionInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openConditionsSection()
            .selectFirstCondition()
            .assertConditionIsVisible()
        return this
    }

    @Step("Select Colors in upload form and assert colors were selected")
    fun selectAndAssertColorsInUploadForm(): UploadFormWorkflowRobot {
        uploadItemRobot
            .openColorsSection()
            .selectColors()
            .assertColorsAreVisible()
        return this
    }

    @Step("Select Price in upload form and assert price was selected")
    fun selectAndAssertPriceInUploadForm(price: String, isUserAuthorized: Boolean = true): UploadFormWorkflowRobot {
        uploadItemRobot
            .openSellingPriceSection()
            .enterPriceAndSubmit(price)
        if (isUserAuthorized) {
            uploadItemRobot.assertPrice(price)
        }
        return this
    }

    @Step("Open, swipe and assert rearranged images")
    fun openSwipeAndAssertRearrangedImagesAndroid(expectedAndroidImages: List<Image>): UploadFormWorkflowRobot {
        itemRobot.clickOnImage()
        expectedAndroidImages.forEach { image ->
            VintedAssert.assertTrue(
                ImageRecognition.isImageInScreen(image, threshold = 0.35),
                "Image displayed should be ${image.name}"
            )
            if (expectedAndroidImages.last() != image) fullImageRobot.swipeLeftToNextImage()
        }
        return this
    }

    @Step("Open, swipe and assert rearranged images")
    fun openSwipeAndAssertRearrangedImagesIos(): UploadFormWorkflowRobot {
        itemRobot.clickOnImage()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_1_PHOTO_CROPPED, threshold = 0.35),
            "First photo should be pink sweater"
        )
        fullImageRobot.swipeLeftToNextImage()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.TIPS_IN_GRID, threshold = 0.33),
            "Second photo should be blazer from photo tips"
        )
        return this
    }

    @Step("Check if brand authentication modal is visible, close it and assert if notice is visible")
    fun checkBrandAuthenticityModalCloseItAndCheckNotice(): UploadFormWorkflowRobot {
        brandAuthenticationRobot
            .checkThatBrandAuthenticationModalIsVisible()
            .closeBrandAuthenticationModal()
        uploadItemRobot.assertBrandAuthenticityNoticeIsVisible()
        return this
    }

    @Step("Assert brands are suggested, brands names match {brands} and click on first brand")
    fun openBrandSectionAssertBrandsAreSuggestedAndChooseFirst(brands: List<String>): UploadFormWorkflowRobot {
        uploadItemRobot
            .openBrandsSection()
            .assertBrandsSuggestionsAreDisplayed(brands)
        brandRobot.selectBrand(brands.first())
        return this
    }

    @Step("Assert brand is selected and brand name matches {brand}")
    fun assertBrandIsSelectedAndNameMatches(brand: String): UploadFormWorkflowRobot {
        uploadItemRobot
            .assertBrandIsVisible()
            .assertBrandNameMatches(brand)
        return this
    }

    @Step("Assert colors are suggested, colors names match {colors} and click on first color")
    fun openColorsSectionAssertColorsAreSuggestedAndChooseFirst(colors: List<String>): UploadFormWorkflowRobot {
        uploadItemRobot
            .openColorsSection()
            .assertColorsSuggestionsAreDisplayed(colors)
        colorRobot
            .selectColor(colors.first())
        actionBarRobot.submitInColorScreen()
        return this
    }

    @Step("Assert color is selected and color name matches {color}")
    fun assertColorIsSelectedAndNameMatches(color: String): UploadFormWorkflowRobot {
        uploadItemRobot
            .assertColorsAreVisible()
            .assertColorNameMatches(color)
        return this
    }

    @Step("Assert sizes are suggested, sizes names match {sizes} and click on first size")
    fun openSizesSectionAssertSizesAreSuggestedAndChooseFirst(sizes: List<String>): UploadItemRobot {
        uploadItemRobot
            .openSizesSection()
            .assertSizeSuggestionsAreDisplayed(sizes)
        sizeRobot.selectSize(sizes.first())
        return uploadItemRobot
    }

    @Step("Open photo, rotate it, click done and assert photo is visible")
    fun openRotateAndAssertPhotoIsVisible(): UploadFormWorkflowRobot {
        uploadItemRobot
            .clickOnPhoto()
            .rotate()
            .clickDone()
        uploadItemRobot.assertPhotoIsVisible()
        return this
    }

    @Step("Select luxury brand and close authenticity modal")
    fun selectLuxuryBrandAndCloseAuthenticityModal(): UploadItemRobot {
        uploadItemRobot
            .openBrandsSection()
            .selectLuxuryBrand()
            .checkBrandAuthenticityModalCloseItAndCheckNotice()
        return uploadItemRobot
    }
}
