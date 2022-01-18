package test.basic.navigation.deepLinkNavigation

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import commonUtil.testng.LoginToNewUser
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile

@RunMobile
@LoginToNewUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationConversationTests : BaseTest() {

    @RunMobile(platform = VintedPlatform.IOS, message = "Test only for IOS")
    @Test(description = "Test id deepLink navigation to 'Notifications' tab is working")
    fun testDeepLinkNavigationToNotificationsTab() {
        deepLink.conversation.goToNotificationsScreen()
        conversationRobot.assertEmptyStateNotificationsTitleIsVisible()
    }

    @Test(description = "Test id deepLink navigation to 'Inbox' tab is working")
    fun testDeepLinkNavigationToInboxTab() {
        deepLink.conversation.goToInbox()
        conversationRobot.assertEmptyStateMessagesTitleIsVisible()
    }
}
