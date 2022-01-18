package test.payments

import RobotFactory.bumpWorkflowRobot
import RobotFactory.deepLink
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import api.data.models.VintedItem
import api.data.responses.getCity
import api.factories.UserFactory
import commonUtil.data.enums.VintedCityEnum.AMSTERDAM
import commonUtil.testng.ResetAppBeforeTest
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.payments.PaymentMethods
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile

@Feature("Bumps Third Party Payments Actions Tests")
class BumpsThirdPartyActionsTests : BaseTest() {
    private var item: VintedItem by thread.lateinit()

    @BeforeMethod(description = "Create item for logged in user")
    fun createItemForLoggedInUser() {
        loggedInUser = UserFactory.createRandomUser()
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "10"
        )
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @ResetAppBeforeTest
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_PAYPAL_COUNTRIES)
    @Test(description = "Test bumping one item through item screen with PayPal")
    @TmsLink("7016")
    fun testBumpingOneItemWithPayPal() {
        bumpWorkflowRobot.bumpItemUsingThirdPartyPaymentMethod(PaymentMethods.PAYPAL, item)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PL)
    @Test(description = "Test bumping one item through item screen with Blik")
    @TmsLink("17800")
    fun testBumpingOneItemWithBlik() {
        bumpWorkflowRobot.bumpItemUsingThirdPartyPaymentMethod(PaymentMethods.BLIK, item)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PL)
    @Test(description = "Test bumping one item through item screen with DotPay")
    @TmsLink("17802")
    fun testBumpingOneItemWithDotPay() {
        bumpWorkflowRobot.bumpItemUsingThirdPartyPaymentMethod(PaymentMethods.DOTPAY, item)
    }

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.INT)
    @Test(description = "Test bumping one item through item screen with iDeal (NL)")
    @TmsLink("23874")
    fun testBumpingOneItemWithIDeal() {
        val city = AMSTERDAM.getCity()
        loggedInUser.userApi.updateCity(city)
        bumpWorkflowRobot.bumpItemUsingThirdPartyPaymentMethod(PaymentMethods.IDEAL, item)
    }

    @ResetAppBeforeTest
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.DE)
    @Test(description = "Test bumping one item through item screen with Sofort")
    @TmsLink("23882")
    fun testBumpingOneItemWithSofort() {
        bumpWorkflowRobot.bumpItemUsingThirdPartyPaymentMethod(PaymentMethods.SOFORT, item)
    }
}
