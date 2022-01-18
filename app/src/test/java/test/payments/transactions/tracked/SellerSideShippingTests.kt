package test.payments.transactions.tracked

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import api.factories.UserFactory
import api.controllers.*
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.*
import api.data.models.VintedItem
import api.data.models.transaction.VintedShipmentStatus
import api.data.models.transaction.VintedTransaction
import api.data.models.transaction.VintedTransactionStatus
import io.qameta.allure.Feature
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.*
import util.driver.WebDriverFactory.driver
import commonUtil.extensions.isInitialized
import util.image.Screenshot
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import test.basic.states.builders.TransactionStatusDesyncStateMachineBuilder
import util.base.BaseTest
import util.values.ShippingStatusTexts
import util.values.TrackingCodes
import util.values.Visibility

@RunMobile(country = VintedCountry.SANDBOX_PAYMENTS_WITH_TRACKED_SHIPPING, env = VintedEnvironment.SANDBOX)
@Feature("Check shipping status seller side tests")
class SellerSideShippingTests : BaseTest() {
    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()
    private var trackingNumber: String by thread.lateinit()
    private val transactionStateMachine get() = TransactionStatusDesyncStateMachineBuilder().build()

    @BeforeMethod(description = "Create item for logged in user and add transaction with that item and tracked shipping")
    fun createLoggedInUserItemAndAddTransactionWithIt() {
        loggedInUser = UserFactory.createRandomUser()
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )
        loggedInUser.paymentsApi.addPaymentsAccountAndValidateItExists()
        transaction = defaultUser.transactionApi.buyItemWithTrackedShipping(loggedInUser, item)
    }

    @BeforeMethod(description = "Get available tracking number and delete it from AfterShip")
    fun getAvailableTrackingNumberAndDeleteItFromAfterShip() {
        val instructions = loggedInUser.shipmentApi.getShipmentInstructions(transaction)
        trackingNumber = AfterShipAPI.deleteTrackingNumberIfUsed(instructions?.carrier, TrackingCodes.getTrackingNumber(instructions?.carrier!!))
    }

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Check shipping status changes seller side views")
    fun testCheckShippingStatusChangesSellerSideViews() {
        deepLink.conversation.goToConversation(transaction.conversationId)
            .confirmEducationActionIos()
            .assertGoToBalanceAndDeliveryInstructionsButtonsVisibility()
            .clickDeliveryInstructionsButton()
            .enterTrackingNumberAndConfirm(trackingNumber)
            .assertTrackParcelButtonVisibleAndClickIt()
            .assertTrackingNumber(trackingNumber)
            .assertMarkShipmentAsReceivedButtonIsNotVisible()
            .leaveShippingTracking()

        transactionStateMachine
            .addStateOffMethod {
                loggedInUser.transactionApi.getTransactionAndAssertStatus(item, VintedTransactionStatus.STATUS_SHIPPED)
                defaultUser.transactionApi.markTransactionAsDelivered(transaction)
                loggedInUser.transactionApi.getTransactionAndAssertStatus(item, VintedTransactionStatus.STATUS_DELIVERED)
            }
            .addStateAndMethods(TransactionStatusDesyncStateMachineBuilder.TestStates.NASA_SHIPMENT_TRANSACTION_STATUS_DESYNC_FEATURE_ON) {
                loggedInUser.shipmentApi.getShipmentAndAssertStatus(item, VintedShipmentStatus.STATUS_SHIPPED)
                defaultUser.transactionApi.markTransactionAsDelivered(transaction)
                loggedInUser.shipmentApi.getShipmentAndAssertStatus(item, VintedShipmentStatus.STATUS_DELIVERED)
            }.run()

        conversationRobot.assertHaveProblemAndEverythingIsOkButtonsVisibility(Visibility.Invisible)
    }

    @Test(description = "Check shipping tracking history seller side views")
    fun testCheckTrackingHistoryWithTrackedShippingSellerSideViews() {
        loggedInUser.shipmentApi.updateShippingTrackingCode(transaction, trackingNumber)
        deepLink.conversation.goToConversation(transaction.conversationId)
            .confirmEducationActionIos()
            .assertTrackParcelButtonVisibleAndClickIt()
            .assertTrackingNumber(trackingNumber)
            .assertTrackingStatusVisible(ShippingStatusTexts.DELIVERED)
    }

    @AfterMethod(description = "Take screenshot if test failed")
    fun afterMethodTakeScreenShot(result: ITestResult) {
        if (!result.isSuccess) {
            if (!driver.isInitialized()) return
            commonUtil.reporting.Report.addImage(Screenshot.takeScreenshot().screenshot)
        }
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        val completed = defaultUser.transactionApi.completeTransactionByItemId(item = item)
        if (!completed) loggedInUser.skipPartCleanup = true
    }

    @AfterMethod(description = "Terminate web browser opened by the link")
    fun afterMethodTerminateBrowser() {
        Android.terminateChrome()
        IOS.terminateSafari()
    }

    @AfterMethod(description = "Make tracking number available again")
    fun makeTrackingNumberAvailableAgain() {
        if (trackingNumber.isInitialized()) {
            TrackingCodes.removeTrackingCodeFromUsedList(trackingCode = trackingNumber)
        }
    }
}
