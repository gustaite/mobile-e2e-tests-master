package test.basic.item

import RobotFactory.browseRobot
import RobotFactory.catalogRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.fullImageRobot
import RobotFactory.itemRobot
import RobotFactory.navigationRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileRobot
import RobotFactory.workflowRobot
import commonUtil.testng.config.ConfigManager.portal
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.*
import util.image.ImageFactory
import util.image.ImageRecognition
import commonUtil.testng.config.VintedCountry.*
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.base.BaseTest

@RunMobile
@Feature("Item tests")
@LoginToMainThreadUser
class ItemTests : BaseTest() {

    @Test(description = "Check that user can swipe images in item screen")
    fun testSwipeRightWorksInItemScreen() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)
        VintedDriver.pullDownToRefresh()

        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_1_PHOTO_CROPPED, threshold = 0.33),
            "First photo should be visible in item"
        )
        itemRobot.swipeLeftToNextImage()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_2_PHOTO_CROPPED, threshold = 0.35),
            "Second photo should be visible in item"
        )
    }

    @Test(description = "Check that user can tap on image and see in full size and swipe to other")
    @TmsLink("138")
    fun testFullImageCanBeOpenAndUserCanSwipeToOtherOne() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot
            .clickOnImage()
            .assertFullScreenImageIsOpen()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_1_PHOTO_CROPPED, threshold = 0.35),
            "First photo should be visible in item"
        )
        fullImageRobot.swipeLeftToNextImage()
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_2_PHOTO_CROPPED, threshold = 0.35),
            "Second photo should be visible in item"
        )
    }

    @Test(
        description = """Check that short user info section is in item and it is clickable.
            Going back from user's profile user is back to item"""
    )
    fun testUserShortInfoIsClickableAndNavigatesBackToItem() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot.shortUserInfo
            .assertShortUserSectionIsVisible()
            .assertUsername(otherUser.username)
            .assertRatingIsVisible()
            .clickOnUserInfo()
        userProfileRobot.closetScreen.shortUserInfo
            .assertUsername(otherUser.username)
            .clickBack()
        itemRobot.assertItemTitle(item.title)
    }

    @Test(description = "Check that item name, size, brand, condition, price and (commented out location (for non payment countries)) are visible")
    fun testItemNameSizeBrandConditionPriceLocationAreVisible() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot
            .assertItemTitle(item.title)
            .assertItemSizeConditionBrand(item)
            .assertPrice(item.priceNumeric)
            .assertLocation(item.city)
    }

    @Test(description = "Check user and similar item tabs. Check that bundle button is visible in user items tab but not similar items tab")
    fun testTabsAndBundleButton() {
        ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot
            .assertSimilarAndUserItemsTabsAreVisible()
            .assertBundleButtonIsVisibleInUserItemsTab()
            .assertBundleButtonIsNotVisibleInSimilarItemsTab()
    }

    @Test(description = "'Ask' button leads to correct conversation")
    @TmsLink("89")
    fun testAskButtonLeadsToCorrectConversation() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot.clickItemMessageButton()
        conversationRobot
            .assertItemTitle(item.title)
            .assertItemPrice(item.priceNumeric)
        navigationRobot.assertNavigationBarNameText(otherUser.username)
    }

    @RunMobile(country = PAYMENTS, message = "Test only for payment countries")
    @Test(description = "Check that 'Buy' button leads to correct checkout")
    fun testBuyButtonLeadsToCorrectCheckout() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot
            .clickBuyButton()
            .assertItemPhotoIsVisibleInCheckout()
    }

    @Test(description = "Favorite item from item screen and check heart color. Unfavorite it from favorite items screen")
    @TmsLink("88")
    fun testItemFavoring() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 2)
        deepLink.item.goToItem(item)

        itemRobot
            .assertHeartIconIsWhite()
            .clickFavoriteButton()
            .assertHeartIconIsRed()
        deepLink.setting
            .goToFavorites()
            .assertItemPhoto()
            .assertHeartIconIsRed()
            .unfavoriteItemAndRefresh()
            .assertEmptyStateTitleIsVisible()
        deepLink.item.goToItem(item)
        itemRobot.assertHeartIconIsWhite()
    }

    @Test(description = "Check description, category, size, colors are displayed and seen, uploaded and interested are shown at all")
    fun testItemDescriptionAndMoreSection() {
        val description = "Item with very interesting description"
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 1, description = description)
        deepLink.item.goToItem(item)

        itemRobot
            .assertDescription(description)
            .clickMore()
            .assertCategory(item.catalogId)
            .assertColor("${item.color1}, ${item.color2}")
            .assertSize(item.size)
            .assertViewCountIsNotEmpty()
            .assertUploadDateIsNotEmpty()
            .assertInterestedUsersCountIsNotEmpty()
    }

    @Test(description = "Check that user can click on size and see sizes web view")
    @TmsLink("25351")
    fun testThatItemSizeIsClickableAndSizeWebViewIsOpen() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 1)
        deepLink.item.goToItem(item)

        itemRobot
            .clickMore()
            .clickOnSize()
            .assertSizesWebViewIsVisible()
    }

    @Test(description = "Check if hashtag is clickable and if it opens catalog")
    @TmsLink("71")
    fun testHashtagInItemDescription() {
        val description = ""
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, description = description)
        deepLink.item.goToItemEditing(item)
        uploadItemRobot
            .enterAndAssertDescriptionWithHashtag()
            .clickSaveAndWait()
        workflowRobot.navigateToUploadedItem(item)
        itemRobot.clickOnItemDescription()
        catalogRobot.assertCatalogLayoutIsVisible()
    }

    @Test(description = "Check if link is clickable in item description")
    @TmsLink("72")
    fun testLinkInItemDescription() {
        val description = "description " + portal.mobile.scheme + "://catalog"
        val item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, description = description)
        deepLink.item.goToItem(item)
        itemRobot.clickOnItemDescription()
        browseRobot.assertBrowseTabIsDisplayed()
    }

    @Test(description = "Check if item category can be opened in catalog")
    fun testOpeningItemCategoryInCatalog() {
        val description = "description"
        val item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, description = description)
        deepLink
            .item.goToItem(item)
            .clickMore()
        val categoryName = itemRobot.returnCategoryName(item.catalogId)
        itemRobot
            .clickOnItemCategory()
            .assertCatalogLayoutIsVisible()
            .openCatalogFilters()
            .assertGivenOptionIsSelected(categoryName)
    }

    @Test(description = "Check if item seller block is opened after click for correct user")
    fun testItemSellerInfoBlockIsOpenedAfterClick() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 1)
        deepLink
            .item.goToItem(item)
            .clickOnSellerInfoBlock()
            .userInfo.assertUsername(otherUser.username)
    }

    @RunMobile(country = PAYMENTS)
    @Test(description = "Check if Refunds & Security Policy section is visible")
    fun testRefundsSecurityPolicyIsVisible() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, photoCount = 1)
        deepLink
            .item.goToItem(item)
            .assertBuyerProtectionCellIsVisible()
    }
}
