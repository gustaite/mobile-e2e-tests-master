package test.basic.inbox

import RobotFactory.contextMenuRobot
import RobotFactory.conversationRobot
import RobotFactory.conversationWorkflowRobot
import RobotFactory.deepLink
import api.controllers.ConversationAPI
import api.controllers.user.conversationApi
import api.controllers.user.notificationSettingsApi
import api.data.models.conversation.getMessages
import api.data.models.isNotNull
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.thread
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.VintedDriver
import util.base.BaseTest
import util.image.ImageFactory
import util.values.ConversationElementTexts.removedMessageByUserText
import util.values.ConversationElementTexts.removedMessagePreviewText
import util.values.Visibility

// ToDo change environment to production when FS 'conversation_message_context_menu_remove' will be turned ON
@RunMobile(env = VintedEnvironment.ALL)
@Feature("Message actions tests")
@LoginToMainThreadUser
class MessageActionsTests : BaseTest() {
    private val initialMessage = "First message"
    private val newMessage = "Second message"
    private var conversationId by thread(0L)

    @BeforeMethod(description = "Delete logged in users conversations and disable push notification screen")
    fun beforeMethod_a_deleteUserConversationsAndDisablePushNotificationScreen() {
        loggedInUser.conversationApi.deleteConversations()
        deepLink.disablePushNotificationScreen()
    }

    @Test(description = "Test remove message in conversation from sender side")
    @TmsLink("25825")
    fun testRemoveMessageInConversationFromSenderSide() {
        conversationId = ConversationAPI.createConversation(sender = loggedInUser, recipient = otherUser, message = initialMessage)
        loggedInUser.conversationApi.replyToConversation(conversationId, newMessage)

        deepLink.conversation
            .goToConversation(conversationId)
        conversationWorkflowRobot
            .longPressOnMessageAndAssertContextMenuElementVisibility(initialMessage, Visibility.Visible)
            .openRemoveMessageModalAndClickNo()
            .assertMessageVisibility(initialMessage, Visibility.Visible)
            .longPressOnMessage(newMessage)
        conversationWorkflowRobot
            .openRemoveMessageModalAndClickRemove()
            .assertMessageVisibility(newMessage, Visibility.Invisible)
            .assertMessageVisibility(removedMessageByUserText, Visibility.Visible)
        conversationWorkflowRobot
            .longPressOnMessageAndAssertContextMenuElementVisibility(removedMessageByUserText, Visibility.Invisible)
            .goToInboxAndAssertMessageTextIsVisibleInPreview(removedMessagePreviewText)
    }

    @Test(description = "Test removed message in conversation from receiver side")
    @TmsLink("25825")
    fun testRemovedMessageInConversationFromReceiverSide() {
        conversationId = ConversationAPI.createConversation(sender = otherUser, recipient = loggedInUser, message = initialMessage)
        otherUser.conversationApi.replyToConversation(conversationId, newMessage)

        val lastMessageReplyId = loggedInUser.conversationApi.getConversation(conversationId).messages.getMessages()
            .firstOrNull { it.entity.body == newMessage }?.entity?.id ?: throw NullPointerException("Conversation message '$newMessage' was not found")

        sleepWithinStep(1000)
        otherUser.conversationApi.deleteMessageInConversation(conversationId, lastMessageReplyId)

        deepLink.conversation
            .goToConversation(conversationId)
            .assertMessageVisibility(removedMessageByUserText, Visibility.Visible)
        conversationWorkflowRobot.longPressOnMessageAndAssertContextMenuElementVisibility(removedMessageByUserText, Visibility.Invisible)
        conversationRobot.longPressOnMessage(initialMessage)
            .assertRemoveOptionInContextMenuIsNotVisible()
            .assertCopyOptionInContextMenuVisibility(Visibility.Visible)
        VintedDriver.scrollDown()
        conversationWorkflowRobot.goToInboxAndAssertMessageTextIsVisibleInPreview(removedMessagePreviewText)
    }

    @Test(description = "Test remove image in conversation from sender side")
    @TmsLink("25826")
    fun testRemoveImageInConversationFromSenderSide() {
        conversationId = loggedInUser.conversationApi.createConversationWithImages(
            sender = loggedInUser, recipient = otherUser, message = initialMessage, listOf(ImageFactory.ITEM_1_PHOTO, ImageFactory.CAT)
        )

        deepLink.conversation
            .goToConversation(conversationId)
            .assertImagesInConversationCount(2)
        conversationWorkflowRobot.longPressOnImageAndAssertContextMenuElementVisibility(Visibility.Visible)
        contextMenuRobot.assertCopyOptionInContextMenuVisibility(Visibility.Invisible)
        conversationWorkflowRobot
            .openRemoveMessageModalAndClickNo()
            .assertImagesInConversationCount(2)
            .longPressOnImage()
        conversationWorkflowRobot
            .openRemoveMessageModalAndClickRemove()
            .assertImagesInConversationCount(1)
            .assertMessageVisibility(removedMessageByUserText, Visibility.Visible)
        conversationWorkflowRobot.longPressOnMessageAndAssertContextMenuElementVisibility(removedMessageByUserText, Visibility.Invisible)
    }

    @Test(description = "Test removed image in conversation from receiver side")
    @TmsLink("25826")
    fun testRemovedImageInConversationFromReceiverSide() {
        conversationId = loggedInUser.conversationApi.createConversationWithImages(
            sender = otherUser, recipient = loggedInUser, message = initialMessage, listOf(ImageFactory.ITEM_1_PHOTO, ImageFactory.CAT)
        )

        val lastMessageReply = loggedInUser.conversationApi.getConversation(conversationId).messages.getMessages().last().entity
        val lastMessageReplyPhotoId = lastMessageReply.photos.last().id

        otherUser.conversationApi.deletePhotoInConversation(conversationId, lastMessageReply.id, lastMessageReplyPhotoId)
        deepLink.conversation.goToConversation(conversationId)
            .assertMessageVisibility(removedMessageByUserText, Visibility.Visible)
            .assertImagesInConversationCount(1)
        conversationWorkflowRobot
            .longPressOnMessageAndAssertContextMenuElementVisibility(removedMessageByUserText, Visibility.Invisible)
            .longPressOnImageAndAssertContextMenuElementVisibility(Visibility.Invisible)
    }

    @Test(description = "Test copy paste in conversation via bottom sheet")
    fun testCopyPasteViaBottomSheet() {
        val conversationId = ConversationAPI.createConversation(sender = loggedInUser, recipient = otherUser, message = initialMessage)
        otherUser.conversationApi.replyToConversation(conversationId, newMessage)

        deepLink.conversation.goToConversation(conversationId)
        conversationWorkflowRobot.assertCopyPasteViaBottomSheetIsWorking(initialMessage)
        conversationWorkflowRobot.assertCopyPasteViaBottomSheetIsWorking(newMessage)
    }

    @AfterMethod(description = "Enable push notification settings for logged in user")
    fun afterMethod_a_enablePushNotificationSettings() {
        loggedInUser.isNotNull().notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
    }
}
