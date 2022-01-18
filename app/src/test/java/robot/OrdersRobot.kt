package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.inbox.conversation.ConversationRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class OrdersRobot : BaseRobot() {

    private val transactionCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("transaction_list_cell"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name CONTAINS \"transaction_message_\"`]")
        )

    @Step("Check if transaction cell is visible")
    fun checkIfTransactionCellIsVisible(): OrdersRobot {
        VintedAssert.assertTrue(transactionCellElement.withWait().isVisible(), "Transaction cell should be visible")
        return RobotFactory.ordersRobot
    }

    @Step("Open transaction")
    fun openTransaction(): ConversationRobot {
        transactionCellElement.click()
        return RobotFactory.conversationRobot
    }
}
