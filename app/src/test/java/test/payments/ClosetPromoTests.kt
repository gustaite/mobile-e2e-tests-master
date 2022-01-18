package test.payments

import RobotFactory.bumpsCheckoutRobot
import RobotFactory.closetPromoCheckoutRobot
import RobotFactory.closetPromoWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.paymentMethodsRobot
import RobotFactory.paymentsScreenRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import api.data.models.VintedUser
import api.factories.UserFactory
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.CreditCardDetails
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue

@Feature("Closet promo tests")
class ClosetPromoTests : BaseTest() {

    private var user: VintedUser? by thread.lateinit()

    @BeforeMethod(description = "Create new user with 5 items and login to that user")
    fun createUserWithFiveItemsAndLoginToIt() {
        user = UserFactory.createRandomUser()
        repeat(5) {
            ItemAPI.uploadItem(
                itemOwner = user!!,
                type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
                price = "10"
            )
        }
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_COUNTRIES_WITH_CLOSET_PROMO)
    @Issue("MARIOS-540")
    @Test(description = "Test buying closet promo with Credit Card")
    @TmsLinks(TmsLink("17796"), TmsLink("17892"), TmsLink("17886"))
    fun testBuyingClosetPromoWithCreditCard() {
        deepLink.loginToAccount(user!!)

        closetPromoWorkflowRobot
            .selectToOrderClosetPromoAndCheckItsElements()
        paymentMethodsRobot
            .selectNewCreditCardPaymentOptionForVAS()
            .insertNewCreditCardInfo(CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        closetPromoCheckoutRobot
            .assertOrderSummaryIsDisplayed()
            .confirmOrder()
        closetPromoWorkflowRobot
            .clickOnPromoStatisticsBannerAndAssertStatisticsHeader()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_COUNTRIES_WITH_CLOSET_PROMO)
    @Issue("MARIOS-540")
    @Test(description = "Test buying closet promo with card not saved")
    @TmsLinks(TmsLink("7019"), TmsLink("17796"))
    fun testBuyingClosetPromoCardNotSaved() {
        deepLink.loginToAccount(user!!)

        closetPromoWorkflowRobot
            .selectToOrderClosetPromoAndCheckItsElements()
        paymentMethodsRobot
            .selectNewCreditCardPaymentOptionForVAS()
            .unselectSaveCreditCardOption()
            .insertNewCreditCardInfo(CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        closetPromoCheckoutRobot
            .assertOrderSummaryIsDisplayed()
            .confirmOrder()
        closetPromoWorkflowRobot
            .clickOnPromoStatisticsBannerAndAssertStatisticsHeader()
        deepLink.goToFeed()
        deepLink
            .goToSettings()
            .openPaymentsSettings()
            .checkIfCreditCardDeleted()
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_US_PL_CZ_LT)
    @Test(description = "Test buying closet promo with Vinted wallet")
    @TmsLinks(TmsLink("17797"), TmsLink("17893"), TmsLink("17887"))
    fun testBuyingClosetPromoWithVintedWallet() {
        user!!.userApi.addFundsToWalletAndWaitUntilItWillBeAvailable(10.00)
        deepLink.loginToAccount(user!!)

        closetPromoWorkflowRobot
            .selectToOrderClosetPromoAndCheckItsElements()
        bumpsCheckoutRobot
            .assertPayWithVintedWalletOptionIsVisible()
        paymentsScreenRobot
            .selectWalletPaymentOptionForIos()
        closetPromoCheckoutRobot
            .confirmOrder()
        closetPromoWorkflowRobot
            .clickOnPromoStatisticsBannerAndAssertStatisticsHeader()
    }
}
