package test.basic.navigation

import RobotFactory.navigationRobot
import RobotFactory.navigationWorkflowRobot
import RobotFactory.userProfileRobot
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile

@LoginToMainThreadUser
@RunMobile
@Feature("Navigation from Navigation bar tests")
class NavigationFromNavigationBarTests : BaseTest() {

    @Test(description = "Test if navigation from navigation bar to profile tab is working")
    fun testNavigationFromNavigationBarToProfileTab() {
        navigationWorkflowRobot.navigationFromNavigationBar(NavigationDataProviders.NavigationBarNavigation.PROFILE)
    }

    @Test(description = "Test if navigation from navigation bar to inbox tab is working")
    fun testNavigationFromNavigationBarToInboxTab() {
        navigationWorkflowRobot.navigationFromNavigationBar(NavigationDataProviders.NavigationBarNavigation.INBOX)
    }

    @Test(description = "Test if navigation from navigation bar to feed tab is working")
    fun testNavigationFromNavigationBarToFeedTab() {
        val feedTab = NavigationDataProviders.NavigationBarNavigation.FEED
        navigationWorkflowRobot.navigationFromNavigationBar(feedTab)
    }

    @Test(description = "Test if navigation from navigation bar to sell tab is working")
    fun testNavigationFromNavigationBarToSellTab() {
        navigationWorkflowRobot.navigationFromNavigationBar(NavigationDataProviders.NavigationBarNavigation.SELL)
    }

    @Test(description = "Test if navigation from navigation bar to browse tab is working")
    fun testNavigationFromNavigationBarToBrowseTab() {
        navigationWorkflowRobot.navigationFromNavigationBar(NavigationDataProviders.NavigationBarNavigation.BROWSE)
    }

    @Test(description = "Test if navigation to catalog is working")
    fun testNavigationToCatalog() {
        navigationRobot
            .openBrowseTab()
            .openAllCategories()
            .assertItemsListIsNotEmpty()
    }

    @Test(description = "Test if navigation to user profile is working")
    fun testNavigationToProfile() {
        navigationRobot
            .openProfileTab()
            .clickOnUserProfile()
        userProfileRobot.assertClosetTabIsVisible()
    }
}
