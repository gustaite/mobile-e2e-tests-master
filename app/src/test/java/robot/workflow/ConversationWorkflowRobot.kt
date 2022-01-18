package robot.workflow

import RobotFactory.contextMenuRobot
import RobotFactory.conversationRobot
import RobotFactory.conversationWorkflowRobot
import RobotFactory.deepLink
import RobotFactory.inboxRobot
import api.data.models.transaction.VintedTransaction
import io.qameta.allure.Step
import robot.BaseRobot
import robot.inbox.InboxRobot
import robot.inbox.conversation.ConversationRobot
import util.absfeatures.AbTestController
import util.values.Visibility

class ConversationWorkflowRobot : BaseRobot() {

    @Step("Go to conversation")
    fun goToConversation(transaction: VintedTransaction): ConversationRobot {
        deepLink.conversation
            .goToConversation(transaction.conversationId)
            .confirmEducationActionIos()
        return conversationRobot
    }

    @Step("Open conversation {conversationId} using deep link and click on message {text}")
    fun goToConversationAndClickOnMessage(conversationId: Long, text: String) {
        deepLink.conversation.goToConversation(conversationId)
        conversationRobot.clickOnMessage(text)
    }

    @Step("Go to inbox and assert message text: '{message}' is visible in preview")
    fun goToInboxAndAssertMessageTextIsVisibleInPreview(message: String): InboxRobot {
        deepLink.conversation.goToInbox()
        inboxRobot.assertMessageTextIsVisibleInPreview(message)
        return inboxRobot
    }

    @Step("Assert copy paste via bottom sheet is working")
    fun assertCopyPasteViaBottomSheetIsWorking(message: String): ConversationWorkflowRobot {
        conversationWorkflowRobot
            .copyAndPasteMessageViaBottomSheet(message)
            .sendPastedTextAndAssert(message)
        return this
    }

    @Step("Copy and paste message via bottom sheet")
    fun copyAndPasteMessageViaBottomSheet(message: String): ConversationRobot {
        conversationRobot
            .longPressOnMessage(message)
            .clickCopyMessage()
            .pasteText()
        return conversationRobot
    }

    @Step("Open remove message modal and click REMOVE button")
    fun openRemoveMessageModalAndClickRemove(): ConversationRobot {
        contextMenuRobot
            .clickRemoveMessage()
            .clickRemoveInRemoveMessageModal()
        return conversationRobot
    }

    @Step("Open remove message modal and click NO button")
    fun openRemoveMessageModalAndClickNo(): ConversationRobot {
        contextMenuRobot
            .clickRemoveMessage()
            .clickNoInRemoveMessageModal()
        return conversationRobot
    }

    @Step("Long press on '{message}' message and assert context menu element is {visibility}")
    fun longPressOnMessageAndAssertContextMenuElementVisibility(message: String, visibility: Visibility): ConversationWorkflowRobot {
        conversationRobot
            .longPressOnMessage(message)
            .assertContextMenuElementVisibility(visibility)
        return this
    }

    @Step("Long press on image message and assert context menu element is {visibility}")
    fun longPressOnImageAndAssertContextMenuElementVisibility(visibility: Visibility): ConversationWorkflowRobot {
        conversationRobot
            .longPressOnImage()
            .assertContextMenuElementVisibility(visibility)
        return this
    }

    @Step("Copy and paste message according to 'conversation_message_context_menu' FS value")
    fun copyAndPasteMessageAccordingToConversationMessageContextMenuValue(message: String): ConversationRobot {
        if (!AbTestController.isConversationMessageContextMenuRemoveOn()) {
            conversationRobot
                .copyText(message)
                .pasteText()
        } else {
            conversationWorkflowRobot.copyAndPasteMessageViaBottomSheet(message)
        }
        conversationRobot.clickSend()
        return conversationRobot
    }
}
