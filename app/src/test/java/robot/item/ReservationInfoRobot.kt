package robot.item

import api.data.models.VintedUser
import api.data.models.VintedItem
import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.PortalFactory.isPaymentCountry
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class ReservationInfoRobot : BaseRobot() {

    private val unreserveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("message_header_action_secondary"),
            iOSBy = VintedBy.accessibilityId("message_action_unreserve")
        )

    private val sellButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("message_header_action_primary"),
            iOSBy = VintedBy.accessibilityId("message_action_transfer")
        )

    private val makeOfferButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("message_header_action_secondary"),
            iOSBy = VintedBy.accessibilityId("message_action_offer")
        )

    @Step("Assert item is reserved for {user.username}")
    fun assertUsernameIsVisible(user: VintedUser): ReservationInfoRobot {
        VintedAssert.assertTrue(
            VintedDriver.findElementByText(user.username, searchType = Util.SearchTextOperator.CONTAINS).isVisible(),
            "Username ${user.username} should be visible in reservation info"
        )
        return this
    }

    @Step("Assert reserved item is {item.title}")
    fun assertCorrectItemWasReserved(item: VintedItem): ReservationInfoRobot {
        VintedAssert.assertTrue(
            VintedDriver.findElementByText(item.title).isVisible(),
            "Item title should be ${item.title}"
        )
        return this
    }

    @Step("Assert user can unreserve or sell item. For payments countries only make an offer button is visible")
    fun assertSellAndUnreserveButtonsAreVisible(): ReservationInfoRobot {
        if (isPaymentCountry) {
            VintedAssert.assertTrue(makeOfferButton.isVisible(), "Make an offer button should be visible")
        } else {
            VintedAssert.assertTrue(unreserveButton.isVisible(), "Unreserve button should be visible")
            VintedAssert.assertTrue(sellButton.isVisible(), "Sell button should be visible")
        }
        return this
    }
}
