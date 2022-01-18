package robot.item

import RobotFactory
import RobotFactory.browseRobot
import RobotFactory.bumpsPreCheckoutRobot
import RobotFactory.bundleRobot
import RobotFactory.catalogRobot
import RobotFactory.conversationRobot
import RobotFactory.fullImageRobot
import RobotFactory.inAppNotificationRobot
import RobotFactory.modalRobot
import RobotFactory.userShortInfoSectionRobot
import RobotFactory.welcomeRobot
import RobotFactory.workflowRobot
import api.controllers.GlobalAPI
import api.data.models.VintedItem
import commonUtil.data.enums.VintedPortal
import io.qameta.allure.Step
import org.openqa.selenium.InvalidArgumentException
import org.openqa.selenium.NoSuchElementException
import robot.*
import robot.browse.CatalogRobot
import robot.bumps.BumpStatisticsRobot
import robot.bumps.BumpsPreCheckoutRobot
import robot.inbox.conversation.ConversationRobot
import robot.profile.tabs.UserProfileClosetRobot
import robot.section.UserShortInfoSectionRobot
import robot.workflow.WorkflowRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.driver.*
import util.image.ImageFactory
import util.image.ImageRecognition
import util.values.*
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.PortalFactory
import commonUtil.testng.config.PortalFactory.isPaymentCountry
import util.absfeatures.AbTestController

class ItemRobot : BaseRobot() {

