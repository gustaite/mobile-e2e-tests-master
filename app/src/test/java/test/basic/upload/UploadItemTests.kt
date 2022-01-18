package test.basic.upload

import RobotFactory.deepLink
import RobotFactory.fullImageRobot
import RobotFactory.galleryRobot
import RobotFactory.itemRobot
import RobotFactory.navigationRobot
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileClosetRobot
import RobotFactory.userProfileRobot
import RobotFactory.workflowRobot
import api.AssertApi
import commonUtil.testng.config.ConfigManager.platform
import api.controllers.item.*
import commonUtil.asserts.VintedAssert
import commonUtil.data.Price
import commonUtil.extensions.logList
import commonUtil.testng.LoginToMainThreadUser
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.upload.ShippingOptionTypes
import util.Android
import util.base.BaseTest
import util.IOS
import util.image.AssertImage.Companion.assertImageIsInScreen
import util.image.AssertImage.Companion.assertImageIsNotInScreen
import util.image.ImageFactory
import util.image.ImageFactory.Companion.ITEM_1_PHOTO_CROPPED
import util.image.ImageFactory.Companion.ITEM_2_PHOTO_CROPPED
import util.image.ImageRecognition
import util.image.ImageUtil
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import util.EnvironmentManager.isAndroid
import util.absfeatures.AbTestController.isBrandsLookupUsingElasticSearchOn
import util.image.ImageFactory.Companion.ITEM_1_PHOTO
import util.image.ImageFactory.Companion.ITEM_2_PHOTO
import util.values.LaboratoryDevice

@RunMobile
@Feature("Upload item tests")
@LoginToMainThreadUser
class UploadItemTests : BaseTest() {

    @BeforeMethod(description = "Delete user's items")
    fun beforeTest() {
        loggedInUser.deleteAllItems()
    }

    private val price = Price.getRandomPriceInRange()
    private val images = if (isAndroid) listOf(ITEM_1_PHOTO, ITEM_2_PHOTO) else emptyList()
    private val croppedImages = if (isAndroid) listOf(ITEM_1_PHOTO_CROPPED, ITEM_2_PHOTO_CROPPED) else emptyList()

    @Test(description = "Upload item with standard shipping")
    @TmsLink("69")
    fun testItemUploadWithStandardShipping() {
        deepLink.item.goToUploadForm()

        uploadItemRobot
            .selectDefaultValuesOnItemUploadWithPhoto(price)
            .clickSaveAndWait()

        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .assertItemsIsVisibleInCloset(1, price)
    }

    @Test(description = "Check if keyboard opens correctly after clicking on title and description")
    @TmsLink("25088")
    fun testIfKeyboardOpensOnTitleAndDescription() {
        deepLink.item.goToUploadForm()

        uploadItemRobot.clickTitle()
        workflowRobot.sleepAndCheckIfKeyboardIsOpenAndCloseIt()

        uploadItemRobot.clickDescription()
        workflowRobot.sleepAndCheckIfKeyboardIsOpenAndCloseIt()
    }

    @RunMobile(platform = VintedPlatform.ANDROID)
    @Test(description = "Test item upload after failing validation")
    fun testItemUploadAfterFailingValidation() {
        deepLink.item.goToUploadForm()

        uploadItemRobot.clickSaveAndWait()
        Android.scrollUpABit()
        uploadItemRobot
            .selectDefaultValuesOnItemUploadWithPhoto(price)
            .clickSaveAndWait()

        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .assertItemsIsVisibleInCloset(1, price)
    }

