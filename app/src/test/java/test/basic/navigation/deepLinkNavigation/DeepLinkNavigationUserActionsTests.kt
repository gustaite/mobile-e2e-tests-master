package test.basic.navigation.deepLinkNavigation

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.LoginToWithItemsUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.EnvironmentManager.isiOS
import util.base.BaseTest
import util.data.NavigationDataProviders
import util.testng.*

@RunMobile
@LoginToWithItemsUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationUserActionsTests : BaseTest() {

    @Test(description = "Test if deepLink navigation to 'Promote closet' screen is working")
    fun testDeepLinkNavigationToPromoteClosetScreen() {
        deepLink.profile.goToClosetPromotion()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.promoteClosetTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Item push multiple selection' screen is working")
    fun testDeepLinkNavigationToPushUpScreen() {
        deepLink.profile.goToPushUpSelect()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.itemPushUpMultipleSelectionPage)
    }

    @Test(description = "Test if deepLink navigation to 'Item push up' screen is working")
    fun testDeepLinkNavigationToItemPushUpScreen() {
        deepLink.profile.goToPushUpReviewOrder(withItemsUserItem)
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.itemPushUpPageTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Sell' screen is working")
    fun testDeepLinkNavigationToSellScreen() {
        deepLink.profile.goToSellScreen()
        navigationRobot.assertNavigationBarNameText(loggedInUser.username)
    }

    @Test(description = "Test if deepLink navigation to 'Leave feedback' screen is working")
    fun testDeepLinkNavigationToLeaveFeedbackScreen() {
        val expectedText = if (isiOS && portal.payments) NavigationDataProviders.profileReviewsPageTitle else NavigationDataProviders.writeFeedbackPageTitle
        deepLink.profile.goToLeaveFeedbackScreen(otherUser.id)
        navigationRobot.assertNavigationBarNameText(expectedText)
    }
}
