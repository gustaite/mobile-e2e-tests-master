package test.payments.transactions

import RobotFactory.bundleRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.offerWorkflowRobot
import api.controllers.user.conversationApi
import api.controllers.user.offerApi
import api.controllers.user.shipmentApi
import api.data.requests.VintedOfferRequest
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.base.BaseTest
import util.testng.*

@LoginToNewUser
@RunMobile(country = VintedCountry.PAYMENTS)
@Feature("Offer flow buyer side tests")
class BuyerSideOfferFlowTests : BaseTest() {

    @Test(description = "Test offer flow views from buyer side buyer sends offer")
    fun testOfferFlowViewsFromBuyerSideBuyerSendsOffer() {
        val firstOfferPrice = 10.25
        val secondOfferPrice = 15.66
        val thirdOfferPrice = 14.77

        offerWorkflowRobot
            .goToProfileAndCheckShopButton(withItemsUser)
            .clickShopBundles(withItemsUser)

        offerWorkflowRobot
            .addRemoveBundleElementsAndAssertCheckMarksAndItemImagesCount()
            .addBundleElementsAndContinue(2)

        val conversation = loggedInUser.conversationApi.getFirstConversation()
        val transactionId = conversation.order?.transactionId!!

        withItemsUser.shipmentApi.submitRandomStandardPackageSize(transactionId)
        deepLink.conversation
            .goToConversation(conversation.id)
            .clickItemBuyButton()
            .assertAllPricesAreDisplayed()
            .leaveCheckout()

        offerWorkflowRobot
            .makeOfferAndAssertPrice(firstOfferPrice, conversation.id)
        withItemsUser.offerApi.rejectPendingOfferRequest(transactionId, conversation.id)
        deepLink.conversation.goToConversation(conversation.id)

        offerWorkflowRobot
            .assertOfferPriceAndStatus(firstOfferPrice, conversation.id, VintedOfferRequest.OFFER_STATUS_REJECTED)
            .makeOfferAndAssertPrice(secondOfferPrice, conversation.id)
            .makeOfferAndAssertPrice(thirdOfferPrice, conversation.id)

        withItemsUser.offerApi.acceptPendingOfferRequest(transactionId, conversation.id)
        deepLink.conversation.goToConversation(conversation.id)

        offerWorkflowRobot
            .assertOfferPriceAndStatus(secondOfferPrice, conversation.id, VintedOfferRequest.OFFER_STATUS_CANCELED)
            .assertOfferPriceAndStatus(thirdOfferPrice, conversation.id, VintedOfferRequest.OFFER_STATUS_ACCEPTED)

        conversationRobot
            .offerActionsRobot.assertItemRequestBuyButtonVisibleAndClick()
            .assertAllPricesAreDisplayed()
    }

    @Test(description = "Test offer flow views from buyer side seller sends offer")
    fun testOfferFlowViewsFromBuyerSideSellerSendsOffer() {
        val firstOfferPrice = 10.25

        offerWorkflowRobot
            .goToProfileAndCheckShopButton(withItemsUser)
            .clickShopBundles(withItemsUser)

        bundleRobot.addBundleElementsAndContinue(2)
        val conversation = loggedInUser.conversationApi.getFirstConversation()
        val transactionId = conversation.order?.transactionId!!

        withItemsUser.shipmentApi.submitRandomStandardPackageSize(transactionId)
        withItemsUser.offerApi.createTransactionOffer(transactionId, 14)
        deepLink.conversation.goToConversation(conversation.id)

        conversationRobot
            .offerActionsRobot.assertAcceptAndDeclineButtonsAreNotVisible()
            .offerActionsRobot.assertItemRequestBuyButtonVisibleAndClick()
            .assertAllPricesAreDisplayed()
            .leaveCheckout()

        offerWorkflowRobot
            .makeOfferAndAssertPrice(firstOfferPrice, conversation.id)
        withItemsUser.offerApi.acceptPendingOfferRequest(transactionId, conversation.id)
        deepLink.conversation.goToConversation(conversation.id)

        offerWorkflowRobot
            .assertOfferPriceAndStatus(firstOfferPrice, conversation.id, VintedOfferRequest.OFFER_STATUS_ACCEPTED)

        conversationRobot
            .offerActionsRobot.assertItemRequestBuyButtonVisibleAndClick()
            .assertAllPricesAreDisplayed()
    }
}