    @RunMobile(country = VintedCountry.PAYMENTS, message = "Test for payment countries")
    @Test(description = "Upload item with custom shipping. Only for payments countries")
    @TmsLink("25670")
    fun testItemUploadWithCustomShipping() {
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        deepLink.item.goToItemEditing(item)

        val shippingOptionRobot = uploadItemRobot.openShippingOptionsSection()

        val selectedTitle = shippingOptionRobot
            .selectShippingOptionAndReturnTitle(ShippingOptionTypes.CUSTOM)

        shippingOptionRobot
            .setPriceForCustomShipping("5.70", "10.00")
            .clickSubmitInParcelSizeScreen()
            .assertShippingOption(selectedTitle)
            .openShippingOptionsSection()
            .assertCustomPrice("5.70", "10.00")
            .clickSubmitInParcelSizeScreen()

        uploadItemRobot.clickSaveAndWait()
        workflowRobot.navigateToUploadedItem(item)

        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .assertItemsIsVisibleInCloset(1, item.priceNumeric)
    }

    @Test(description = "Create item with rotated photo")
    @TmsLink("19593")
    fun testCreateItemWithRotatedPhoto() {
        ImageUtil.uploadImageToDeviceOnceInDay(listOf(ImageFactory.CAT))
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 0)
        deepLink.item.goToItemEditing(item)

        uploadItemRobot.clickAddFirstPhoto()
        galleryRobot.selectOneSpecificPictureInGallery()

        assertImageIsInScreen(ImageFactory.CROPPED_CAT, threshold = 0.40)

        uploadFormWorkflowRobot.openRotateAndAssertPhotoIsVisible()

        assertImageIsInScreen(ImageFactory.CROPPED_ROTATED_CAT, threshold = 0.5)
        assertImageIsNotInScreen(ImageFactory.CROPPED_CAT, retryCount = 0, threshold = 0.6)

        uploadItemRobot.clickSaveAndWait()
        workflowRobot.navigateToProfile()

        userProfileRobot.closetScreen
            .assertItemsIsVisibleInCloset(1, item.priceNumeric)

        val itemList = loggedInUser.getItemsWithDrafts()
        deepLink.item.goToItem(itemList.first())

