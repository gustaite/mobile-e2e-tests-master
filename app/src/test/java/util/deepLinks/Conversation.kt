package util.deepLinks

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import io.qameta.allure.Step
import robot.inbox.conversation.ConversationRobot
import util.IOS

class Conversation {

    @Step("Open 'Notifications' tab (only iOS)")
    fun goToNotificationsScreen() {
        IOS.doIfiOS {
            deepLink.openURL("notifications")
        }
    }

    @Step("Open 'Inbox' tab")
    fun goToInbox() {
        deepLink.openURL("messaging")
    }

    @Step("Open conversation by ID")
    fun goToConversation(conversationId: Long): ConversationRobot {
        deepLink.openURL("messaging?id=$conversationId")
        return conversationRobot
    }

    @Step("Open conversation with transaction progress")
    fun goToConversationTransactionProgress(conversationId: Long) {
        deepLink.openURL("messaging_transaction_progress?id=$conversationId")
    }
}
