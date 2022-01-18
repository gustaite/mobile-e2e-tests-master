package robot.inbox.conversation

import RobotFactory.conversationRobot
import commonUtil.extensions.adaptPrice
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class OfferRobot : BaseRobot() {
    private val itemPriceInputElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id(Android.INPUT_FIELD_ID),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeTextField")
        )

    private val itemPriceSubmitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdMatches(".*_submit"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'buyer_offer_view_action_button_title' || name == 'transaction_make_offer_send'")
        )

    @Step("Make an offer with price {price}")
    fun makeAnOfferRequest(price: String): ConversationRobot {
        val priceAdapted = price.adaptPrice()
        itemPriceInputElement.sendKeys(priceAdapted)
        itemPriceSubmitButton.click()
        return conversationRobot
    }
}
