package test.payments.transactions.labelled

import RobotFactory.conversationWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.inAppNotificationRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.models.transaction.VintedTransaction
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.SkipRetryOnFailure
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@LoginToNewUser
@Feature("Transaction cancellation seller side tests")
class SellerSideTransactionCancellationTests : BaseTest() {
    private var item: VintedItem by thread.lateinit()
    private var transaction: VintedTransaction by thread.lateinit()
    private val waitForDpdLabelTime: Long = 360

    @BeforeMethod(description = "Create item for logged in user and add transaction with that item")
    fun createLoggedInUserItemAndAddTransactionWithIt() {
        loggedInUser.skipPartCleanup = true
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )
        transaction = defaultUser.transactionApi.buyItemWithLabelledShipping(loggedInUser, item)
    }

    @RunMobile(country = VintedCountry.PAYMENTS, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views when transaction is cancelled from buyer side")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromBuyerSide() {
        defaultUser.transactionApi.cancelTransaction(transaction)
        conversationWorkflowRobot
            .goToConversation(transaction)
            .assertTransactionCancelledElementVisibility(Visibility.Visible)
            .assertReuploadItemButtonVisibleAndClickIt()
            .assertTransactionItemsReuploadedElementVisibility(Visibility.Visible)
    }

    @RunMobile(country = VintedCountry.SANDBOX_PAYMENT_COUNTRIES_EXCEPT_DE, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views after transaction with label generated is cancelled from buyer side - seller clicks ok")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromBuyerSide_sellerClicksOk() {
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickGenerateLabel()
            .fillInRecoveryAddressAndGenerateLabel(item)
            .goBackAndToConversationUntilDownloadLabelButtonOrCodeLabelIsVisible(transaction.conversationId)
            .assertDownloadLabelButtonOrCodeLabelIsVisible()

        deepLink.goToFeed()
        defaultUser.transactionApi.cancelTransaction(transaction)
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickOk()
            .assertTransactionCancelledElementVisibility(Visibility.Visible)
            .assertReuploadItemButtonVisibleAndClickIt()
            .assertTransactionItemsReuploadedElementVisibility(Visibility.Visible)
    }

    @SkipRetryOnFailure
    @RunMobile(country = VintedCountry.DE, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views after transaction with label generated is cancelled from buyer side - seller clicks ok")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromBuyerSide_sellerClicksOk_SB_DE() {
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickGenerateLabel()
            .fillInRecoveryAddressAndGenerateLabel(item)
            .goBackAndToConversationUntilDownloadLabelButtonOrCodeLabelIsVisible(transaction.conversationId, waitForDpdLabelTime)
            .assertDownloadLabelButtonOrCodeLabelIsVisible()

        deepLink.goToFeed()
        defaultUser.transactionApi.cancelTransaction(transaction)
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickOk()
            .assertTransactionCancelledElementVisibility(Visibility.Visible)
            .assertReuploadItemButtonVisibleAndClickIt()
            .assertTransactionItemsReuploadedElementVisibility(Visibility.Visible)
    }

    @RunMobile(country = VintedCountry.SANDBOX_PAYMENT_COUNTRIES_EXCEPT_DE, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views after transaction with label generated is cancelled from buyer side - seller clicks already sent")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromBuyerSide_sellerClicksAlreadySent() {
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickGenerateLabel()
            .fillInRecoveryAddressAndGenerateLabel(item)
            .goBackAndToConversationUntilDownloadLabelButtonOrCodeLabelIsVisible(transaction.conversationId)
            .assertDownloadLabelButtonOrCodeLabelIsVisible()

        deepLink.goToFeed()
        defaultUser.transactionApi.cancelTransaction(transaction)
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickAlreadySent()
            .assertTransactionCancelledElementVisibility(Visibility.Invisible)
            .assertDownloadLabelButtonOrCodeLabelIsVisible()
    }

    @RunMobile(country = VintedCountry.PAYMENTS, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views after transaction is cancelled from seller side")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromSellerSide() {
        conversationWorkflowRobot
            .goToConversation(transaction)
            .assertTransactionCancelledElementVisibility(visibility = Visibility.Invisible)
            .openConversationDetails()
            .clickCancelOrderButton()
            .selectReasonAndCancelOrder()
            .assertTransactionCancelledElementVisibility(visibility = Visibility.Visible)
            .assertReuploadItemButtonVisibleAndClickIt()
            .assertTransactionItemsReuploadedElementVisibility(Visibility.Visible)
    }

    @SkipRetryOnFailure
    @RunMobile(country = VintedCountry.DE, env = VintedEnvironment.SANDBOX)
    @Test(description = "Test seller side views after transaction with label generated is cancelled from buyer side - seller clicks already sent")
    fun testSellerSideTransactionCancellationViewsAfterTransactionCancelledFromBuyerSide_sellerClicksAlreadySent_SB_DE() {
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickGenerateLabel()
            .fillInRecoveryAddressAndGenerateLabel(item)
            .goBackAndToConversationUntilDownloadLabelButtonOrCodeLabelIsVisible(transaction.conversationId, waitForDpdLabelTime)
            .assertDownloadLabelButtonOrCodeLabelIsVisible()

        deepLink.goToFeed()
        defaultUser.transactionApi.cancelTransaction(transaction)
        conversationWorkflowRobot
            .goToConversation(transaction)
            .clickAlreadySent()
            .assertTransactionCancelledElementVisibility(Visibility.Invisible)
            .assertDownloadLabelButtonOrCodeLabelIsVisible(waitForDpdLabelTime)
    }

    @SkipRetryOnFailure
    @RunMobile(country = VintedCountry.SANDBOX_PAYMENT_COUNTRIES, env = VintedEnvironment.SANDBOX, platform = VintedPlatform.ANDROID)
    @Test(description = "Test seller receives notification after transaction")
    fun testSellerReceivesNotificationAfterTransaction() {
        inAppNotificationRobot
            .assertInAppNotificationIsVisibleAndClickItAndroid()
            .assertGenerateLabelButtonIsVisible()
    }
}
