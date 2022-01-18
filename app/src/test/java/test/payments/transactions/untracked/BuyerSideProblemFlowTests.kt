package test.payments.transactions.untracked

import RobotFactory.conversationRobot
import RobotFactory.problemWorkflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.conversationApi
import api.controllers.user.helpCenterApi
import api.controllers.user.paymentsApi
import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.models.isNotNull
import api.data.models.transaction.VintedTransaction
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import util.testng.*
import commonUtil.thread
import util.values.Visibility

@LoginToDefaultUser
@RunMobile(country = VintedCountry.SANDBOX_PAYMENT_WITH_UNTRACKED_SHIPPING, env = VintedEnvironment.SANDBOX)
@Feature("Have a problem flow buyer side tests")
class BuyerSideProblemFlowTests : BaseTest() {

    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()

    @BeforeMethod(description = "Create item for other user and add transaction with that item and tracked shipment")
    fun createOtherUserItemAndAddTransactionWithIt() {
        item = ItemAPI.uploadItem(
            itemOwner = otherUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )
        otherUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        transaction = loggedInUser.transactionApi.buyItemWithUntrackedShipping(otherUser, item)
    }

    @BeforeMethod(description = "Mark transaction as shipped and delivered")
    fun shipTransactionAndMarkItAsDelivered() {
        otherUser.transactionApi.markTransactionAsShipped(transaction)
        loggedInUser.transactionApi.markTransactionAsDelivered(transaction)
    }

    @Test(description = "I Have a Problem flow: At the end of TX suspend transaction (buyer side views)")
    fun testIHaveAProblemFlowSuspendTransactionBuyerSideViews() {
        problemWorkflowRobot.haveProblemSuspendTransactionBuyerSideViews(transaction)
        val complaintId = loggedInUser.conversationApi.getComplaintId(conversationId = otherUser.conversationApi.getFirstConversation().id)
        conversationRobot.assertTransactionSuspendedElementVisible()
        otherUser.helpCenterApi.resolveComplaint(complaintId)
        conversationRobot.assertRefundProcessedElementVisibility(Visibility.Visible)
    }

    @Test(description = "I Have a Problem flow: At the end of TX suspend transaction, try escalating issue to support (buyer side views)")
    fun testIHaveAProblemFlowSuspendTransactionTryEscalatingToSupportBuyerSideViews() {
        problemWorkflowRobot.haveProblemSuspendTransactionTryEscalatingToSupportBuyerSideViews(transaction)
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        val completed = loggedInUser.isNotNull().transactionApi.completeTransactionByItemId(item = item)
        if (!completed) otherUser.isNotNull().skipPartCleanup = true
    }
}
