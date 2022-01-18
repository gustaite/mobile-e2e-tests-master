package test.payments.transactions

import RobotFactory.deepLink
import RobotFactory.offerWorkflowRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.bundleApi
import api.data.models.VintedItem
import api.data.requests.VintedOfferRequest
import io.qameta.allure.Feature
import org.testng.annotations.*
import util.base.BaseTest
import util.EnvironmentManager.isAndroid
import api.controllers.user.conversationApi
import api.controllers.user.offerApi
import commonUtil.testng.LoginToNewUser
import util.testng.*
import commonUtil.thread
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@LoginToNewUser
@CreateOneTestUser
@RunMobile(country = VintedCountry.PAYMENTS)
@Feature("Offer flow seller side tests")
class SellerSideOfferFlowTests : BaseTest() {
    private var item: VintedItem? by thread.lateinit()
    private var item2: VintedItem? by thread.lateinit()

    @BeforeMethod(description = "Create two items for logged in user")
    fun createTwoItemsForLoggedInUser() {
        item = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )

        item2 = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "6"
        )
    }

    @Test(description = "Test offer flow views from seller side")
    fun testOfferFlowViewFromSellerSide() {
        val firstBuyerOfferAmount = 7.99
        val secondBuyerOfferAmount = 8.99

        val transaction = oneTestUser.bundleApi.createBundleWithItems(item!!, item2!!)
        loggedInUser.conversationApi.getFirstConversation()

        deepLink.conversation
            .goToConversation(transaction.conversationId)
            .assertMarkAsReservedButtonVisibility(visibility = Visibility.Visible)
            .openParcelSizeSelectionForm()
            .selectParcelSizeAndSubmit()
            .assertMarkAsReservedButtonVisibility(visibility = if (isAndroid) Visibility.Invisible else Visibility.Visible)

        offerWorkflowRobot.makesOfferFromSellerSideAndAssertPrice(10.99)
        oneTestUser.offerApi.createOfferRequest(transaction.id, firstBuyerOfferAmount)

        deepLink.conversation.goToConversation(transaction.conversationId)
        offerWorkflowRobot
            .assertOfferPriceAndStatus(firstBuyerOfferAmount, transaction.conversationId, VintedOfferRequest.OFFER_STATUS_PENDING)
            .declineItemRequestOffer()
            .makeOfferThroughOfferYourPriceButtonAndAssertPrice(9.99)
        oneTestUser.offerApi.createOfferRequest(transaction.id, secondBuyerOfferAmount)

        deepLink.conversation
            .goToConversation(transaction.conversationId)
            .offerActionsRobot.acceptItemRequestOffer()

        offerWorkflowRobot
            .assertOfferPriceAndStatus(firstBuyerOfferAmount, transaction.conversationId, VintedOfferRequest.OFFER_STATUS_REJECTED)
            .assertOfferPriceAndStatus(secondBuyerOfferAmount, transaction.conversationId, VintedOfferRequest.OFFER_STATUS_ACCEPTED)
    }
}
