package robot.profile.tabs

import RobotFactory
import RobotFactory.bundleRobot
import RobotFactory.userProfileClosetOrganiseRobot
import RobotFactory.userProfileClosetRobot
import api.data.models.VintedItem
import commonUtil.testng.config.PortalFactory
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.extensions.changeSimpleSpaceToSpecial
import io.qameta.allure.Step
import org.openqa.selenium.StaleElementReferenceException
import robot.BaseRobot
import robot.SharingOptionsRobot
import robot.bumps.BumpsItemsSelectionRobot
import robot.closetpromo.ClosetPromoPreCheckoutRobot
import robot.item.BundleRobot
import robot.item.ItemRobot
import robot.profile.UserProfileClosetOrganiseRobot
import robot.profile.UserProfileEditRobot
import util.*
import util.EnvironmentManager.isiOS
import util.IOS.ElementType.ANY
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage
import util.values.Visibility

class UserProfileRobot : BaseRobot() {

    private val userProfileCompleteButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdMatches(".*_complete_profile"),
            iOSBy = VintedBy.accessibilityId("user_profile_complete")
        )

    private val successNotificationElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(
                "view_notification_container"
            )
        )

    private val aboutTabElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("user_closet_tab_about"), iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("user_profile_tabs_about")}' || name == 'tab_item_2'"))

    private val closetTabElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_tab_closet"),
            iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("user_profile_tabs_closet")}' || name == 'Spinta' || name == 'tab_item_0'")
        )

    private val settingsMenuButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_more_options"),
            iOSBy = VintedBy.accessibilityId("more")
        )

    private val shareButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("title", Android.getElementValue("user_profile_menu_share")),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeSheet/**/XCUIElementTypeButton[2]")
        )

    private val feedbackTabElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("user_closet_tab_reviews"), iOSBy = VintedBy.iOSNsPredicateString("name == '${IOS.getElementValue("user_profile_tabs_reviews")}' || name == 'tab_item_1'"))

    private val bumpBannerElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_item_bump_cta"),
            iOSBy = VintedBy.iOSTextByBuilder(
                text = ElementByLanguage.bumpBannerText,
                onlyVisibleInScreen = true,
                elementType = ANY,
                searchType = Util.SearchTextOperator.STARTS_WITH
            )
        )

    private val closetPromoBannerElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_closet_promo_cta"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name CONTAINS 'user_closet_banner'`][1]")
        )

    private val closetPromoStatisticsBannerElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_closet_closet_promo_cta"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeOther[`name CONTAINS 'user_closet_banner'`][1]")
        )

    private val shopButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("profile_bundle_header_button"),
            iOSBy = VintedBy.accessibilityId("bundling_entry_point_button_text")
        )

    private val verifiedInfoElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId(
                IOS.getElementValue("user_profile_verified_info")
            )
        )

    private val bumpLabelElementsList: List<VintedElement>
        get() = VintedDriver.findElementListByText(
            text = ElementByLanguage.chooseValueByPlatform(
                ElementByLanguage.bumpLabelText,
                ElementByLanguage.bumpLabelText.uppercase()
            )
        )

    val closetScreen: UserProfileClosetRobot get() = UserProfileClosetRobot()
    val aboutScreen: ProfileAboutTabRobot get() = ProfileAboutTabRobot()
    val feedbackScreen: UserProfileFeedbackRobot get() = UserProfileFeedbackRobot()

    // Todo change iOS element when 'turn off a11y project' PR will be merged
    private val itemElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.scrollableId("item_box_image"),
            iOSBy = VintedBy.iOSNsPredicateString("${IOS.predicateWithCurrencySymbolsByName} || name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_global_item_box")}'")
        )

    private fun itemTitleElementIos(itemTitle: String) =
        VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name CONTAINS '$itemTitle'"))

    private fun itemElementWithPriceIos(price: String) =
        VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '${price.changeSimpleSpaceToSpecial()}'"))

    private val closetFilterButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_filter_btn"), iOSBy = VintedBy.accessibilityId("filter")
        )

    @Step("Click complete profile button")
    fun clickCompleteProfileButton(): UserProfileEditRobot {
        // ToDo start using click complete profile when IOS/Android will be alligned
        userProfileCompleteButton.click()
        return UserProfileEditRobot()
    }

    @Step("Click on feedback tab")
    fun clickFeedbackTab(): UserProfileFeedbackRobot {
        feedbackTabElement.click()
        return feedbackScreen
    }

    @Step("Open about tab in profile")
    fun openAboutTab(): ProfileAboutTabRobot {
        aboutTabElement.click()
        sleepWithinStep(300)
        return aboutScreen
    }

    @Step("Open closet tab in profile")
    fun openClosetTab(): UserProfileClosetRobot {
        closetTabElement.click()
        return closetScreen
    }

    @Step("Assert closet tab is visible in profile")
    fun assertClosetTabIsVisible(): UserProfileRobot {
        VintedAssert.assertTrue(isClosetTabVisible(10), "Closet tab element was not visible")
        return this
    }

    @Step("Is 'Closet tab' visible in profile")
    fun isClosetTabVisible(waitSec: Long): Boolean {
        return closetTabElement.isVisible(waitSec)
    }

    @Step("ANDROID ONLY: Wait until changes successfully saved notification disappears")
    fun waitUntilSuccessNotificationDisappears(): UserProfileRobot {
        if (isiOS) return this
        try {
            var isVisible = successNotificationElementAndroid.withWait().isVisible()
            while (isVisible) {

                isVisible = successNotificationElementAndroid.isVisible(1)
            }
        } catch (e: StaleElementReferenceException) {
            // catch exception and do nothing
            commonUtil.reporting.Report.addMessage("stale reference ${e.message}")
        }
        return this
    }

    @Step("Click settings button")
    fun clickSettingsButton(): UserProfileRobot {
        settingsMenuButton.click()
        return this
    }

    @Step("Click share button")
    fun clickShareButton(): SharingOptionsRobot {
        shareButtonElement.click()
        return SharingOptionsRobot()
    }

    @Step("Click on closet promo banner")
    fun clickOnClosetPromoBanner(): ClosetPromoPreCheckoutRobot {
        VintedAssert.assertTrue(closetPromoBannerElement.isVisible(), "Closet promo banner should be visible")
        closetPromoBannerElement.click()
        return RobotFactory.closetPromoPreCheckoutRobot
    }

    @Step("Click on closet promo statistics banner")
    fun clickOnClosetPromoStatisticsBanner(): ClosetPromoPreCheckoutRobot {
        VintedAssert.assertTrue(closetPromoStatisticsBannerElement.isVisible(), "Closet promo banner should be visible")
        closetPromoStatisticsBannerElement.click()
        return RobotFactory.closetPromoPreCheckoutRobot
    }

    @Step("Click on bump banner")
    fun clickOnBumpBanner(): BumpsItemsSelectionRobot {
        bumpBannerElement.click()
        return RobotFactory.bumpsItemsSelectionRobot
    }

    @Step("Scroll to the items")
    private fun scrollToItem() {
        waitUntilSuccessNotificationDisappears()
        IOS.scrollDown()
        Android.scrollDown()
    }

    @Step("Open item by index")
    fun openItemByIndex(itemNumber: Int): ItemRobot {
        scrollToItem()
        itemElementList[itemNumber].click()
        return RobotFactory.itemRobot
    }

    @Step("Open first item")
    fun openFirstItem(): ItemRobot {
        openItemByIndex(0)
        return RobotFactory.itemRobot
    }

    @Step("Open first item or by title")
    fun openFirstItemOrByUsingTitle(item: VintedItem): ItemRobot {
        Android.doIfAndroid { openFirstItem() }
        openItemByItemTitle(item.title)
        return RobotFactory.itemRobot
    }

    @Step("iOS only: Open item by title")
    fun openItemByItemTitle(itemTitle: String): ItemRobot {
        IOS.doIfiOS {
            scrollToItem()
            itemTitleElementIos(itemTitle).click()
        }
        return RobotFactory.itemRobot
    }

    @Step("Assert there is at least one item in user's profile")
    fun assertItemIsVisible(): UserProfileRobot {
        VintedAssert.assertTrue(itemElementList.isNotEmpty(), "Item should be visible")
        return this
    }

    @Step("Open item by price: {item.price} (only IOS)")
    fun openItemByPrice(item: VintedItem): ItemRobot {
        val price = if (isiOS && PortalFactory.isCurrentRegardlessEnv(VintedPortal.CZ)) {
            item.priceNumeric.substringBefore(".")
        } else {
            item.price
        }
        commonUtil.reporting.Report.addMessage("Price to open by is: $price")
        IOS.doIfiOS {
            VintedDriver.scrollDownABit()
            itemElementWithPriceIos(price).withScrollDownSimple(tryForSeconds = 120).click()
        }
        return RobotFactory.itemRobot
    }

    @Step("Click shop button")
    fun clickShopButton(): BundleRobot {
        shopButton.click()
        return bundleRobot
    }

    @Step("Is shop button visible")
    fun isShopButtonIsVisible(): Boolean {
        return shopButton.isVisible(1)
    }

    @Step("Is Verified info visible")
    fun isVerifiedInfoVisible(): Boolean {
        return verifiedInfoElementIos.isVisible(1)
    }

    @Step("Check if right amount of items with bump labels are visible in profile screen")
    fun assertBumpLabelsAreVisible(bumpedItemsCount: Int) {
        IOS.scrollDown()
        Android.scrollDown()
        VintedAssert.assertEquals(bumpLabelElementsList.count(), bumpedItemsCount, "Bump labels count does not match count of bumped items")
    }

    @Step("Click on Closet Filter Button")
    fun clickClosetFilterButton(): UserProfileClosetOrganiseRobot {
        closetFilterButtonElement.click()
        return userProfileClosetOrganiseRobot
    }

    @Step("Check shop bundle button visibility")
    fun checkShopBundleVisibility(visibility: Visibility): UserProfileClosetRobot {
        VintedAssert.assertVisibilityEquals(shopButton, visibility, "Button to shop bundle should be $visibility")
        return userProfileClosetRobot
    }
}