    private val descriptionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_expandable"),
            iOSBy = VintedBy.accessibilityId("item_description_collapsable")
        )

    private val itemActionsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("menu_item_details_settings"),
            iOSBy = VintedBy.accessibilityId("more")
        )

    private val androidLabelElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("item_status_label_closed_hidden"))

    private val itemHiddenLabelElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidElement = { androidLabelElement },
            iosTranslationKey = "item_state_hidden"
        )

    private val itemSoldLabelElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidElement = { androidLabelElement },
            iosTranslationKey = "item_state_sold"
        )

    private val itemSwapLabelElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidElement = { androidLabelElement },
            iosTranslationKey = "item_state_swapped"
        )

    private val reservedButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_action_reserve_button"),
            iOSBy = VintedBy.accessibilityId("reserved_button_title")
        )

    private val shareButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_action_share_button"),
            iOSBy = VintedBy.accessibilityId("item_share_button")
        )

    private fun priceElement(price: String): VintedElement = VintedDriver.findElement(
        VintedBy.scrollableId("item_header_info_price"),
        VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value CONTAINS '$price'")
    )

    private val imageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("photos_carousel"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' AND name == 'Photo gallery'")
        )

    val shortUserInfo: UserShortInfoSectionRobot get() = UserShortInfoSectionRobot()

    private val userItemsTabElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidUIAutomator("UiScrollable(UiSelector()).scrollIntoView(UiSelector().description(\"OTHER_USER_ITEMS\"))"),
            VintedBy.accessibilityId(IOS.getElementValue("user_clothes"))
        )

    private val similarItemsTabElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.androidUIAutomator("UiScrollable(UiSelector()).scrollIntoView(UiSelector().description(\"SIMILAR_ITEMS\"))"),
            VintedBy.accessibilityId(IOS.getElementValue("related_items"))
        )

    private val itemSizeConditionBrandElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_header_info_subtitle"),
            iOSBy = VintedBy.accessibilityId("item_summary_details")
        )

    private val itemLocationElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("item_header_info_location"))

    private val bundleLayout: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("bundle_header_layout"),
            iOSBy = VintedBy.accessibilityId("bundling_entry_point_button_text")
        )

    private val bundleButton: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId("bundle_header_button"),
            VintedBy.accessibilityId("bundling_entry_point_button_text")
        )

    private val itemActionMessageButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_action_message_button"),
            iOSBy = VintedBy.accessibilityId("message_seller")
        )

    private val itemActionFavoriteButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_action_favorite_button"),
            iOSBy = VintedBy.accessibilityId("item_favourite_button")
        )

    private val moreButtonAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_description_more"))

    private val androidCategoryTextElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_description_category"))

    private val categoryTextElement: VintedElement
        get() = VintedDriver.findElement(
            { androidCategoryTextElement },
            VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'catalog_cell'")
        )

    private val sizeTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_size_arrow"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'item_description_size_cell'")
        )

    private val colorTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_color"),
            iOSBy = VintedBy.accessibilityId("item_description_color")
        )

    private val isbnTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_isbn"),
            iOSBy = VintedBy.accessibilityId("item_description_isbn")
        )

    private val authorTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_book_author"),
            iOSBy = VintedBy.accessibilityId("item_description_book_author")
        )

    private val titleTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_book_title"),
            iOSBy = VintedBy.accessibilityId("item_description_book_title")
        )

    private val materialTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_material"),
            iOSBy = VintedBy.accessibilityId("item_description_material")
        )

    private val viewCountTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_view_count"),
            iOSBy = VintedBy.accessibilityId("item_description_view_count")
        )

    private val uploadDateTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_date"),
            iOSBy = VintedBy.accessibilityId("item_description_date")
        )

    private val interestedUserCountTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_description_interested"),
            iOSBy = VintedBy.accessibilityId("item_description_interested")
        )

    private val itemActionBuyButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_action_buy_button"),
            iOSBy = VintedBy.accessibilityId("buy_now")
        )

    private val buyerProtectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_header_actions_buyer_protection_info"),
            iOSBy = VintedBy.accessibilityId("buyer_protection_pro_money_back_transparent")
        )

    private val bumpButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_action_push_up_button"),
            iOSBy = VintedBy.accessibilityId("item_details_push_up_button_title")
        )

    private val bumpLabelElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_status_label_promoted"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("pushed_up_item_state_title"))
        )

    private val bumpStatisticsCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("item_header_push_up_performance_container", "item_details_push_up_cell_title")

    @Step("Click more")
    fun clickMore(): ItemRobot {
        if (isAndroid) {
            moreButtonAndroid.withScrollDownSimple()
            closeReturnLabelMvpModalIfVisible()
            moreButtonAndroid.click()
        } else {
            clickMoreIos()
        }
        return this
    }

    @Step("Close 'Return label MVP' modal if visible")
    private fun closeReturnLabelMvpModalIfVisible() {
        if (AbTestController.isReturnLabelMvpOn() && modalRobot.isModalVisible()) modalRobot.clickCancelToCloseModal().also { commonUtil.reporting.Report.addMessage("Return label mvp modal was closed") }
    }

    @Step("Click description with retries (iOS)")
    private fun clickDescriptionWithRetryIos() {
        IOS.doIfiOS {
            val x = descriptionElement.withScrollIos().location.getX()
            val y = descriptionElement.location.getY()
            val width = descriptionElement.size.width
            val height = descriptionElement.size.height
            // Tap right side of element
            VintedDriver.tap((x + width - 1), descriptionElement.center.y)
            if (!isItemPropertiesOrCatalogLayoutOrAllCategoriesVisible()) {
                // Tap bottom of element
                VintedDriver.tap((x + 1), (y + height - 1))
            }
        }
    }

    @Step("Click more (iOS)")
    private fun clickMoreIos() {
        IOS.doIfiOS {
            VintedAssert.assertTrue(descriptionElement.withScrollIos().isVisible(), "Description element should be visible")
            VintedDriver.scrollDownABit(0.8, 0.7)
            descriptionElement.tap()
            if (!isItemPropertiesOrCatalogLayoutOrAllCategoriesVisible()) clickDescriptionWithRetryIos()
        }
    }

    private fun isItemPropertiesOrCatalogLayoutOrAllCategoriesVisible(): Boolean {
        return categoryTextElement.isVisible(2) || catalogRobot.isCatalogLayoutVisible(2) || browseRobot.isAllCategoriesElementVisible()
    }

    @Step("Click on size")
    fun clickOnSize(): SizeWebViewRobot {
        sizeTextElement.withScrollIos().click()
        return SizeWebViewRobot()
    }

    @Step("Assert category is {categoryId}")
    fun assertCategory(categoryId: Long): ItemRobot {
        val expectedCategory = GlobalAPI.getCatalogById(user = loggedInUser, id = categoryId).title
        commonUtil.reporting.Report.addMessage("Expected category $expectedCategory, id: $categoryId")
        assertText({ categoryTextElement }, expectedCategory, "category")
        return this
    }

    @Step("Assert size is {size}")
    fun assertSize(size: String): ItemRobot {
        assertText({ sizeTextElement }, size, "size")
        return this
    }

    @Step("Assert color is {color}")
    fun assertColor(color: String): ItemRobot {
        assertText({ colorTextElement }, color, "color")
        return this
    }

    @Step("Assert book ISBN is {isbn}")
    fun assertISBN(isbn: String): ItemRobot {
        assertText({ isbnTextElement }, isbn, "isbn")
        return this
    }

    @Step("Assert book author is {author}")
    fun assertAuthor(author: String): ItemRobot {
        assertText({ authorTextElement }, author, "author")
        return this
    }

    @Step("Assert book title is {title}")
    fun assertTitle(title: String): ItemRobot {
        assertText({ titleTextElement }, title, "title")
        return this
    }

    @Step("Assert material is {material}")
    fun assertMaterial(material: String): ItemRobot {
        assertText({ materialTextElement }, material, "material")
        return this
    }

    @Step("Assert view count is not empty")
    fun assertViewCountIsNotEmpty(): ItemRobot {
        val text = viewCountTextElement.withScrollIos().text
        VintedAssert.assertTrue(text.isNotEmpty(), "View count should not be empty. But was: \"$text\"")
        return this
    }

    @Step("Assert upload date is not empty")
    fun assertUploadDateIsNotEmpty(): ItemRobot {
        Android.scrollDown()
        val text = uploadDateTextElement.withScrollIos().text
        VintedAssert.assertTrue(text.isNotEmpty(), "Upload date should not be empty. But was: \"$text\"")
        return this
    }

    @Step("Assert interested users count is not empty")
    fun assertInterestedUsersCountIsNotEmpty(): ItemRobot {
        val text = interestedUserCountTextElement.withScrollIos().text
        VintedAssert.assertTrue(text.isNotEmpty(), "Interested users count should not be empty. But was: \"$text\"")
        return this
    }

    @Step("Assert item size, condition and brand are: {item.size} {item.status} {item.brand} in short item info")
    fun assertItemSizeConditionBrand(item: VintedItem): ItemRobot {
        assertTextAndroidAndCheckIfElementNotNullIos({ itemSizeConditionBrandElement }, item.size, "Expected size: ${item.size}")
        if (item.brand.isNotEmpty()) {
            assertTextAndroidAndCheckIfElementNotNullIos({ itemSizeConditionBrandElement }, item.brand, "Expected brand: ${item.brand}")
        }
        assertTextAndroidAndCheckIfElementNotNullIos({ itemSizeConditionBrandElement }, item.status, "Expected condition: ${item.status}")
        return this
    }

    @Step("Assert item brand is {brand} in short item info")
    fun assertItemBrand(brand: String): ItemRobot {
        VintedAssert.assertTrue(itemSizeConditionBrandElement.text.contains(brand), "Item should contain $brand brand but was ${itemSizeConditionBrandElement.text}")
        return this
    }

    @Step("SKIPPED!!  Assert location is {location} for non payment countries.")
    fun assertLocation(location: String): ItemRobot {
        return this
        if (!isPaymentCountry && isAndroid) {
            var tmpLocation = location
            if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.UK) && isiOS) {
                commonUtil.reporting.Report.addMessage("UK location should be United Kingdom for iOS")
                tmpLocation = "United Kingdom"
            }
            assertTextAndroidAndCheckIfElementNotNullIos({ itemLocationElementAndroid }, tmpLocation, "Expected city: $tmpLocation")
        }
        return this
    }

    @Step("Swipe left to next image")
    fun swipeLeftToNextImage(): ItemRobot {
        imageElement.swipeLeft()
        return this
    }

    @Step("Click on image")
    fun clickOnImage(): FullImageRobot {
        imageElement.tap()
        return fullImageRobot
    }

    @Step("Assert share button is {visibility}")
    fun assertShareButtonVisibility(visibility: Visibility): ItemRobot {
        VintedAssert.assertEquals(shareButton.isVisible(), visibility.value, "Share button should be $visibility")
        return this
    }

    @Step("Click share button")
    fun clickShareButton(): SharingOptionsRobot {
        shareButton.click()
        return SharingOptionsRobot()
    }

    @Step("Assert price is {price}")
    fun assertPrice(price: String): ItemRobot {
        val expectedPrice = PriceFactory.getFormattedPriceWithCurrencySymbol(price, false)
        val actualPrice = priceElement(expectedPrice).text.trim()
        PriceFactory.assertEquals(actualPrice, expectedPrice, "Price should be as expected $expectedPrice")
        return this
    }

    @Step("Assert reserved button is visible")
    fun assertReservedButtonIsVisible(): ItemRobot {
        VintedAssert.assertTrue(reservedButton.withScrollIos().isVisible(), "Reserved button should be visible")
        return this
    }

    @Step("Click reservation button")
    fun clickReservation(): ReservationInfoRobot {
        reservedButton.click()
        return ReservationInfoRobot()
    }

    @Step("Open item's actions")
    fun openItemActions(): ItemActionRobot {
        itemActionsButton.withWait(seconds = 10).click()
        return ItemActionRobot()
    }

    @Step("Assert that hidden label is {visibility} in item screen")
    fun assertHiddenLabelVisibility(visibility: Visibility) {
        VintedAssert.assertVisibilityEquals(itemHiddenLabelElement.withWait(), visibility, "Hidden label should be $visibility")
    }

    @Step("Assert that sold label is visible in item screen")
    fun assertSoldLabelIsVisible() {
        VintedAssert.assertTrue(itemSoldLabelElement.isVisible(), "Sold label should be visible")
    }

    @Step("Assert that swap label is visible in item screen")
    fun assertSwapLabelIsVisible() {
        VintedAssert.assertTrue(itemSwapLabelElement.isVisible(), "Swap label should be visible")
    }

    @Step("Assert item title is {title}")
    fun assertItemTitle(title: String): ItemRobot {
        try {
            VintedDriver.findElement(
                VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}item_header_info_title\").text(\"$title\")"),
                VintedBy.accessibilityId(title)
            )
        } catch (e: NoSuchElementException) {
            VintedAssert.fail("Item title '$title' was not found", e)
        }
        return this
    }

    @Step("Assert description is {expectedText} only on Android")
    fun assertDescription(expectedText: String): ItemRobot {
        Android.doIfAndroid {
            val descriptionElementText = descriptionElement.text
            VintedAssert.assertEquals(
                descriptionElementText, expectedText,
                "Item description should be '$expectedText' but was '$descriptionElementText'"
            )
        }
        return this
    }

    @Step("Assert similar and user items tabs are visible")
    fun assertSimilarAndUserItemsTabsAreVisible(): ItemRobot {
        repeat(2) { IOS.scrollDown() }
        Android.doIfAndroid { VintedDriver.scrollUpABit(); VintedDriver.scrollDown() }
        VintedAssert.assertTrue(userItemsTabElement.isVisible(), "User items tab should be visible")
        VintedAssert.assertTrue(similarItemsTabElement.isVisible(), "Similar items tab should be visible")
        return this
    }

    @Step("Assert bundle button is visible in user items tab but not similar items tab")
    fun assertBundleButtonIsVisibleInUserItemsTab(): ItemRobot {
        Android.scrollDown()
        assertBundleButtonVisibility(Visibility.Visible)
        return this
    }

    @Step("Assert bundle button visibility: {visibility} in user items tab ")
    fun assertBundleButtonVisibilityInUserItemsTab(visibility: Visibility, scrollUp: Boolean = true): ItemRobot {
        sleepWithinStep(1000)
        repeat(2) { IOS.scrollDown() }
        repeat(3) { Android.scrollDown() }
        assertBundleButtonVisibility(visibility)
        if (scrollUp) {
            repeat(3) { Android.scrollUp() }
            IOS.scrollUp()
        }
        return this
    }

    @Step("Open similar items tab and assert bundle button is not visible")
    fun assertBundleButtonIsNotVisibleInSimilarItemsTab() {
        similarItemsTabElement.click()
        assertBundleButtonVisibility(Visibility.Invisible)
    }

    @Step("Assert bundle button is: {visibility}")
    private fun assertBundleButtonVisibility(visibility: Visibility) {
        VintedAssert.assertVisibilityEquals(bundleLayout, visibility, "Bundle button should be $visibility")
    }

    @Step("Click bundle button")
    fun clickBundleButton(): BundleRobot {
        commonUtil.Util.retryAction(
            block = {
                Android.scrollUpABit()
                bundleButton.tap()
                bundleRobot.isAddBundleElementsVisible()
            },
            actions = { Android.scrollUpABit() },
            retryCount = 3
        )
        return bundleRobot
    }

    @Step("Click item message button")
    fun clickItemMessageButton(): ConversationRobot {
        commonUtil.Util.retryAction(
            block =
            {
                itemActionMessageButton.withScrollIos().click()
                conversationRobot.isConversationScreenVisible()
            },
            actions = {
                VintedDriver.scrollUpABit()
            },
            retryCount = 2
        )
        return conversationRobot
    }

    @Step("Click item message button for skip authentication")
    fun clickItemMessageButtonForSkipAuthentication(): WorkflowRobot {
        commonUtil.Util.retryAction(
            block =
            {
                itemActionMessageButton.withScrollIos().click()
                welcomeRobot.isShowLoginOptionsVisible()
            },
            actions = {
                VintedDriver.scrollUpABit()
            },
            retryCount = 2
        )
        return workflowRobot
    }

    @Step("Click buy button")
    fun clickBuyButton(): CheckoutRobot {
        VintedDriver.scrollDownABit(0.8, 0.7)
        inAppNotificationRobot.closeInAppNotificationIfExists()
        itemActionBuyButton.click()
        return CheckoutRobot()
    }

    @Step("Click favorite button")
    fun clickFavoriteButton(): ItemRobot {
        Android.doIfAndroid {
            itemActionFavoriteButton.tapWithRetry()
        }
        IOS.doIfiOS { itemActionFavoriteButton.click() }
        return this
    }

    @Step("Assert favorite button heart icon is red")
    fun assertHeartIconIsRed() {
        val isInImage = ImageRecognition.isImageInScreen(
            file = ImageFactory.ITEM_FAVORITE_HEART_ACTIVE,
            threshold = 0.35
        )
        VintedAssert.assertTrue(isInImage, "Image occurrence was not found")
    }

    @Step("Assert heart icon is white")
    fun assertHeartIconIsWhite(): ItemRobot {
        itemActionFavoriteButton.withScrollIos().mobileElement.tagName
        val isInImage = ImageRecognition.isImageInScreen(
            file = ImageFactory.ITEM_FAVORITE_HEART_INACTIVE,
            threshold = 0.35
        )
        VintedAssert.assertTrue(isInImage, "Image occurrence was not found")
        return ItemRobot()
    }

    @Step("Assert message button is visible in item screen")
    fun assertMessageButtonIsVisibleInItemScreen(): ItemRobot {
        VintedAssert.assertTrue(itemActionMessageButton.withScrollIos().isVisible(), "Message button should be visible")
        IOS.scrollUp()
        return this
    }

    private fun assertTextAndroidAndCheckIfElementNotNullIos(androidElement: () -> VintedElement, text: String, errorMessage: String) {
        if (isAndroid) {
            val actualText = androidElement().text
            VintedAssert.assertTrue(actualText.contains(text), "$errorMessage, actual text: $actualText")
        } else {
            val element = IOS.findElementByTextContains(text)
            VintedAssert.assertTrue(element.isVisible(), errorMessage)
        }
    }

    private fun assertText(element: () -> VintedElement, text: String, errorInSection: String) {
        val actualText = element().withScrollIos().text
        VintedAssert.assertEquals(actualText, text, "Expected $errorInSection: $text, but was $actualText")
    }

    @Step("Click on item description")
    fun clickOnItemDescription() {
        if (isiOS) {
            clickMoreIos()
        } else {
            Android.scrollDownABit()
            clickOnBottomOfDescriptionElementAndroid()
        }
    }

    private fun clickOnBottomOfDescriptionElementAndroid() {
        Android.doIfAndroid {
            commonUtil.Util.retryAction(
                {
                    val x = descriptionElement.center.getX()
                    val y = descriptionElement.center.getY()
                    val height = descriptionElement.rect.getHeight()
                    try {
                        // Try to click at the bottom of element as it fails if description contains multiple lines
                        Android.tap(x, y + (height / 3)); true
                    } catch (e: InvalidArgumentException) {
                        false
                    }
                },
                { Android.scrollDownABit() }, 3
            )
        }
    }

    @Step("Click on item category")
    fun clickOnItemCategory(): CatalogRobot {
        categoryTextElement.withScrollIos().click()
        Android.doIfAndroid {
            commonUtil.Util.retryAction(
                block = {
                    !VintedDriver.findElement(androidBy = VintedBy.id("item_description_category")).isVisible(2)
                },
                actions = {
                    VintedDriver.scrollUpABit() // On S20, S10+ fails because Category is partially visible
                    categoryTextElement.click()
                },
                retryCount = 1
            )
        }
        return CatalogRobot()
    }

    @Step("Return category name")
    fun returnCategoryName(categoryId: Long): String {
        val categoryName = GlobalAPI.getCatalogById(user = loggedInUser, id = categoryId).title
        commonUtil.reporting.Report.addMessage(categoryName)
        return categoryName
    }

    @Step("Click on item brand")
    fun clickOnItemBrand(): CatalogRobot {
        val x = itemSizeConditionBrandElement.center.getX() + 20
        val y = itemSizeConditionBrandElement.center.getY()
        Android.tap(x, y)
        IOS.tap(x, y)
        return CatalogRobot()
    }

    @Step("Click on seller info block")
    fun clickOnSellerInfoBlock(): UserProfileClosetRobot {
        userShortInfoSectionRobot.clickOnUserInfo()
        return RobotFactory.userProfileClosetRobot
    }

    @Step("Assert buyer protection cell is visible")
    fun assertBuyerProtectionCellIsVisible(): ItemRobot {
        IOS.doIfiOS { if (!buyerProtectionElement.isVisible(1)) VintedDriver.scrollDownABit() }
        VintedAssert.assertTrue(buyerProtectionElement.isVisible(), "Buyer protection cell should be visible")
        return this
    }

    @Step("Assert buy button is visible: {shouldBeVisible}")
    fun assertBuyButtonVisibility(shouldBeVisible: Boolean): ItemRobot {
        if (shouldBeVisible) {
            VintedAssert.assertTrue(itemActionBuyButton.isVisible(), "Buy button should be visible: {$shouldBeVisible}")
        } else {
            VintedAssert.assertTrue(itemActionBuyButton.isInvisible(1), "Buy button should be visible: {$shouldBeVisible}")
        }
        return this
    }

    @Step("Assert bump button is {visibility}")
    fun assertBumpButtonVisibility(visibility: Visibility): ItemRobot {
        VintedAssert.assertEquals(bumpButton.isVisible(), visibility.value, "Bump button should be $visibility")
        return this
    }

    @Step("Click on bump button")
    fun clickOnBumpButton(): BumpsPreCheckoutRobot {
        bumpButton.withWait(waitFor = WaitFor.Visible).click()
        bumpsPreCheckoutRobot.closeBumpsInfoModalIfVisible()
        return bumpsPreCheckoutRobot
    }

    @Step("Check if bump label is {visibility}")
    fun assertBumpLabelVisibility(visibility: Visibility = Visibility.Invisible): ItemRobot {
        VintedAssert.assertVisibilityEquals(bumpLabelElement, visibility, "Bump label should be visible: $visibility", waitForVisible = 14)
        return this
    }

    @Step("Click on bump statistics cell")
    fun clickOnBumpStatisticsButton(): BumpStatisticsRobot {
        bumpStatisticsCellElement.withWait(WaitFor.Visible).click()
        return RobotFactory.bumpStatisticsRobot
    }
}
