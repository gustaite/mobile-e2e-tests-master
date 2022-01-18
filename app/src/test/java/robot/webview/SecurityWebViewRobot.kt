package robot.webview

import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.models.transaction.VintedTransaction
import api.data.models.transaction.VintedTransactionStatus
import commonUtil.Util
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.conversation.ConversationRobot
import util.VintedDriver
import util.absfeatures.AbTestController
import util.base.BaseTest.Companion.loggedInUser
import util.driver.VintedBy
import util.driver.VintedElement

class SecurityWebViewRobot : BaseRobot() {

    private val creditCardSecurityPasswordTabElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"code\")"),
            iOSBy = VintedBy.className("XCUIElementTypeSecureTextField")
        )

    private val submitCreditCardPasswordButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.className("android.widget.Button"),
            iOSBy = VintedBy.accessibilityId("Submit")
        )

    private val yes3dsSimulationButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText("Yes"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Yes\"`]")
        )

    @Step("Simulate successful 3ds response after clicking buy (if needed)")
    fun simulateSuccessful3dsResponseAfterClickingBuy(txId: Long): ConversationRobot {
        return simulateSuccessful3dsResponseAfterClickingBuy(txId = txId, item = null)
    }

    @Step("Simulate successful 3ds response after clicking buy (if needed)")
    fun simulateSuccessful3dsResponseAfterClickingBuy(item: VintedItem): ConversationRobot {
        return simulateSuccessful3dsResponseAfterClickingBuy(txId = null, item = item)
    }

    @Step("Simulate successful 3ds response after adding credit card")
    fun simulateSuccessful3dsResponseAfterAddingCreditCard() {
        val password = loggedInUser.creditCardCredentials.info.securePassword
        if (password != null && AbTestController.isHandle3dsDuringCardAddOn()) {
            simulateSuccessful3dsResponseIfNeeded()
        } else {
            commonUtil.reporting.Report.addMessage("securePassword was not provided (or 3ds FS/AB test is turned off) so 3ds check is not expected")
        }
    }

    @Step("[API] Get security 3d check success property")
    private fun getsecurity3dCheckStatus(txId: Long?, item: VintedItem?): String? {
        VintedAssert.assertTrue(txId != null || item != null, "Both 'txId' and 'item' were not provided")
        var transaction: VintedTransaction? = null
        Util.retryUntil(
            block = {
                transaction = if (txId != null) {
                    loggedInUser.transactionApi.getTransactionById(txId)
                } else {
                    loggedInUser.transactionApi.getTransactionByItemId(item!!)
                }
                transaction?.status?.value ?: 0 >= VintedTransactionStatus.STATUS_DEBIT_PROCESSING.value
            },
            tryForSeconds = 60 // ToDo change this back to 35
        )

        return transaction?.buyerDebit?.security3dCheckStatus
    }

    @Step("Simulate successful 3ds response")
    private fun simulateSuccessful3dsResponseIfNeeded(): ConversationRobot {
        if (yes3dsSimulationButton.withWait().isVisible(40)) {
            yes3dsSimulationButton.click()
        }
        return RobotFactory.conversationRobot
    }

    private fun simulateSuccessful3dsResponseAfterClickingBuy(txId: Long?, item: VintedItem?): ConversationRobot {
        val password = loggedInUser.creditCardCredentials.info.securePassword
        if (password != null) {
            val security3dsCheck = getsecurity3dCheckStatus(txId = txId, item = item)
            if (security3dsCheck != null) {
                simulateSuccessful3dsResponseIfNeeded()
            } else {
                commonUtil.reporting.Report.addMessage("security3dsCheck was null so 3ds check is not expected")
            }
        } else {
            commonUtil.reporting.Report.addMessage("securePassword was not provided so 3ds check is not expected")
        }
        return RobotFactory.conversationRobot
    }
}
