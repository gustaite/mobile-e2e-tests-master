package robot

import RobotFactory.feedRobot
import RobotFactory.fullImageRobot
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import io.qameta.allure.Step
import robot.browse.BrowseRobot
import robot.browse.CatalogRobot
import robot.item.FullImageRobot
import robot.item.ItemRobot
import util.Android
import util.Android.Companion.scrollUntilVisibleAndroid
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.IOS
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.image.AssertImage.Companion.assertImageIsInScreen
import util.image.AssertImage.Companion.assertImageIsInSelectedElement
import util.image.AssertImage.Companion.assertImageIsNotInSelectedElement
import util.image.ImageFactory
import util.values.ScrollDirection

class FeedRobot : BaseRobot() {

    private val personalizationButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("feed_personalization_banner_cta"),
            iOSBy = VintedBy.accessibilityId("feed_personalization")
        )

    private val itemHeartElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_box_favorites_icon"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' AND (name BEGINSWITH 'heartMedium')")
        )

    private val homepageShopByCategoryTitleElementIos: VintedElement
        get() = IOS.findElementByTranslationKey("homepage_shop_by_category")

    // Todo remove iosBy2 when 'turn off a11y project' PR will be merged
    private val itemBoxImageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_box_image"),
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.iOSClassChain("**/XCUIElementTypeAny[`(name == 'item_box' || name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_global_item_box")}') && visible == 1`]"),
                    iosBy2 = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND ${IOS.predicateWithCurrencySymbolsByName}")
                )
            }
        )

    private val searchBarElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "search_container",
                "search_button"
            ),
            iOSBy = VintedBy.accessibilityId("search_bar")
        )

    private fun sizeElementList(sizeText: String) =
        VintedDriver.findElementList(
            androidBy = VintedBy.androidTextByBuilder(text = sizeText, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = sizeText, searchType = Util.SearchTextOperator.CONTAINS)
        )

    private val favoriteItemsBlockTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("recently_favourited_items_title"),
            iOSBy = VintedBy.accessibilityId("recently_favourited_header_title")
        )

    private val favoriteItemsBlockSeeAllButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("recently_favourited_items_see_all"),
            iOSBy = VintedBy.accessibilityId("recently_favourited_header_see_all_button")
        )

    private val popularItemsBlockSeeAllButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("popular_items_see_all"),
            iOSBy = VintedBy.accessibilityId("popular_items_header_see_all_button")
        )

    private val favoriteItemsBoxElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild("recently_favourited_items_list", "item_box_image"),
            iOSBy = VintedBy.accessibilityId("recently_favourited_item_box")
        )

    private val homepageCategoriesBlockTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("homepage_categories_title"),
            iOSBy = VintedBy.accessibilityId("homepage_categories_header_title")
        )

    private val popularItemsTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("popular_items_title"),
            iOSBy = VintedBy.accessibilityId("homepage_popular_items_header_title")
        )

    private val popularItemsSeeAllButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("popular_items_see_all"),
            iOSBy = VintedBy.accessibilityId("popular_items_header_see_all_button")
        )

    // Todo change iOS element when 'turn off a11y project' PR will be merged
    private val popularItemElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild("popular_items_list", "item_box_image"),
            iOSBy = VintedBy.iOSNsPredicateString("${IOS.predicateWithCurrencySymbolsByName} || name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_global_item_box")}'")
        )

    private val brandListTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("homepage_brand_list_title"),
            iOSBy = VintedBy.accessibilityId("homepage_shop_by_brand_header_title")
        )

    private val brandListBlockElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("homepage_brands_list")
        )

    private val popularSearchesTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("popular_searches_title"),
            iOSBy = VintedBy.accessibilityId("popular_searches_header_title")
        )

    private fun catalogTitleElement(catalogTitleAndPosition: Pair<String, Int>): VintedElement {
        val (catalogTitle, position) = catalogTitleAndPosition

        return VintedDriver.findElement(
            androidBy = VintedBy.androidText(catalogTitle),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeOther' AND name == 'homepageCategory_$position'")
        )
    }

    private val newsFeedTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("news_feed_heading_title"),
            iOSBy = VintedBy.accessibilityId("news_feed_header_title")
        )

    private val closetPromoHeaderElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("promoted_closet_header", "promoted_member_members_title")

    private val closetPromoCarouselElementAndroid: VintedElement
        get() = VintedDriver.findElement(VintedBy.id("closet_promo_item_carousel"))

    @Step("Assert that personalization button is visible")
    fun assertPersonalizationButtonIsVisible(): FeedRobot {
        if (isAndroid)
            personalizationButton.scrollUntilVisibleAndroid(ScrollDirection.UP)
        else {
            IOS.scrollUp()
            VintedAssert.assertTrue(personalizationButton.isVisible(), "Personalization button should be visible")
        }
        return this
    }

    @Step("Assert that first heart icon in the feed is inactive")
    fun assertFirstHeartIconIsNotRed(): FeedRobot {
        if (isiOS && !itemHeartElement.isVisible()) {
            repeat(2) { IOS.scrollDown() }
        }
        assertImageIsInSelectedElement({ itemHeartElement }, ImageFactory.ITEM_FAVORITE_HEART_INACTIVE, threshold = 0.3)
        assertImageIsNotInSelectedElement({ itemHeartElement }, ImageFactory.ITEM_FAVORITE_HEART_ACTIVE, threshold = 0.45, retryCount = 0)
        return this
    }

    @Step("Click on the first heart icon")
    fun clickOnFirstHeartIcon(): FeedRobot {
        itemHeartElement.tap()
        return this
    }

    @Step("iOS only: Scroll down if new homepage test element is visible")
    fun scrollDownIfNewHomePageIsVisible(): FeedRobot {
        if (homepageShopByCategoryTitleElementIos.isVisible()) {
            repeat(2) { IOS.scrollDown() }
        }
        return this
    }

    @Step("Assert heart icon is red")
    fun assertFirstHeartIconIsRed(): FeedRobot {
        assertImageIsInScreen(ImageFactory.ITEM_FAVORITE_HEART_ACTIVE, threshold = 0.42)
        return this
    }

    @Step("Open item")
    fun openItem(): ItemRobot {
        itemBoxImageElement.withScrollDownUntilElementIsInTopThirdOfScreen()
        itemBoxImageElement.click()
        return ItemRobot()
    }

    @Step("Long press on item")
    fun longPressOnItem(): FullImageRobot {
        itemBoxImageElement.withScrollDownUntilElementIsInTopThirdOfScreen()
        itemBoxImageElement.performLongPress()
        return fullImageRobot
    }

    @Step("Wait for items to be visible")
    fun waitForItemsToBeVisible(): ItemRobot {
        itemBoxImageElement.isVisible()
        return ItemRobot()
    }

    @Step("Assert {expectedItemCountMatchingSize} items are visible with {sizeText} size text")
    fun assertItemCountWithSize(sizeText: String, expectedItemCountMatchingSize: Int) {
        val sizeElementCount = if (VintedElement.isListVisible({ sizeElementList(sizeText) })) sizeElementList(sizeText).size else 0

        VintedAssert.assertEquals(
            sizeElementCount,
            expectedItemCountMatchingSize,
            "$expectedItemCountMatchingSize items with $sizeText sizeText should be visible but found $sizeElementCount"
        )
    }

    @Step("Assert minimum {expectedMinItemCount} items are visible with {sizeText} size text")
    fun assertItemCountWithSizeNotLessThan(sizeText: String, expectedMinItemCount: Int) {
        scrollDownToSkipHomepageElements()
        val sizeElementCount = sizeElementList(sizeText).size
        VintedAssert.assertTrue(
            sizeElementCount >= expectedMinItemCount,
            "$expectedMinItemCount items with $sizeText sizeText should be visible but found $sizeElementCount"
        )
    }

    @Step("Scroll down to skip homepage elements")
    fun scrollDownToSkipHomepageElements(): FeedRobot {
        VintedDriver.scrollDown()
        newsFeedTitleElement
            .withScrollDownSimple()
            .withScrollDownUntilElementIsInTopThirdOfScreen()
        itemBoxImageElement
            .withScrollDownUntilElementIsInTopThirdOfScreen()

        return this
    }

    @Step("Assert catalogs are displayed")
    fun assertCatalogsAreDisplayed(): FeedRobot {
        VintedAssert.assertTrue(homepageCategoriesBlockTitleElement.isVisible(), "Catalogs should be displayed")
        return this
    }

    @Step("Assert search button is visible")
    fun assertSearchButtonVisible(): FeedRobot {
        VintedAssert.assertTrue(searchBarElement.isVisible(), "Search button should be visible")
        return this
    }

    @Step("Assert feed is visible")
    fun assertFeedIsVisible(): FeedRobot {
        feedRobot.assertSearchButtonVisible()
        return this
    }

    @Step("Assert Favorite items block elements are displayed")
    fun assertFavoriteItemsBlockIsVisible(): FeedRobot {
        val softAssert = VintedSoftAssert()
        softAssert.assertTrue(favoriteItemsBlockTitleElement.isVisible(), "Favorite items block title should be displayed")
        softAssert.assertTrue(favoriteItemsBlockSeeAllButtonElement.isVisible(), "See all button should be visible")
        softAssert.assertTrue(favoriteItemsBoxElement.isVisible(), "Favorite items should be displayed")
        softAssert.assertAll()
        return this
    }

    @Step("Click on See All button in favorite items block")
    fun clickSeeAllInFavoritesBlock(): FavoriteItemsRobot {
        favoriteItemsBlockSeeAllButtonElement.click()
        return FavoriteItemsRobot()
    }

    @Step("Click on See All button in popular items block")
    fun clickSeeAllInPopularItemsBlock(): CatalogRobot {
        popularItemsBlockSeeAllButtonElement.click()
        return CatalogRobot()
    }

    @Step("Assert popular brands are displayed")
    fun assertBrandListIsVisible(): FeedRobot {
        IOS.doIfiOS { VintedDriver.scrollDownABit() }
        VintedAssert.assertTrue(brandListTitleElement.isVisible(), "Brand list title should be visible")
        Android.doIfAndroid {
            VintedAssert.assertTrue(brandListBlockElementAndroid.isVisible(), "Block with popular brands should be visible")
        }
        return this
    }

    @Step("Assert popular items block elements are displayed")
    fun assertPopularItemsAreDisplayed(): FeedRobot {
        val softAssert = VintedSoftAssert()
        softAssert.assertTrue(popularItemsTitleElement.withScrollIos().isVisible(), "Popular items block title should be displayed")
        softAssert.assertTrue(popularItemElement.isVisible(), "Popular items should be visible")
        softAssert.assertTrue(popularItemsSeeAllButton.isVisible(), "Popular items see all button should be visible")
        softAssert.assertAll()
        return this
    }

    @Step("Assert popular searches are displayed")
    fun assertPopularSearchesAreDisplayed(): FeedRobot {
        VintedAssert.assertTrue(popularSearchesTitleElement.withScrollIos().isVisible(), "Popular searches block title should be visible")
        return this
    }

    @Step("Click on {catalogTitleAndPosition.first} catalog")
    fun openCatalog(catalogTitleAndPosition: Pair<String, Int>): BrowseRobot {
        catalogTitleElement(catalogTitleAndPosition).click()
        return BrowseRobot()
    }

    @Step("Assert news feed title is displayed")
    fun assertNewsFeedTitleIsDisplayed(): FeedRobot {
        VintedDriver.scrollDownABit()
        VintedAssert.assertTrue(newsFeedTitleElement.withScrollIos().isVisible(), "News feed title title should be visible")
        return this
    }

    @Step("Scroll down if closet promo is visible in feed")
    fun scrollDownIfClosetPromoIsVisible(): FeedRobot {
        if (closetPromoHeaderElement.isVisible(2)) VintedDriver.scrollDown()
        Android.doIfAndroid {
            if (closetPromoCarouselElementAndroid.isVisible(1)) VintedDriver.scrollDown()
        }
        return this
    }
}
