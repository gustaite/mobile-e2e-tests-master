package test.payments.transactions

import RobotFactory.checkoutWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.offerWorkflowRobot
import RobotFactory.securityWebViewRobot
import api.controllers.user.*
import api.data.models.isNotNull
import api.data.models.transaction.VintedTransactionStatus
import api.data.responses.VintedShipmentDeliveryType
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
import io.qameta.allure.TmsLink
import util.values.Visibility

@LoginToNewUser
@RunMobile(country = VintedCountry.PAYMENTS_EXCEPT_US_PL_UK_CZ_LT, env = VintedEnvironment.SANDBOX)
@Feature("Bundle transaction tests")
class BundleTransactionTests : BaseTest() {

    private var transactionId: Long? by thread.lateinit()

    @BeforeMethod(description = "Disable push notifications")
    fun disableNotifications() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    @BeforeMethod(description = "Add shipping address and credit card to logged in user")
    fun setupBuyer() {
        loggedInUser.userApi.addShippingToAddress()
        loggedInUser.paymentsApi.addCreditCardAsPaymentMethod()
    }

    @Test(description = "Check bundles (go through the transaction)")
    @TmsLink("100")
    fun testCheckBundlesGoThroughTransaction() {
        offerWorkflowRobot
            .goToProfileAndCheckShopButton(withItemsUser)
            .clickShopBundles(withItemsUser)

        offerWorkflowRobot
            .addRemoveBundleElementsAndAssertCheckMarksAndItemImagesCount()
            .addBundleElementsAndContinue(2)

        val conversation = loggedInUser.conversationApi.getFirstConversation()
        transactionId = conversation.order?.transactionId!!

        withItemsUser.shipmentApi.submitRandomStandardPackageSize(transactionId!!)

        deepLink.conversation
            .goToConversation(conversation.id)
            .assertBuyAndMakeOfferButtonVisibility(isVisible = true)
            .clickItemBuyButton()
            .assertAllPricesAreDisplayed()

        checkoutWorkflowRobot
            .selectPickUpOrHomeDeliveryTypeByTransactionAndFillDeliveryDetails(transactionId!!, VintedShipmentDeliveryType.PICK_UP)
            .selectPickUpPointIfNeeded(VintedShipmentDeliveryType.PICK_UP)
            .setShippingContactInformationForBuyerIfRequired(conversation.order!!.items.first())
            .clickBuy()
        securityWebViewRobot
            .simulateSuccessful3dsResponseAfterClickingBuy(transactionId!!)
            .assertMessageInputVisibility(Visibility.Visible)

        val transaction = loggedInUser.transactionApi.waitUntilTransactionIsAvailable(transactionId!!)
        withItemsUser.shipmentApi.shipTransactionByShipmentType(transaction)
        loggedInUser.transactionApi.markTransactionAsDelivered(transaction)
        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertHaveProblemAndEverythingIsOkButtonsVisibility(Visibility.Visible)
            .clickEverythingIsOkButton()
        loggedInUser.transactionApi.waitUntilTransactionStatusBecome(transactionId = transactionId!!, transactionStatus = VintedTransactionStatus.STATUS_COMPLETED, waitTime = 30)
    }

    @AfterMethod(description = "Complete ongoing transactions")
    fun afterMethod() {
        loggedInUser.isNotNull().transactionApi.completeTransactionByTransactionId(txId = transactionId)
    }
}
