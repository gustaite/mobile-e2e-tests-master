package robot.profile

import RobotFactory.forumHomeRobot
import RobotFactory.holidayModeRobot
import RobotFactory.ordersRobot
import RobotFactory.personalizationRobot
import RobotFactory.profileTabRobot
import RobotFactory.walletRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.*
import robot.forum.ForumHomeRobot
import robot.holidaymode.HolidayModeRobot
import robot.personalization.PersonalizationRobot
import robot.profile.balance.WalletRobot
import robot.profile.settings.SettingsRobot
import robot.profile.tabs.UserProfileRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class ProfileTabRobot : BaseRobot() {

    private val settingsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_menu_account_settings"),
            iOSBy = VintedBy.accessibilityId("profile_settings")
        )

    private val profileInfoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_short_info"),
            iOSBy = VintedBy.accessibilityId("user_profile")
        )

    private val personalizationElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "user_menu_manage_feed",
            "profile_tab_personalization"
        )

    private val forumTabElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_menu_forum"))

    private val privacyCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_menu_privacy_manager"),
            iOSBy = VintedBy.accessibilityId("user_menu_privacy_manager")
        )

    private val aboutVintedAndroidElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_menu_get_to_know"))

    private val aboutVintedCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey({ aboutVintedAndroidElement }, "profile_tab_get_to_know")

    private val walletElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("user_menu_wallet", "settings_manage_payouts_button")

    private val helpCenterAndroidElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_menu_help_center"))

    private val helpCenterCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey({ helpCenterAndroidElement }, "faq_screen_title")

    private val holidayModeCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_menu_holiday"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("holiday_setting"))
        )

    private val myOrdersCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "user_menu_my_orders",
            "settings_manage_orders_button"
        )

    private val vintedGuideElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "user_menu_vinted_guide",
            iosTranslationKey = "user_menu_vinted_guide"
        )

    private val myFavouriteItemsElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "user_menu_favorites",
            iosTranslationKey = "favorite_clothes"
        )

    private val bundleDiscountsElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            {
                VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_menu_bundle_discount"))
            },
            iosTranslationKey = "profile_tab_bundle_discount"
        )

    private val forumTabElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            { forumTabElementAndroid },
            iosTranslationKey = "tab_bar_forum"
        )

    private val inviteFriendsElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            {
                VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_menu_invite_friends"))
            },
            iosTranslationKey = "referral_share_cell"
        )

    @Step("Open Settings screen")
    fun openSettingsScreen(): SettingsRobot {
        settingsElement.withScrollIos().click()
        IOS.doIfiOS {
            commonUtil.Util.retryAction(
                { !settingsElement.isVisible(1) },
                { settingsElement.click() },
                1
            )
        }
        return SettingsRobot()
    }

    @Step("Open Privacy screen")
    fun openPrivacyScreen(): ProfileTabRobot {
        privacyCellElement.withScrollIos().click()
        return this
    }

    @Step("Open forum tab")
    fun openForumTab(): ForumHomeRobot {
        forumTabElement.withScrollIos().click()
        return forumHomeRobot
    }

    @Step("Click on user profile")
    fun clickOnUserProfile(): UserProfileRobot {
        profileInfoElement.click()
        return UserProfileRobot()
    }

    @Step("Click on about vinted cell")
    fun openAboutVinted(): AboutVintedRobot {
        aboutVintedCellElement.withScrollIos().click()
        return AboutVintedRobot()
    }

    @Step("Open balance screen")
    fun openBalanceScreen(): WalletRobot {
        walletElement.click()
        return walletRobot
    }

    @Step("Click on help center cell")
    fun openHelpCenter(): HelpCenterRobot {
        helpCenterCellElement.withScrollIos().click()
        return HelpCenterRobot()
    }

    @Step("Open holiday mode screen")
    fun openHolidayModeScreen(): HolidayModeRobot {
        holidayModeCellElement.withScrollIos().click()
        return holidayModeRobot
    }

    @Step("Open my orders screen")
    fun openMyOrdersScreen(): OrdersRobot {
        myOrdersCellElement.click()
        return ordersRobot
    }

    @Step("Open personalization screen")
    fun openPersonalizationScreen(): PersonalizationRobot {
        personalizationElement.click()
        return personalizationRobot
    }

    @Step("Open Vinted guide tab")
    fun openVintedGuideScreen(): VintedGuideRobot {
        vintedGuideElement.click()
        return VintedGuideRobot()
    }

    @Step("Open My favourites tab")
    fun openMyFavouriteItemsScreen() {
        myFavouriteItemsElement.click()
    }

    @Step("Open Bundle discounts tab")
    fun openBundleDiscountsScreen() {
        bundleDiscountsElement.click()
    }

    @Step("Open Invite friends tab")
    fun openInviteFriendsScreen() {
        inviteFriendsElement.withScrollIos().click()
    }

    @Step("Assert Profile info element is visible")
    fun assertProfileInfoElementIsVisible(): ProfileTabRobot {
        VintedAssert.assertTrue(profileInfoElement.isVisible(), "Profile info element should be visible")
        return profileTabRobot
    }
}
