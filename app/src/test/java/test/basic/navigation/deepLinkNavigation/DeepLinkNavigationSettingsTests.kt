package test.basic.navigation.deepLinkNavigation

import RobotFactory.actionBarRobot
import RobotFactory.deepLink
import RobotFactory.navigationRobot
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedCountry
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile

@RunMobile
@LoginToMainThreadUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationSettingsTests : BaseTest() {

    @Test(description = "Test if deepLink navigation to 'Bundle discounts' screen is working")
    fun testDeepLinkNavigationToBundleDiscountsScreen() {
        deepLink.setting.goToBundleDiscountsScreen()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.bundleDiscountsPageTitle)
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if deepLink navigation to 'Invite friends' screen is working")
    fun testDeepLinkNavigationToInviteFriendsScreen() {
        deepLink.setting.goToInviteFriendsScreen()
        actionBarRobot.closeBottomSheetComponentIfVisible()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.inviteFriendsTitle)
    }

    @RunMobile(platform = VintedPlatform.IOS, message = "Only for IOS")
    @Test(description = "Test if deepLink navigation to 'Seller options' screen is working")
    fun testDeepLinkNavigationToSellerOptionsScreen() {
        deepLink.setting.goToShippingOptionsScreen()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.settingsShippingOptionsTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Customization' screen is working")
    fun testDeepLinkNavigationToCustomizationCategoriesAndSizesScreen() {
        deepLink.setting.goToCustomizationCategoriesAndSizesScreen()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.customizationCategoriesSizesTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Push notifications' screen is working")
    fun testDeepLinkNavigationToPushUpNotificationSettingsScreen() {
        deepLink.setting.goToPushNotificationSettings()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.pushNotificationsTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Email notifications' screen is working")
    fun testDeepLinkNavigationToEmailNotificationSettingsScreen() {
        deepLink.setting.gotoSettingsSubscriptionsPage()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.emailNotificationsTitle)
    }
}
