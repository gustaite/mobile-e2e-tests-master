package test.payments.transactions.labelled

import RobotFactory.problemWorkflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.*
import api.data.models.VintedItem
import api.data.models.isNotNull
import api.data.models.transaction.VintedShipmentStatus
import api.data.models.transaction.VintedTransaction
import api.data.models.transaction.VintedTransactionStatus
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import test.basic.states.builders.TransactionStatusDesyncStateMachineBuilder

@LoginToNewUser
@RunMobile(country = VintedCountry.SANDBOX_PAYMENT_COUNTRIES_EXCEPT_DE, env = VintedEnvironment.SANDBOX)
@Feature("Have a problem flow seller side tests")
class SellerSideProblemFlowTests : BaseTest() {

    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()
    private val transactionStateMachine get() = TransactionStatusDesyncStateMachineBuilder().build()

    @BeforeMethod(description = "Create item for logged in user and add transaction with that item and labelled shipping")
    fun createLoggedInUserItemAndAddTransactionWithIt() {
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )

        loggedInUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        transaction = defaultUser.transactionApi.buyItemWithLabelledShipping(loggedInUser, item)
    }

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Generate shipping label and mark transaction as delivered")
    fun generateShippingLabelAndMarkTransactionAsDelivered() {
        val address = loggedInUser.shipmentApi.updateDeliveryAddress(transaction)
        loggedInUser.transactionApi.waitUntilTransactionStatusBecome(transaction.id, VintedTransactionStatus.STATUS_DEBIT_PROCESSED)
        loggedInUser.shipmentApi.getShippingLabelWithRetry(transaction, address)
        transactionStateMachine
            .addStateOffMethod {
                loggedInUser.transactionApi.waitUntilTransactionStatusBecome(
                    transactionId = transaction.id,
                    transactionStatus = VintedTransactionStatus.STATUS_SHIPMENT_LABEL_SENT
                )
            }
            .addStateAndMethods(TransactionStatusDesyncStateMachineBuilder.TestStates.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON) {
                loggedInUser.shipmentApi.waitUntilShipmentStatusBecomes(
                    transactionId = transaction.id,
                    shipmentStatus = VintedShipmentStatus.STATUS_LABEL_SENT
                )
            }.run()
        defaultUser.transactionApi.markTransactionAsDelivered(transaction)
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
