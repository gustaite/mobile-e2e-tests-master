package robot.workflow

import RobotFactory.cmpWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.feedRobot
import RobotFactory.navigationRobot
import RobotFactory.profileTabRobot
import RobotFactory.settingsRobot
import robot.BaseRobot
import util.data.NavigationDataProviders

class NavigationWorkflowRobot : BaseRobot() {

    fun openTabFromMyProfileTab(tab: String) {
        deepLink.profile.goToMyProfile()
        navigationRobot.openProfileTab()
        when (tab) {
            NavigationDataProviders.PROFILE_VINTED_GUIDE_TAB -> {
                profileTabRobot.openVintedGuideScreen()
            }
            NavigationDataProviders.PROFILE_MY_FAVOURITES_TAB -> {
                profileTabRobot.openMyFavouriteItemsScreen()
            }
            NavigationDataProviders.PROFILE_PEROSONALISATION_TAB -> {
                profileTabRobot.openPersonalizationScreen()
            }
            NavigationDataProviders.PROFILE_BALANCE_TAB -> {
                profileTabRobot.openBalanceScreen()
            }
            NavigationDataProviders.PROFILE_MY_ORDERS_TAB -> {
                profileTabRobot.openMyOrdersScreen()
            }
            NavigationDataProviders.PROFILE_BUNDLE_DISCOUNTS_TAB -> {
                profileTabRobot.openBundleDiscountsScreen()
            }
            NavigationDataProviders.PROFILE_FORUM_TAB -> {
                profileTabRobot.openForumTab()
            }
            NavigationDataProviders.PROFILE_INVITE_FRIENDS_TAB -> {
                profileTabRobot.openInviteFriendsScreen()
            }
            NavigationDataProviders.PROFILE_HOLIDAY_MODE_TAB -> {
                profileTabRobot.openHolidayModeScreen()
            }
            NavigationDataProviders.PROFILE_SETTINGS_TAB -> {
                profileTabRobot.openSettingsScreen()
            }
            NavigationDataProviders.PROFILE_PRIVACY_TAB -> {
                profileTabRobot.openPrivacyScreen()
            }
            NavigationDataProviders.PROFILE_ABOUT_VINTED_TAB -> {
                profileTabRobot.openAboutVinted()
            }
            NavigationDataProviders.HELP_CENTER_TAB -> {
                profileTabRobot.openHelpCenter()
            }
        }
    }

    fun openTabFromProfileSettingsTab(tab: String) {
        deepLink.goToSettings()
        when (tab) {
            NavigationDataProviders.PROFILE_SETTINGS_PROFILE_DETAILS_TAB -> {
                settingsRobot.openProfileDetails()
            }
            NavigationDataProviders.PROFILE_SETTINGS_PAYMENTS_TAB -> {
                settingsRobot.openPaymentsSettings()
            }
            NavigationDataProviders.PROFILE_SETTINGS_POSTAGE_TAB -> {
                settingsRobot.openShippingSettings()
            }
            NavigationDataProviders.PROFILE_SETTINGS_SECURITY_TAB -> {
                settingsRobot.openSecurityScreen()
            }
            NavigationDataProviders.PROFILE_SETTINGS_PUSH_NOTIFICATIONS_TAB -> {
                settingsRobot.openPushNotificationSettings()
            }
            NavigationDataProviders.PROFILE_SETTINGS_EMAIL_NOTIFICATIONS_TAB -> {
                settingsRobot.openEmailNotificationSettings()
            }
            NavigationDataProviders.PROFILE_SETTINGS_DATA_SETTINGS_TAB -> {
                settingsRobot.openDataSettings()
            }
        }
    }

    fun navigationFromNavigationBar(testData: NavigationDataProviders.NavigationBarNavigation) {
        when (testData.tab) {
            NavigationDataProviders.PROFILE_TAB -> {
                navigationRobot.openProfileTab()
                navigationRobot.assertNavigationBarNameText(testData.screenTitle)
            }
            NavigationDataProviders.INBOX_TAB -> {
                navigationRobot.openInbox().assertInboxTitle(testData.screenTitle)
            }
            NavigationDataProviders.FEED_TAB -> {
                navigationRobot.openFeedTab()
                feedRobot.assertSearchButtonVisible()
            }
            NavigationDataProviders.SELL_TAB -> {
                navigationRobot.openSellTab()
                navigationRobot.assertNavigationBarNameText(testData.screenTitle)
            }
            NavigationDataProviders.BROWSE_TAB -> navigationRobot.openBrowseTab().assertBrowseTabIsDisplayed()
        }
    }

    fun openPrivacyScreenFromProfileTab(): CmpWorkflowRobot {
        navigationRobot
            .openProfileTab()
            .openPrivacyScreen()
        return cmpWorkflowRobot
    }
}
