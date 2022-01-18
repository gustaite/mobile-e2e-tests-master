package robot.inbox.conversation

import RobotFactory.checkoutRobot
import RobotFactory.conversationRobot
import commonUtil.asserts.VintedAssert
import commonUtil.extensions.changeSimpleSpaceToSpecial
import io.qameta.allure.Step
import robot.BaseRobot
import robot.CheckoutRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.extensions.escapeApostrophe
import commonUtil.extensions.removeSpecialSpaceAndMinus

class OfferActionsRobot : BaseRobot() {
    private val buyNowOfferButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.scrollableIdWithText(
                        "offer_request_primary_action",
                        Android.getElementValue("conversation_offer_request_action_buy_now")
                    ),
                    androidBy2 = VintedBy.androidIdAndText("offer_primary_action", Android.getElementValue("conversation_offer_request_action_buy_now"))
                )
            },
            iOSBy = VintedBy.accessibilityId("buy")
        )

    private val offerYourPriceButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                "offer_request_primary_action",
                Android.getElementValue("conversation_offer_request_action_suggest_your_price")
            ),
            iOSBy = VintedBy.accessibilityId("offer")
        )

    private val acceptOfferButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                "offer_request_primary_action",
                Android.getElementValue("conversation_offer_request_action_accept")
            ),
            iOSBy = VintedBy.accessibilityId("accept")
        )

    private val declineOfferButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                "offer_request_secondary_action",
                Android.getElementValue("conversation_offer_request_action_reject")
            ),
            iOSBy = VintedBy.accessibilityId("reject")
        )

    private val offerRequestAmountElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId("offer_request_amount_and_status"),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeStaticText[`${IOS.predicateWithCurrencySymbols}`]")
        )

    private fun offerAmountElement(price: String) = VintedDriver.findElement(
        VintedBy.scrollableIdWithText("offer_price", price.changeSimpleSpaceToSpecial()),
        VintedBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeStaticText[`value CONTAINS '$price'`]")
    )

    private fun offerRequestAmountElement(status: String) =
        VintedDriver.findElement(
            VintedBy.scrollableIdWithText("offer_request_amount_and_status", status.removeSpecialSpaceAndMinus()),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell[\$name CONTAINS '${status.escapeApostrophe()}'\$]/**/XCUIElementTypeStaticText[`${IOS.predicateWithCurrencySymbolsGrouped}`]")
        )

    private fun offerRequestStatusElementIos(status: String): VintedElement {
        val escapedStatus = status.escapeApostrophe()
        return VintedDriver.findElement(iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[\$name CONTAINS '$escapedStatus'\$]/**/XCUIElementTypeStaticText[`value CONTAINS '$escapedStatus'`]"))
    }

    @Step("Assert offer request price is {price}")
    fun assertOfferRequestPrice(price: String) {
        val offerText = offerRequestAmountElement.text
        commonUtil.reporting.Report.addMessage("Offer text: $offerText \nExpected price: $price")
        PriceFactory.assertContains(offerText, price, "Offer does not contain expected price")
    }

    @Step("Assert offer request price is {price} and status is {status}")
    fun assertOfferRequestPriceAndStatus(price: String, status: String) {
        var actualText = ""
        offerRequestAmountElement(status).let { element ->
            commonUtil.Util.retryUntil(block = { actualText = element.text; actualText.isNotEmpty() }, tryForSeconds = 10)
        }
        commonUtil.reporting.Report.addMessage("Offer text: $actualText \nExpected price: $price")
        PriceFactory.assertContains(actualText, price, "Offer does not contain expected price")

        if (isAndroid) {
            VintedAssert.assertTrue(actualText.contains(status), "Offer does not contain expected status. Expected: $status")
        } else {
            VintedAssert.assertTrue(offerRequestStatusElementIos(status).isVisible(), "Offer status element should be visible")
            VintedAssert.assertTrue(offerRequestStatusElementIos(status).text.contains(status), "Offer does not contain expected status. Expected: $status")
        }
    }

    @Step("Assert offer price is {price}")
    fun assertOfferPrice(price: String) {
        val actualText = offerAmountElement(price).text
        commonUtil.reporting.Report.addMessage("Offer text: $actualText \nExpected price: $price")
        PriceFactory.assertContains(actualText, price, "Offer does not contain expected price")
    }

    @Step("Assert item request offer buy button is visible and click it")
    fun assertItemRequestBuyButtonVisibleAndClick(): CheckoutRobot {
        VintedAssert.assertTrue(buyNowOfferButton.isVisible(), "Item request buy button should be visible")
        buyNowOfferButton.click()
        return checkoutRobot
    }

    @Step("Assert item request decline button is visible and click it")
    fun declineItemRequestOffer(): ConversationRobot {
        VintedAssert.assertTrue(declineOfferButton.isVisible(), "Item request decline button should be visible")
        declineOfferButton.click()
        return conversationRobot
    }

    @Step("Assert item request accept button is visible and click it")
    fun acceptItemRequestOffer(): ConversationRobot {
        VintedAssert.assertTrue(acceptOfferButton.isVisible(), "Item request accept button should be visible")
        acceptOfferButton.click()
        return conversationRobot
    }

    @Step("Assert Accept and Decline offer buttons are not visible")
    fun assertAcceptAndDeclineButtonsAreNotVisible(): ConversationRobot {
        VintedAssert.assertTrue(acceptOfferButton.isInvisible(1), "Accept offer button should not be visible")
        VintedAssert.assertTrue(declineOfferButton.isInvisible(1), "Decline offer button should not be visible")
        return conversationRobot
    }

    @Step("Make offer through offer your price button")
    fun makeOfferThroughOfferYourPriceButton(price: String): ConversationRobot {
        VintedAssert.assertTrue(offerYourPriceButton.isVisible(), "Offer your price button should be visible")
        offerYourPriceButton.click()
        OfferRobot().makeAnOfferRequest(price)
        return conversationRobot
    }
}
