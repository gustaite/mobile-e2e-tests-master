package test.payments

import RobotFactory.checkoutWorkflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder.VintedType.SIMPLE_ITEM
import api.controllers.item.ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING
import api.controllers.item.ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING
import api.controllers.item.getUserItems
import api.controllers.user.paymentsApi
import api.controllers.user.transactionApi
import api.data.models.isNotNull
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.testng.CreateOneTestUser
import io.qameta.allure.Feature
import org.testng.annotations.*
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.ResetAppAfterTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import io.qameta.allure.Issues
import io.qameta.allure.TmsLink
import util.base.BaseTest

@Feature("Transaction tests")
@LoginToNewUser
@CreateOneTestUser
class TransactionsTests : BaseTest() {

    @BeforeMethod
    fun addPaymentsAccountForOneTestUser() {
        oneTestUser.paymentsApi.addPaymentsAccountAndValidateItExists()
    }

    @RunMobile(
        env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_US,
        message = "Test for payment countries except US because it has no pick-up shipping"
    )

    @ResetAppAfterTest
    @Test(description = "Test buying an item with pick-up shipping method in sandbox")
    @TmsLink("27908")
    fun testBuyingAnItemWithPickUpShippingMethodInSandbox() {
        val oneTestUserItem = ItemAPI.uploadItem(oneTestUser, SIMPLE_ITEM, price = "25.00")
        checkoutWorkflowRobot.buyAnItemWithPickUpOrHomeDeliveryMethod(oneTestUserItem, VintedShipmentDeliveryType.PICK_UP)
    }

    @Issue("ZEBRA-1066")
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_CZ_LT, message = "Test for payment countries except CZ, LT because it has no home shipping")
    @ResetAppAfterTest
    @Test(description = "Test buying an item with home delivery shipping method in sandbox")
    @TmsLink("27909")
    fun testBuyingAnItemWithHomeDeliveryShippingMethodInSandbox() {
        val oneTestUserItem = ItemAPI.uploadItem(oneTestUser, SIMPLE_ITEM, price = "25.00")
        checkoutWorkflowRobot.buyAnItemWithPickUpOrHomeDeliveryMethod(oneTestUserItem, VintedShipmentDeliveryType.HOME)
    }

    // Buy button was restricted on US for CUSTOM and NO_SHIPPING https://github.com/vinted/core/pull/57060
    @Issue("ZEBRA-1066")
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_US, message = "Test for payment countries only")
    @ResetAppAfterTest
    @Test(description = "Test buying an item with custom shipping method in sandbox")
    @TmsLink("27910")
    fun testBuyingAnItemWithCustomShippingMethodInSandbox() {
        val itemType = SIMPLE_ITEM_WITH_CUSTOM_SHIPPING
        val customShippingItem = ItemAPI.uploadItem(oneTestUser, itemType, price = "25.00")
        checkoutWorkflowRobot.buyAnItemWithCustomOrNoShippingMethod(customShippingItem, itemType)
    }

    // Buy button was restricted on US for CUSTOM and NO_SHIPPING https://github.com/vinted/core/pull/57060
    @Issues(Issue("MARIOS-540"), Issue("ZEBRA-1066"))
    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.PAYMENTS_EXCEPT_US, message = "Test for payment countries only")
    @ResetAppAfterTest
    @Test(description = "Test buying an item with no shipping method in sandbox")
    @TmsLink("27911")
    fun testBuyingAnItemWithNoShippingMethodInSandbox() {
        val itemType = SIMPLE_ITEM_WITH_NO_SHIPPING
        val noShippingItem = ItemAPI.uploadItem(oneTestUser, itemType, price = "25.00")
        checkoutWorkflowRobot.buyAnItemWithCustomOrNoShippingMethod(noShippingItem, itemType)
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        oneTestUser.isNotNull().getUserItems().forEach { item ->
            loggedInUser.isNotNull().transactionApi.completeTransactionByItemId(item)
        }
    }
}
