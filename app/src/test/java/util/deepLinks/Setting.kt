package util.deepLinks

import RobotFactory.deepLink
import io.qameta.allure.Step
import robot.FavoriteItemsRobot
import robot.HelpCenterRobot
import robot.VintedGuideRobot
import robot.notificationsettings.NotificationSettingsRobot
import util.IOS

class Setting {
    @Step("Open 'Invite friends' screen")
    fun goToInviteFriendsScreen() {
        deepLink.openURL("invite_friends")
    }

    @Step("Open 'Shipping options' screen (only IOS)")
    fun goToShippingOptionsScreen() {
        IOS.doIfiOS {
            deepLink.openURL("user/seller_options")
        }
    }

    @Step("Open 'Bundle discounts' screen")
    fun goToBundleDiscountsScreen() {
        deepLink.openURL("bundle_discounts")
    }

    @Step("Open 'Customization - Categories & Sizes' screen")
    fun goToCustomizationCategoriesAndSizesScreen() {
        deepLink.openURL("customization/categories_sizes")
    }

    @Step("Open push notification settings")
    fun goToPushNotificationSettings(): NotificationSettingsRobot {
        deepLink.openURL("user/settings/notifications")
        return NotificationSettingsRobot()
    }

    @Step("Open 'Subscriptions' screen")
    fun gotoSettingsSubscriptionsPage() {
        deepLink.openURL("settings/subscriptions")
    }

    @Step("Open favorites")
    fun goToFavorites(): FavoriteItemsRobot {
        deepLink.openURL("favorites")
        return FavoriteItemsRobot()
    }

    @Step("Open Help center screen")
    fun goToHelpCenter(): HelpCenterRobot {
        deepLink.openURL("help_center")
        return HelpCenterRobot()
    }

    @Step("Open Vinted guide screen")
    fun goToVintedGuide(): VintedGuideRobot {
        deepLink.openURL("vinted_guide")
        return VintedGuideRobot()
    }
}
