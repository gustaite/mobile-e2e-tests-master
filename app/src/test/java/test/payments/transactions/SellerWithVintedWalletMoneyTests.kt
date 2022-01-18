package test.payments.transactions

import RobotFactory.navigationRobot
import RobotFactory.workflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.data.models.VintedItem
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.base.BaseTest
import api.data.models.VintedUser
import api.controllers.user.notificationSettingsApi
import api.controllers.user.userApi
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToNewUser
import api.factories.UserFactory
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile

@RunMobile(country = VintedCountry.PAYMENTS_EXCEPT_US_PL_CZ_LT, env = VintedEnvironment.SANDBOX)
@LoginToNewUser
@Feature("Buy with Vinted wallet money tests - seller side tests")
class SellerWithVintedWalletMoneyTests : BaseTest() {

    private var loggedInUserItem: VintedItem by thread.lateinit()
    private var withVintedWalletMoneyUser: VintedUser by thread.lateinit()

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Create withVintedWalletMoneyUser and add item")
    fun beforeMethod_a_createWithVintedWalletMoneyUser() {
        withVintedWalletMoneyUser = UserFactory.createRandomUser()
    }

    @BeforeMethod(description = "Add funds to 'withVintedWalletMoneyUser'")
    fun beforeMethod_b_addFundsForWithVintedWalletMoneyUser() {
        withVintedWalletMoneyUser.userApi.addFundsToWalletAndWaitUntilItWillBeAvailable(10.00)
    }

    @BeforeMethod(description = "Create item for logged in user")
    fun beforeMethod_c_createItemForLoggedInUser() {
        loggedInUserItem = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "2"
        )
    }

    @Test(description = "Try to buy an item using money from your vinted wallet (except in US) - check seller balance")
    fun testBuyItemWithVintedWalletMoneyCheckSellerBalance() {
        val transaction = workflowRobot.sellItemToGetMoneyInVintedWallet(buyer = withVintedWalletMoneyUser, seller = loggedInUser, item = loggedInUserItem)
        val invoice = loggedInUser.userApi.currentInvoice().invoiceLines!!.first()
        navigationRobot
            .openProfileTab()
            .openBalanceScreen()
            .assertInvoiceIsVisible(loggedInUserItem.title)
            .assertWalletInvoiceAmountAndItemTitleMatching(
                amount = transaction.buyerDebit!!.sellerShare!!,
                itemName = invoice.subtitle!!,
                invoiceTitle = invoice.title!!
            )
            .assertWithdrawMoneyAndPendingBalanceElementsAreVisible()
    }
}
