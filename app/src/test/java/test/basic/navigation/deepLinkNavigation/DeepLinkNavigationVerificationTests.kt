package test.basic.navigation.deepLinkNavigation

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders

@RunMobile
@LoginToNewUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationVerificationTests : BaseTest() {
    @Test(description = "Test if deepLink navigation to 'Phone change' screen is working")
    fun testDeepLinkNavigationToPhoneChangeScreen() {
        deepLink.verification.goToPhoneChangeScreen()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.phoneEditPageTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Phone verification' screen is working")
    fun testDeepLinkNavigationToPhoneVerificationScreen() {
        deepLink.verification.goToPhoneVerificationScreen()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.phoneVerificationPageTitle.trim())
    }

    @Test(description = "Test if deepLink navigation to 'New Email change confirmation' screen is working'")
    fun testDeepLinkNavigationToEmailChangeConfirmationScreen() {
        deepLink.verification.goToEmailChangeConfirmationScreen(loggedInUser.id)
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.emailChangePageTitle)
    }
}
