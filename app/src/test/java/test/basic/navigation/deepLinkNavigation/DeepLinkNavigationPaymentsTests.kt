package test.basic.navigation.deepLinkNavigation

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.absfeatures.AbTestController.isKycEducationOn
import util.base.BaseTest
import util.data.NavigationDataProviders

@RunMobile
@LoginToMainThreadUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationPaymentsTests : BaseTest() {

    @Test(description = "Test if deepLink navigation to 'Add credit card' screen is working")
    fun testDeepLinkNavigationToAddCreditCardScreen() {
        deepLink.payment.goToAddCreditCard()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.addCreditCardTitle)
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if deepLink navigation to 'Activate wallet' screen is working")
    fun testDeepLinkNavigationToActivateWalletScreen() {
        deepLink.payment.goToActivateBalance()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.activateWalletTitle)
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if deepLink navigation to 'Payments Identity' screen is working")
    fun testDeepLinkNavigationToPaymentsIdentityScreen() {
        val paymentsIdentityPageTitle = if (isKycEducationOn())
            NavigationDataProviders.paymentsIdentityEducationPageTitle else NavigationDataProviders.paymentsIdentityPageTitle
        deepLink.payment.goToPaymentsIdentity()
        navigationRobot.assertNavigationBarNameText(paymentsIdentityPageTitle)
    }

    @RunMobile(country = VintedCountry.PAYMENTS)
    @Test(description = "Test if deepLink navigation to 'Wallet' screen is working")
    fun testDeepLinkNavigationToWalletScreen() {
        deepLink.payment.goToWallet()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.walletPageTitle)
    }
}
