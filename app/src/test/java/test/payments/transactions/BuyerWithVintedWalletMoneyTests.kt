package test.payments.transactions

import RobotFactory.checkoutRobot
import RobotFactory.deepLink
import RobotFactory.navigationRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.*
import api.data.models.VintedItem
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.EnvironmentManager.preferredShipmentType
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.LoginToNewUser
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@RunMobile(country = VintedCountry.PAYMENTS_EXCEPT_US_PL_CZ_LT, env = VintedEnvironment.SANDBOX)
@LoginToNewUser
@CreateOneTestUser
@Feature("Buy with Vinted wallet money tests - buyer side tests")
class BuyerWithVintedWalletMoneyTests : BaseTest() {
    private var oneTestUserItem: VintedItem by thread.lateinit()

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Create item for oneTestUser")
    fun beforeMethod_a_createOneTestUserWithItem() {
        oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "2"
        )
        oneTestUser.paymentsApi.addPaymentsAccountAndValidateItExists()
    }

    @BeforeMethod(description = "Add funds to loggedInUser wallet")
    fun beforeMethod_b_addMoneyForLoggedInUserInVintedWallet() {
        loggedInUser.skipPartCleanup = true
        loggedInUser.userApi.addFundsToWalletAndWaitUntilItWillBeAvailable(10.00)
    }

    @Test(description = "Try to buy an item using money from your vinted wallet (except in US)")
    fun testBuyItemWithVintedWalletMoney() {
        loggedInUser.userApi.addShippingToAddress()
        loggedInUser.paymentsApi.addCreditCardAsPaymentMethod()
        loggedInUser.transactionApi.createConversationWithTransactionAndPreestimateItForInitialStatus(
            oneTestUser,
            oneTestUserItem,
            preferredShipmentType
        )

        deepLink.item.goToItem(oneTestUserItem)
            .clickBuyButton()
            .assertAllPricesAreDisplayed()
            .assertWalletAmountIsDisplayed()
        checkoutRobot
            .clickBuy()
            .assertMessageInputVisibility(Visibility.Visible)
        deepLink.goToFeed()
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .assertInvoiceIsVisible(oneTestUserItem.title)
    }
}
