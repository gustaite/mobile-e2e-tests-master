package test.payments.transactions.tracked

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import api.controllers.*
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.paymentsApi
import api.controllers.user.shipmentApi
import api.controllers.user.transactionApi
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
import commonUtil.extensions.isInitialized
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import commonUtil.thread
import test.basic.states.builders.TransactionStatusDesyncStateMachineBuilder
import util.values.ShippingStatusTexts
import util.values.TrackingCodes
import util.values.Visibility

@LoginToDefaultUser
@RunMobile(country = VintedCountry.SANDBOX_PAYMENTS_WITH_TRACKED_SHIPPING, env = VintedEnvironment.SANDBOX)
@Feature("Check shipping status buyer side tests")
class BuyerSideShippingTests : BaseTest() {

    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()
    private var trackingNumber: String by thread.lateinit()
    private val transactionStateMachine get() = TransactionStatusDesyncStateMachineBuilder().build()

    @BeforeMethod(description = "Create item for other user and add transaction with that item and tracked shipment")
    fun createOtherUserItemAndAddTransactionWithIt() {
        item = ItemAPI.uploadItem(
            itemOwner = otherUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )
        otherUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        transaction = loggedInUser.transactionApi.buyItemWithTrackedShipping(otherUser, item)
    }

    @BeforeMethod(description = "Get available tracking number and delete it from AfterShip")
    fun getAvailableTrackingNumberAndDeleteItFromAfterShip() {
        val instructions = otherUser.shipmentApi.getShipmentInstructions(transaction)
        trackingNumber = AfterShipAPI.deleteTrackingNumberIfUsed(instructions?.carrier, TrackingCodes.getTrackingNumber(instructions?.carrier!!))
    }

    @Test(description = "Check shipping status changes buyer side views")
    fun testCheckShippingStatusChangesBuyerSideViews() {
        deepLink.conversation.goToConversation(transaction.conversationId)
        otherUser.shipmentApi.updateShippingTrackingCode(transaction, trackingNumber)
        deepLink.conversation.goToConversation(transaction.conversationId)
        transactionStateMachine
            .addStateOffMethod { loggedInUser.transactionApi.getTransactionAndAssertStatus(item, VintedTransactionStatus.STATUS_SHIPPED) }
            .addStateAndMethods(TransactionStatusDesyncStateMachineBuilder.TestStates.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON) {
                loggedInUser.shipmentApi.getShipmentAndAssertStatus(item, VintedShipmentStatus.STATUS_SHIPPED)
            }.run()

        conversationRobot
            .assertTrackParcelButtonVisibleAndClickIt()
            .assertTrackingNumber(trackingNumber)
            .markShipmentAsReceived()
        transactionStateMachine
            .addStateOffMethod { loggedInUser.transactionApi.getTransactionAndAssertStatus(item, VintedTransactionStatus.STATUS_DELIVERED) }
            .addStateAndMethods(TransactionStatusDesyncStateMachineBuilder.TestStates.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON) {
                loggedInUser.shipmentApi.getShipmentAndAssertStatus(item, VintedShipmentStatus.STATUS_DELIVERED)
            }.run()
        conversationRobot.assertHaveProblemAndEverythingIsOkButtonsVisibility(Visibility.Visible)
    }

    @Test(description = "Check shipping tracking history buyer side views")
    fun testCheckTrackingHistoryWithTrackedShippingBuyerSideViews() {
        otherUser.shipmentApi.updateShippingTrackingCode(transaction, trackingNumber)
        loggedInUser.transactionApi.markTransactionAsDelivered(transaction)
        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertShipmentInformationButtonVisibleAndClickIt()
            .assertTrackingStatusVisible(ShippingStatusTexts.SHIPPED)
            .assertTrackingStatusVisible(ShippingStatusTexts.CONFIRMATION_REQUIRED)
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        val completed = loggedInUser.isNotNull().transactionApi.completeTransactionByItemId(item = item)
        if (!completed) otherUser.isNotNull().skipPartCleanup = true
    }

    @AfterMethod(description = "Make tracking number available again")
    fun makeTrackingNumberAvailableAgain() {
        if (trackingNumber.isInitialized()) {
            TrackingCodes.removeTrackingCodeFromUsedList(trackingCode = trackingNumber)
        }
    }
}
