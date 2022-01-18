package test.payments.transactions.untracked

import RobotFactory.problemWorkflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.notificationSettingsApi
import api.controllers.user.paymentsApi
import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.models.isNotNull
import api.data.models.transaction.VintedTransaction
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest

@LoginToNewUser
@RunMobile(country = VintedCountry.SANDBOX_PAYMENT_WITH_UNTRACKED_SHIPPING, env = VintedEnvironment.SANDBOX)
@Feature("Have a problem flow seller side tests")
class SellerSideProblemFlowTests : BaseTest() {

    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()

    @BeforeMethod(description = "Create item for logged in user and add transaction with that item and tracked shipping")
    fun createLoggedInUserItemAndAddTransactionWithIt() {
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )

        loggedInUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        transaction = defaultUser.transactionApi.buyItemWithUntrackedShipping(loggedInUser, item)
    }

    @BeforeMethod(description = "Mark transaction as shipped and delivered")
    fun shipTransactionAndMarkItAsDelivered() {
        loggedInUser.transactionApi.markTransactionAsShipped(transaction)
        defaultUser.transactionApi.markTransactionAsDelivered(transaction)
    }

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @Test(description = "I Have a Problem flow: At the end of TX suspend transaction (seller side views)")
    fun testIHaveAProblemFlowSuspendTransactionSellerSideViews() {
        problemWorkflowRobot.haveProblemSuspendTransactionSellerSideViews(transaction)
    }

    @Test(description = "I Have a Problem flow: At the end of TX suspend transaction, try escalating issue to support (seller side views)")
    fun testIHaveAProblemFlowSuspendTransactionTryEscalatingToSupportSellerSideViews() {
        problemWorkflowRobot.haveProblemSuspendTransactionTryEscalatingToSupportSellerSideViews(transaction)
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        val completed = defaultUser.isNotNull().transactionApi.completeTransactionByItemId(item = item)
        if (!completed) loggedInUser.isNotNull().skipPartCleanup = true
    }
}
