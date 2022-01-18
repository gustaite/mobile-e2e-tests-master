package robot.inbox

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.conversation.ConversationRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class NewMessageRobot : BaseRobot() {

    private val recipientInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.className("android.widget.EditText"),
            iOSBy = VintedBy.accessibilityId("search_bar")
        )

    private val autoCompleteValue: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.CELL_TITLE_LINE_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeStaticText")
        )

    private val messageInputElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.className("XCUIElementTypeTextView")
        )

    private val sendButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("message_input_submit"),
            iOSBy = VintedBy.accessibilityId("send_button")
        )

    private fun prefilledRecipientElement(recipientUsername: String): VintedElement = VintedDriver.findElement(
        VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_cell_title\").textMatches(\"$recipientUsername\")"),
        VintedBy.iOSClassChain("**/XCUIElementTypeTextField[`value == \"$recipientUsername\"`]")
    )

    @Step("Type and select recipient {recipientUsername}")
    fun typeAndSelectRecipient(recipientUsername: String): NewMessageRobot {
        recipientInputElement.click()
        recipientInputElement.sendKeys(recipientUsername)

        autoCompleteValue.isVisible(10)
        val recipientElement = findRecipientElementByText(recipientUsername)
        recipientElement.click()

        return this
    }

    @Step("Assert {recipientUsername} is prefilled when writing new message from {recipientUsername} profile")
    fun assertRecipientIsPrefilledComingFromProfile(recipientUsername: String): NewMessageRobot {
        VintedAssert.assertTrue(
            prefilledRecipientElement(recipientUsername).isVisible(),
            "Recipient username should be prefilled"
        )
        return this
    }

    @Step("Send message: {messageText}")
    fun sendMessage(messageText: String): ConversationRobot {
        messageInputElement.click()
        messageInputElement.sendKeys(messageText)
        sendButton.click()

        return ConversationRobot()
    }

    private fun findRecipientElementByText(recipientUsername: String): VintedElement {
        return VintedDriver.findElement(
            VintedBy.androidUIAutomator("UiSelector().resourceId(\"${Android.ID}view_cell_title\").textMatches(\"$recipientUsername\")"),
            VintedBy.accessibilityId(recipientUsername)
        )
    }
}
