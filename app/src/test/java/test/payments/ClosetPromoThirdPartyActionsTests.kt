package test.payments

import RobotFactory.closetPromoWorkflowRobot
import RobotFactory.deepLink
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import api.data.responses.getCity
import api.factories.UserFactory
import commonUtil.data.enums.VintedCityEnum.AMSTERDAM
import commonUtil.testng.ResetAppBeforeTest
import commonUtil.testng.SkipRetryOnFailure
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.payments.PaymentMethods
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile

@Feature("Closet Promo Third Party Payments Actions Tests")
class ClosetPromoThirdPartyActionsTests : BaseTest() {

    @BeforeMethod(description = "Create new user with 5 items")
    fun createUserWithFiveItems() {
        loggedInUser = UserFactory.createRandomUser()
        repeat(5) {
            ItemAPI.uploadItem(
                itemOwner = loggedInUser,
                type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
                price = "10"
            )
        }
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @ResetAppBeforeTest
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_PAYPAL_COUNTRIES)
    @Test(description = "Test buying closet promo with PayPal")
    @TmsLink("7014")
    fun testBuyingClosetPromoWithPayPal() {
        closetPromoWorkflowRobot.orderClosetPromoUsingThirdPartyPaymentMethod(PaymentMethods.PAYPAL)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PL)
    @Test(description = "Test buying closet promo with Blik")
    @TmsLink("17803")
    fun testBuyingClosetPromoWithBlik() {
        closetPromoWorkflowRobot.orderClosetPromoUsingThirdPartyPaymentMethod(PaymentMethods.BLIK)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PL)
    @TmsLink("17805")
    @Test(description = "Test buying closet promo with DotPay")
    fun testBuyingClosetPromoWithDotPay() {
        closetPromoWorkflowRobot.orderClosetPromoUsingThirdPartyPaymentMethod(PaymentMethods.DOTPAY)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.INT)
    @Test(description = "Test buying closet promo with iDeal (NL)")
    @TmsLink("23878")
    fun testBuyingClosetPromoWithIDeal() {
        val city = AMSTERDAM.getCity()
        loggedInUser.userApi.updateCity(city)
        closetPromoWorkflowRobot.orderClosetPromoUsingThirdPartyPaymentMethod(PaymentMethods.IDEAL)
    }

    @ResetAppBeforeTest
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.DE)
    @Test(description = "Test buying closet promo with Sofort (DE)")
    @SkipRetryOnFailure
    @TmsLink("23883")
    fun testBuyingClosetPromoWithSofort() {
        closetPromoWorkflowRobot.orderClosetPromoUsingThirdPartyPaymentMethod(PaymentMethods.SOFORT)
    }
}
