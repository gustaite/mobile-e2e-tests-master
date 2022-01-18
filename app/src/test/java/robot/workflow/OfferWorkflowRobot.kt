package robot.workflow

import RobotFactory.bundleRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.userProfileRobot
import api.controllers.user.conversationApi
import api.controllers.user.userApi
import api.data.models.VintedUser
import api.data.requests.VintedOfferRequest
import commonUtil.asserts.VintedAssert
import commonUtil.extensions.adaptPrice
import io.qameta.allure.Step
import robot.item.BundleRobot
import util.*
import commonUtil.extensions.numeric
import util.base.BaseTest
import java.text.DecimalFormat

class OfferWorkflowRobot {

    @Step("From buyer side make an offer for bundle items and assert price is visible")
    fun makeOfferAndAssertPrice(price: Double, conversationId: Long): OfferWorkflowRobot {
        conversationRobot
            .clickItemMakeOfferButton()
            .makeAnOfferRequest(price.numeric())

        assertOfferPriceAndStatus(price, conversationId, VintedOfferRequest.OFFER_STATUS_PENDING)
        return this
    }

    @Step("From seller side make an offer for bundled items and assert price is visible")
    fun makesOfferFromSellerSideAndAssertPrice(price: Double): OfferWorkflowRobot {
        val priceNumeric = price.numeric().adaptPrice()
        conversationRobot
            .clickItemMakeOfferButton()
            .makeAnOfferRequest(priceNumeric)
        assertOfferPrice(price)
        return this
    }

    @Step("From seller side make offer through 'Offer your price' button and assert price")
    fun makeOfferThroughOfferYourPriceButtonAndAssertPrice(price: Double): OfferWorkflowRobot {
        val priceNumeric = DecimalFormat("0.00").format(price)
        conversationRobot.offerActionsRobot.makeOfferThroughOfferYourPriceButton(priceNumeric)
        assertOfferPrice(price)
        return this
    }

    @Step("Assert offer price is {price} and status is {status}")
    fun assertOfferPriceAndStatus(price: Double, conversationId: Long, status: Int): OfferWorkflowRobot {
        val priceFormatted = PriceFactory.getFormattedPriceWithCurrencySymbol(price, replaceSpaceCharToSpec = true)
        val offer = BaseTest.loggedInUser.conversationApi.getOfferRequestFromConversation(conversationId, status)
        conversationRobot.offerActionsRobot.assertOfferRequestPriceAndStatus(priceFormatted, offer.statusTitle)
        return this
    }

    @Step("Assert offer price is {price} and status is {status}")
    fun assertOfferPrice(price: Double): OfferWorkflowRobot {
        val priceFormatted = PriceFactory.getFormattedPriceWithCurrencySymbol(price)
        conversationRobot.offerActionsRobot.assertOfferPrice(priceFormatted)
        return this
    }

    @Step("Add bundle element and assert check mark and bundle item image are visible, then remove element and assert check mark and bundle item image are not visible anymore")
    fun addRemoveBundleElementsAndAssertCheckMarksAndItemImagesCount(): BundleRobot {
        bundleRobot
            .addBundleElements(1)
            .assertCheckMarksAreVisible(1)
            .assertBundleItemImagesAreVisible(1)
            .removeBundleElements(1)
            .assertCheckMarksAreVisible(0)
            .assertBundleItemImagesAreVisible(0)
        return bundleRobot
    }

    @Step("Decline item request offer")
    fun declineItemRequestOffer(): OfferWorkflowRobot {
        conversationRobot.offerActionsRobot.declineItemRequestOffer()
        return this
    }

    @Step("For 240 seconds repeat going to user profile until shop button become visible (only IOS)")
    fun goToProfileAndCheckShopButton(user: VintedUser): OfferWorkflowRobot {
        IOS.doIfiOS {
            commonUtil.Util.retryUntil(
                block = {
                    deepLink.goToFeed()
                    deepLink.profile.goToUserProfile(user.id)
                    userProfileRobot.isShopButtonIsVisible()
                },
                tryForSeconds = 240
            )
        }
        Android.doIfAndroid { deepLink.profile.goToUserProfile(user.id) }
        return this
    }

    @Step("Wait until at least one item returned through api and click shop button")
    fun clickShopBundles(user: VintedUser) {
        VintedAssert.assertTrue(userProfileRobot.isShopButtonIsVisible(), "Shop button should be visible")

        // Wait until bundle items will be returned
        commonUtil.Util.retryUntil(
            block = {
                user.userApi.getBundleItems().count() > 0
            },
            tryForSeconds = 5
        )

        userProfileRobot.clickShopButton()
    }
}
