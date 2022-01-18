package robot.inbox.conversation

import RobotFactory.conversationRobot
import RobotFactory.inAppNotificationRobot
import io.qameta.allure.Step
import robot.ActionBarRobot
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class OrderCancellationRobot : BaseRobot() {
    private val actionBarRobot: ActionBarRobot get() = ActionBarRobot()

    private val cancellationReasonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "reasons_container",
                "cancellation_reason_radio"
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[2]")
        )

    private val cancellationReasonExplanationElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "reason_explanation",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeTextView")
        )

    @Step("Select reason and cancel order")
    fun selectReasonAndCancelOrder(): ConversationRobot {
        cancellationReasonElement.click()

        if (cancellationReasonExplanationElement.withWait(seconds = 2).isVisible()) {
            cancellationReasonExplanationElement.sendKeys("Agreed with seller")
        }
        inAppNotificationRobot.closeInAppNotificationIfExists()
        actionBarRobot.submit()
        return conversationRobot
    }
}
