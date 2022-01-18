package robot.inbox.conversation

import RobotFactory.conversationRobot
import RobotFactory.rateAppRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ShippingStatusTexts

class ShipmentTrackingRobot : BaseRobot() {

    private fun shipmentTrackingNumberElement(trackingNumber: String) =
        VintedDriver.findElement(
            androidBy = VintedBy.id("bubble_tracking_code_title_text"),
            iOSBy = VintedBy.accessibilityId(trackingNumber)
        )

    private val confirmParcelReceivedButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("confirm_parcel_received_button"),
            iOSBy = VintedBy.accessibilityId("confirm_parcel_received_title")
        )

    private val modalConfirmButton: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "modal_primary_button",
            "transaction_action_modal_mark_as_delivered_confirm"
        )

    private val closeButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("close"))

    private fun shipmentTrackingItemStatus(status: String) =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText("item_shipment_tracking_title", status),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name CONTAINS '$status'")
        )

    @Step("Android only: Assert tracking number {trackingNumber} is visible")
    fun assertTrackingNumber(trackingNumber: String): ShipmentTrackingRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(
                shipmentTrackingNumberElement(trackingNumber).isVisible(10),
                "Shipment tracking number should be visible"
            )
        }
        return this
    }

    @Step("Assert 'mark shipment as received' button is not visible")
    fun assertMarkShipmentAsReceivedButtonIsNotVisible(): ShipmentTrackingRobot {
        VintedAssert.assertTrue(confirmParcelReceivedButton.isInvisible(1), "Mark shipment as received button should not be visible")
        return this
    }

    @Step("Mark shipment as received")
    fun markShipmentAsReceived(): ConversationRobot {
        confirmParcelReceivedButton.click()
        modalConfirmButton.click()
        rateAppRobot.clickRateAppLater()
        return conversationRobot
    }

    @Step("Leave shipping tracking page")
    fun leaveShippingTracking() {
        Android.doIfAndroid { clickBack() }
        IOS.doIfiOS { closeButtonIos }
    }

    @Step("Assert tracking status visible {tracking.status}")
    fun assertTrackingStatusVisible(tracking: ShippingStatusTexts): ShipmentTrackingRobot {
        shipmentTrackingItemStatus(tracking.status).let { element ->
            VintedAssert.assertTrue(element.withWait().isVisible(), "Item shipment status should be visible. Expected status: $tracking")
            VintedAssert.assertTrue(element.text.contains(tracking.status), "Item shipment status should match")
        }
        return this
    }
}
