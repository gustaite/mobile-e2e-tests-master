package robot.inbox.conversation

import RobotFactory.conversationRobot
import RobotFactory.inAppNotificationRobot
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class DeliveryInstructionsRobot : BaseRobot() {
    private val shippingInstructionsTrackingNumber: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "shipping_instructions_tracking_number",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[\$name == '${IOS.getElementValue("shipping_instructions2_tracking_number_title")}'$]/XCUIElementTypeTextField")
        )

    private val confirmShippingInstructionsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("shipping_instructions_action"),
            iOSBy = VintedBy.accessibilityId("mark_as_shipped")
        )

    @Step("Enter tracking number and confirm")
    fun enterTrackingNumberAndConfirm(trackingNumber: String): ConversationRobot {
        Android.doIfAndroid {
            commonUtil.Util.retryUntil(
                block = {
                    inAppNotificationRobot.closeInAppNotificationIfExists()
                    shippingInstructionsTrackingNumber.isVisible(2)
                },
                tryForSeconds = 10
            )
        }
        shippingInstructionsTrackingNumber.withScrollIos().sendKeys(trackingNumber)
        confirmShippingInstructionsButton.click()
        return conversationRobot
    }
}
