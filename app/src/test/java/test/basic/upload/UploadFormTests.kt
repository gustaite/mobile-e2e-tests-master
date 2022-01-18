package test.basic.upload

import RobotFactory.cameraRobot
import RobotFactory.deepLink
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import RobotFactory.uploadPhotoActionRobot
import RobotFactory.userProfileRobot
import commonUtil.extensions.removeListSurroundingsAndReturnString
import commonUtil.testng.LoginToMainThreadUser
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.image.AssertImage
import util.image.ImageFactory
import commonUtil.testng.config.VintedCountry.*
import commonUtil.testng.config.VintedPlatform.ANDROID
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import robot.upload.photo.PictureNumber
import util.values.ElementByLanguage.Companion.threeBrands
import util.values.ElementByLanguage.Companion.threeSizes
import util.values.LaboratoryDevice
import util.values.Visibility

@RunMobile
@Feature("Upload form tests")
@LoginToMainThreadUser
class UploadFormTests : BaseTest() {

    @BeforeMethod
    fun beforeTest() {
        deepLink.item.goToUploadForm()
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Take photos from camera, add photos from gallery and assert that user can add 20 photos")
    fun testUploadPhotoFromCameraAndGallery() {
        uploadItemRobot
            .clickAddFirstPhoto()
            .selectPhotosFromGallery(18)
            .clickAddPhoto()
            .openCameraAndTakeAdditionalPhoto()
            .clickAddPhoto()
            .selectPhotosFromGallery(1)
            .assertAddButtonVisibility(false)
    }

    @Test(description = "Add photos from gallery and assert that user can add at least 6 photos")
    fun testUploadPhotoFromGallery() {
        uploadItemRobot
            .clickAddFirstPhoto()
            .selectPhotosFromGallery(6)
            .assertAddButtonVisibility(true)
    }

    @RunMobile(country = PAYMENTS, message = "Test for payment countries only")
    @Test(description = "Test if no shipping option is available")
    @TmsLink("27907")
    fun testNoShippingOption() {
        uploadItemRobot
            .assertShippingOptionVisibility(Visibility.Invisible)
            .selectAndAssertNoShippingCategoryInUploadForm()

        val shippingOptionRobot = uploadItemRobot.openShippingOptionsSection()

        uploadItemRobot
            .assertShippingOption(shippingOptionRobot.selectNoShippingOptionAndReturnTitle())
    }

    @Issue("MARIOS-692")
    @Test(description = "Test if brand and size suggestions from description works in upload form")
    @TmsLink("73")
    fun testSuggestionsFromDescription() {
        val brands = threeBrands
        val sizes = threeSizes
        val description = brands.removeListSurroundingsAndReturnString() + " " + sizes.removeListSurroundingsAndReturnString()

        deepLink
            .item.goToUploadForm()
            .enterAndAssertDescription(description)
        uploadFormWorkflowRobot
            .selectAndAssertCategoryInUploadForm()
            .openBrandSectionAssertBrandsAreSuggestedAndChooseFirst(brands)
            .assertBrandIsSelectedAndNameMatches(brands.first())
            .openSizesSectionAssertSizesAreSuggestedAndChooseFirst(sizes)
            .assertSizeIsVisible(sizes.first())
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Test if item form validation messages are visible")
    fun testErrorValidationMessagesAreVisibleOnItemUploadForm() {
        uploadItemRobot.clickSaveAndWait()
        uploadItemRobot.validationRobot
            .assertValidationVisibleOnPhotoTitleDescription()
            .assertValidationVisibleOnCategoryBrandConditionPrice()
        uploadFormWorkflowRobot
            .selectAndAssertCategoryInUploadForm()
        uploadItemRobot.validationRobot
            .assertValidationVisibleOnSizeColor()
            .assertValidationVisibleOnParcelSize()
    }

    @Test(description = "Test if category suggestion from image works in upload form")
    @TmsLink("19")
    fun testThatCategoriesAreSuggested() {
        uploadItemRobot
            .uploadPhotoAndAssertVisibilityInUploadForm()
            .selectTitleDescriptionValuesOnItemUpload()
            .openCategorySectionAndAssertCategorySuggested()
            .selectSuggestedCategoryAndAssertVisibilityInUploadForm()
            .saveDraft()
        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .editDraft()
            .assertCategoryIsVisible()
            .openCategorySectionAndAssertCategorySuggestedFromEdit()
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Test if pictures can be rearranged in camera view")
    @TmsLink("18947")
    fun testRearrangePicturesInCameraView() {
        uploadItemRobot
            .clickAddFirstPhoto()
            .selectPhotoFromGalleryByImageRecognition(image = ImageFactory.TIPS_IN_GRID)
            .clickAddPhoto()
            .selectPhotoFromGalleryByImageRecognition(image = ImageFactory.CAT_IN_GRID)
            .swipeRightInPhotoCarousel(listOf(LaboratoryDevice.A50, LaboratoryDevice.S20_FE))
        cameraRobot
            .rearrangePhotosInCameraView()
            .clickDone()

        AssertImage.assertImageIsInScreen(ImageFactory.CROPPED_CAT, threshold = 0.47)
        AssertImage.assertImageIsInScreen(ImageFactory.TIPS_IN_GRID, threshold = 0.5)
        uploadItemRobot
            .removeOnlyOnePhoto(1)
        AssertImage.assertImageIsInScreen(ImageFactory.CROPPED_CAT, threshold = 0.47)
        AssertImage.assertImageIsNotInScreen(ImageFactory.TIPS_IN_GRID, threshold = 0.5)
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Test if information modal about canceling captured photos is visible")
    fun testInformationModalAboutCancelingCapturedPhotos() {
        uploadItemRobot
            .clickAddFirstPhoto()
            .openCameraAndTakePhoto(PictureNumber.FIRST)
            .clickBackInCameraViewAndAssertModalVisibility()
            .clickNoInCapturedPhotoCancellationModal()
            .assertInformationAboutCancelingTakenPhotosVisibility(visibility = Visibility.Invisible)
            .clickBackInCameraViewAndAssertModalVisibility()
            .closeModal()
        cameraRobot.assertInformationAboutCancelingTakenPhotosVisibility(visibility = Visibility.Invisible)
        uploadPhotoActionRobot.assertTakePhotoImageIsVisible()
    }
}
