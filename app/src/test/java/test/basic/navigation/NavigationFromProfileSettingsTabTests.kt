package test.basic.navigation

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
@Feature("Navigation from Settings tab tests")
class NavigationFromProfileSettingsTabTests : BaseTest() {

    @Test(description = "Test if navigation from Settings tab to Profile Details tab is working")
    fun testNavigationFromSettingsTabToProfileDetailsTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.PROFILE_DETAILS
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Settings tab to Payments tab is working")
    fun testNavigationFromSettingsTabToPaymentsTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.PAYMENTS
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if navigation from Settings tab to Postage tab is working")
    fun testNavigationFromSettingsTabToPostageTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.POSTAGE
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Settings tab to Push Notifications tab is working")
    fun testNavigationFromSettingsTabToPushNotificationsTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.PUSH_NOTIFICATIONS
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Settings tab to Email Notifications tab is working")
    fun testNavigationFromSettingsTabToEmailNotificationsTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.EMAIL_NOTIFICATIONS
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Settings tab to Data Settings tab is working")
    fun testNavigationFromSettingsTabToDataSettingsTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.DATA_SETTINGS
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }

    @Test(description = "Test if navigation from Settings tab to Security tab is working")
    @TmsLink("23366")
    fun testNavigationFromSettingsTabToSecurityTab() {
        val navigationData = NavigationDataProviders.SettingsTabNavigation.SECURITY
        navigationWorkflowRobot.openTabFromProfileSettingsTab(navigationData.tab)
        navigationRobot.assertNavigationBarNameText(navigationData.screenTitle.trimEnd())
    }
}