        assertImageIsInScreen(ImageFactory.CROPPED_ROTATED_CAT, threshold = 0.5)
        assertImageIsNotInScreen(ImageFactory.CROPPED_CAT, retryCount = 0, threshold = 0.8)
    }

    @Test(description = "Test that draft properly converts to item")
    @TmsLink("5228")
    fun testThatDraftConvertsToItem() {
        deepLink.item.goToUploadForm()
        uploadItemRobot
            .selectDefaultValuesOnItemUploadWithPhoto(price)
            .saveDraft()
        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .editDraft()
            .clickSaveAndWait()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.getItems().logList().size },
            expected = 1,
            errorMessage = "Item list should contain one item",
            retryCount = 120,
            sleepTime = 500
        )
    }

    @Test(description = "Test that draft is discarded")
    @TmsLink("5228")
    fun testThatDraftIsDiscarded() {
        deepLink.item.goToUploadForm()

        uploadItemRobot
            .selectTitleDescriptionValuesOnItemUpload()
            .discardDraft()
        deepLink.profile.goToMyProfile()
        userProfileClosetRobot.assertClosetIsEmpty()

        AssertApi.assertApiResponseWithWait(
            actual = { loggedInUser.getItemsWithDrafts().logList().size },
            expected = 0,
            errorMessage = "Item list should contain no items or drafts",
            retryCount = 120,
            sleepTime = 500
        )
    }

    @Test(description = "Test if user can create a custom brand if search result does not match any brand")
    @TmsLink("25353")
    @RunMobile(neverRunOnSandbox = true)
    fun testItemUploadWithCustomBrandCreation() {
        var customBrand = "Testuoklis$platform"

        if (isBrandsLookupUsingElasticSearchOn()) {
            customBrand += System.currentTimeMillis().toString()
        }

        deepLink.item.goToUploadForm()
        uploadItemRobot.selectDefaultPhotoTitleDescriptionValuesOnItemUpload()
        uploadFormWorkflowRobot
            .selectAndAssertCategoryInUploadForm()
            .selectAndAssertCustomBrandInUploadForm(customBrand)
            .selectAndAssertFirstSizeInUploadForm()
            .selectAndAssertFirstConditionInUploadForm()
            .selectAndAssertColorsInUploadForm()
        workflowRobot.selectDefaultPriceShippingValuesOnItemUploadAndGoToItem()
        itemRobot.assertItemBrand(customBrand)
    }

    @Test(description = "Check that user can rearrange pictures while uploading in upload form")
    @TmsLink("18947")
    fun testRearrangePicturesInUploadViewWhenUploading() {
        ImageUtil.uploadImagesToDevice(images)

        deepLink.item
            .goToUploadForm()
            .clickAddFirstPhoto()
        galleryRobot
            .assertFirstSelectableImageIsExpectedOneAndroid(images)
            .selectFirstTwoExpectedImagesInGallery()
            .swipeRightInPhotoCarousel(listOf(LaboratoryDevice.S6_EDGE))
            .rearrangePhotosInUploadForm()
            .selectDefaultValuesOnItemUploadWithoutPhoto(price)
            .clickSaveAndWait()

        deepLink.profile.goToMyProfile()
            .openFirstItem()

        Android.doIfAndroid {
            uploadFormWorkflowRobot.openSwipeAndAssertRearrangedImagesAndroid(expectedAndroidImages = croppedImages.reversed())
        }
        IOS.doIfiOS {
            uploadFormWorkflowRobot.openSwipeAndAssertRearrangedImagesIos()
        }
    }

    @Test(description = "Check that user can rearrange pictures in draft edit while uploading in upload form")
    @TmsLink("18947")
    fun testRearrangePicturesInUploadViewWhenEditingDraft() {
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 0)
        ImageUtil.uploadImagesToDevice(images)

        deepLink.item
            .goToItemEditing(item)
            .clickAddFirstPhoto()
        galleryRobot
            .assertFirstSelectableImageIsExpectedOneAndroid(croppedImages)
            .selectFirstTwoExpectedImagesInGallery()
            .rearrangePhotosInUploadForm()
            .clickSaveAndWait()

        workflowRobot.navigateToUploadedItem(item)

        Android.doIfAndroid {
            uploadFormWorkflowRobot.openSwipeAndAssertRearrangedImagesAndroid(expectedAndroidImages = croppedImages.reversed())
        }
        IOS.doIfiOS {
            uploadFormWorkflowRobot.openSwipeAndAssertRearrangedImagesIos()
        }
    }

    @Test(description = "Check that user can rearrange pictures in item edit in upload")
    @TmsLink("18947")
    fun testRearrangePicturesInUploadViewWhenEditingItem() {
        val item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)

        deepLink.item.goToItemEditing(item)
        uploadItemRobot.rearrangePhotosInUploadForm()
        uploadItemRobot
            .clickSaveAndWait()
        workflowRobot.navigateToUploadedItem(item)

        itemRobot.clickOnImage()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ITEM_2_PHOTO_CROPPED, threshold = 0.33),
            "First photo should be glasses"
        )
        IOS.doIfiOS {
            fullImageRobot.swipeLeftToNextImage()
            VintedAssert.assertTrue(
                ImageRecognition.isImageInScreen(ITEM_1_PHOTO_CROPPED, threshold = 0.35),
                "Second photo should be pink sweater"
            )
        }
    }

    @Test(description = "Check that images are removed in item draft and edit")
    @TmsLink("5226")
    fun testDeletePhotos() {
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItemEditing(item)
        uploadItemRobot
            .clickAddPhoto()
            .selectPhotosFromGallery(2)
            .removeSelectedPhotosAmount(3)
            .clickSaveAndWait()
        workflowRobot.navigateToUploadedItem(item)

        AssertApi.assertApiResponseWithWait(
            actual = {
                loggedInUser.getItemInformation(item).photos.count()
                    .also { commonUtil.reporting.Report.addMessage("Photos count was: $it") }
            },
            expected = 1,
            errorMessage = "Item should contain 1 photo but did not"
        )

        deepLink.item.goToItemEditing(item)
            .removeOnlyOnePhoto(0)
            .clickSaveAndWait()
        uploadItemRobot.validationRobot.assertValidationVisibleOnPhoto()
    }
}
