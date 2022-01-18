package test.basic.navigation

import RobotFactory.actionBarRobot
import RobotFactory.helpCenterRobot
import RobotFactory.navigationRobot
import RobotFactory.navigationWorkflowRobot
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink

@LoginToMainThreadUser
@RunMobile
@Feature("Navigation from Profile tab tests")
class NavigationFromProfileTests : BaseTest() {

    @Test(description = "Test if navigation from Profile tab to Vinted Guide is working")
    @TmsLink("5274")
    fun testNavigationFromProfileTabToVintedGuide() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.VINTED_GUIDE
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to My Favourites is working")
    fun testNavigationFromProfileTabToMyFavourites() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.MY_FAVOURITES
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Personalisation is working")
    fun testNavigationFromProfileTabToPersonalisation() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.PERSONALISATION
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if navigation from Profile tab to Balance is working")
    fun testNavigationFromProfileTabToBalance() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.BALANCE
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if navigation from Profile tab to My Orders is working")
    fun testNavigationFromProfileTabToMyOrders() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.MY_ORDERS
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Bundle Discounts is working")
    fun testNavigationFromProfileTabToBundleDiscounts() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.BUNDLES_DISCOUNT
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Forum is working")
    fun testNavigationFromProfileTabToForum() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.NEW_FORUM
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if navigation from Profile tab to Invite Friends is working")
    fun testNavigationFromProfileTabToInviteFriends() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.INVITE_FRIENDS
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        actionBarRobot.closeBottomSheetComponentIfVisible()
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Holiday Mode is working")
    fun testNavigationFromProfileTabToHolidayMode() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.HOLIDAY_MODE
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Settings is working")
    fun testNavigationFromProfileTabToSettings() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.SETTINGS
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Privacy manager is working")
    fun testNavigationFromProfileTabToPrivacyManager() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.PRIVACY
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to About Vinted is working")
    fun testNavigationFromProfileTabToAboutVinted() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.ABOUT_VINTED
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Profile tab to Help center is working")
    @TmsLink("68")
    fun testNavigationFromProfileTabToAHelpCenter() {
        val navigationData = NavigationDataProviders.ProfileTabNavigation.HELP_CENTER
        navigationWorkflowRobot.openTabFromMyProfileTab(navigationData.tab)
        helpCenterRobot.assertHelpCenterScreenText(navigationData.screenTitle.trimEnd())
    }
}
