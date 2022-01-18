package test.basic.inbox

import RobotFactory.catalogRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.inAppNotificationRobot
import RobotFactory.inboxRobot
import RobotFactory.modalRobot
import RobotFactory.navigationRobot
import RobotFactory.notificationRobot
import RobotFactory.pushNotificationRobot
import RobotFactory.suspiciousPhotoRobot
import RobotFactory.cameraAndGalleryWorkflowRobot
import RobotFactory.conversationWorkflowRobot
import RobotFactory.userProfileClosetRobot
import api.AssertApi
import api.controllers.ConversationAPI
import api.factories.UserFactory
import api.controllers.item.*
import api.controllers.user.*
import api.data.models.isNotNull
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.*
import util.*
import util.base.BaseTest
import util.image.ImageFactory.Companion.SUSPICIOUS_PHOTO_THUMBNAIL
import commonUtil.testng.config.VintedCountry.WITH_SUSPICIOUS_PHOTO_CLASSIFICATION
import commonUtil.testng.config.VintedPlatform.ANDROID
import commonUtil.testng.mobile.RunMobile
import util.values.*

@RunMobile
@Feature("Conversation tests")
class ConversationTests : BaseTest() {
    private val initialMessage = "Test message"
    private val newMessage = "Hello world"
    private var conversationId by thread(0L)

    @BeforeMethod(description = "Delete logged in users conversations and disable push notification screen")
    fun deleteUserConversationsAndDisablePushNotificationScreen() {
        loggedInUser.conversationApi.deleteConversations()
        deepLink.disablePushNotificationScreen()
    }

    @LoginToMainThreadUser
    @Test(description = "Block member and check if you can no longer write messages. Unblock and check if you can write again")
    @TmsLink("263")
    fun testBlockingAndUnblockingInConversation() {
        val sender = UserFactory.createRandomUser()
        conversationId = ConversationAPI.createConversation(sender = sender, recipient = loggedInUser, message = initialMessage)

        deepLink.conversation
            .goToConversation(conversationId)
            .openConversationDetails()
            .blockUser()
            .assertMessageInputVisibility(Visibility.Invisible)
            .openConversationDetails()
            .unblockUser()
            .assertMessageInputVisibility(Visibility.Visible)
            .sendMessageAndAssertItIsVisible(newMessage)
    }

    @LoginToMainThreadUser
    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Receive in app notification whilst not being in conversation. Check that it leads to conversation")
    fun testInAppNotificationLeadsToConversation() {
        loggedInUser.notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        val expectedInAppNotificationText = "${defaultUser.username.lowercase()}: $initialMessage"

        inAppNotificationRobot
            .assertInAppNotificationText(expectedInAppNotificationText)
            .clickOnInAppNotification()

        conversationRobot.assertMessageVisibility(initialMessage, Visibility.Visible)
    }

    @LoginToMainThreadUser
    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Check if unread message bubble disappears after reading a message")
    fun testUnreadMessageBubbleDisappearsAfterReadingMessage() {
        loggedInUser.notificationSettingsApi
            .enableNotifications(VintedNotificationSettingsTypes.PUSH)
            .conversationApi.deleteConversations()
            .userApi.markAsReadAllNotifications()

        inboxRobot.assertInboxUnreadMessageBubbleVisibility(Visibility.Invisible)
        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        inboxRobot.assertInboxUnreadMessageBubbleVisibility(Visibility.Visible)
        notificationRobot.isMessageAppeared(initialMessage, 30)
        deepLink.conversation.goToConversation(conversationId)
        inboxRobot.clickBack()
        inboxRobot.assertInboxUnreadMessageBubbleVisibility(Visibility.Invisible)
    }

    @LoginToMainThreadUser
    @Test(description = "Send new message via inbox")
    @TmsLink("262")
    fun testSendNewMessageViaInbox() {
        val longMessageText = "Ilgas tekstas, Ilgas tekstas, Ilgas tekstas, Ilgas tekstas, Ilgas tekstas, Ilgas tekstas"

        deepLink.conversation.goToInbox()
        inboxRobot
            .clickSendNewMessageButton()
            .typeAndSelectRecipient(otherUser.username)
            .sendMessage(longMessageText)
            .assertMessageVisibility(longMessageText, Visibility.Visible)
    }

    @LoginToMainThreadUser
    @Test(description = "Add gallery photo in the conversation")
    @TmsLink("25682")
    fun testAddGalleryPhotoInConversation() {
        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        navigationRobot
            .openInbox()
            .clickOnConversationUsingUsername(defaultUser.username)
        conversationRobot
            .clickToAddPhoto()
        cameraAndGalleryWorkflowRobot.selectPhotosFromGallery()
        conversationRobot.assertConversationPhotoIsVisible()
    }

    @LoginToMainThreadUser
    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Receive push notification. Confirm that upon opening it leads to conversation")
    fun testPushNotificationLeadsToConversation() {
        loggedInUser.notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
        Android.clickHome()

        notificationRobot
            .openNotifications()

        ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        val notificationExists = notificationRobot.isMessageAppeared(initialMessage, 30)
        VintedAssert.assertTrue(notificationExists, "Push notification should be visible")

        pushNotificationRobot
            .openPushNotificationByText(initialMessage)
            .assertMessageVisibility(initialMessage, Visibility.Visible)
    }

    @LoginToMainThreadUser
    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Test hashtags in conversation")
    fun testHashtagsInConversation() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
        val incompleteHashtag = "#v"

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        deepLink.conversation
            .goToConversation(conversationId)
            .typeMessageSelectAutocompleteValueAndSend(incompleteHashtag)
            .assertInputIsNotEqualToSentMessage(incompleteHashtag)
            .clickOnMessageByTextBeginning(incompleteHashtag)
        catalogRobot.assertCatalogLayoutIsVisible()
    }

    @LoginToMainThreadUser
    @Test(description = "Test mentions in conversation")
    fun testMentionsInConversation() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
        val incompleteMention = "@auto_test"

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        deepLink.conversation
            .goToConversation(conversationId)
            .typeMessageSelectAutocompleteValueAndSend(incompleteMention)
            .assertInputIsNotEqualToSentMessage(incompleteMention)

        val usernameInMessage = conversationRobot.clickOnMessageByTextBeginning(incompleteMention)
        val normalizedUsername = usernameInMessage.removePrefix("@")
        userProfileClosetRobot.shortUserInfo.assertUsername(normalizedUsername)
    }

    // toDo remove when FS conversation_message_context_menu_remove will be scaled in prod
    @LoginToMainThreadUser
    @Test(description = "Test copy paste in conversation")
    fun testCopyPaste() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
        val message = "internet"
        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = message)

        deepLink.conversation
            .goToConversation(conversationId)
            .copyText(message)
            .pasteText()
            .sendPastedTextAndAssert(message)
    }

    @LoginToMainThreadUser
    @RunMobile(country = WITH_SUSPICIOUS_PHOTO_CLASSIFICATION, message = "Suspicious photo hiding is not enabled in INT")
    @Test(description = "Test suspicious photo in the conversation")
    @TmsLink("24439")
    fun testSuspiciousPhotoInConversation() {
        val photoUuid = Media.uploadPhoto(user = defaultUser, imageFile = SUSPICIOUS_PHOTO_THUMBNAIL, content = VintedTempMediaType.USER_MSG).tempUuid!!
        conversationId = ConversationAPI.createConversation(
            sender = defaultUser, recipient = loggedInUser, message = initialMessage, photos = listOf(photoUuid)
        )

        AssertApi.assertApiResponseWithWait(
            actual = {
                loggedInUser.conversationApi
                    .getConversation(conversationId)
                    .let {
                        commonUtil.reporting.Report.addMessage("Conversation: $it")
                        it.suspicious && it.messages?.last()?.entityType == "report_suggestion"
                    }
            },
            expected = true,
            errorMessage = "Conversation should be suspicious and last message should be report suggestion"
        )

        deepLink.conversation.goToConversation(conversationId)
        suspiciousPhotoRobot
            .assertSuspiciousPhotoTextIsVisible()
            .assertUnsafeConversationReportActionIsVisible()
            .clickOnSuspiciousPhotoTextToRevealThePhoto()
            .assertPhotoThumbnailIsVisible(SUSPICIOUS_PHOTO_THUMBNAIL)
        suspiciousPhotoRobot
            .clickOnPhotoToEnlargeItThenCloseIt()
            .assertRevealedPhotoThumbnailIsVisibleAndCloseConversation(SUSPICIOUS_PHOTO_THUMBNAIL)
        deepLink.conversation
            .goToConversation(conversationId)
            .assertPhotoThumbnailIsVisible(SUSPICIOUS_PHOTO_THUMBNAIL)
    }

    @LoginToMainThreadUser
    @Test(description = "Test that conversation works when user is deleted")
    @TmsLink("23799")
    fun testConversationWithDeletedOppositeUser() {
        val message = "Message from deleted user"
        val oppositeUser = UserFactory.createRandomUser()
        ConversationAPI.createConversation(sender = oppositeUser, recipient = loggedInUser, message = message)
        oppositeUser.userApi.deleteAccount()

        navigationRobot
            .openInbox()
            .assertDeletedUserUsernamePlaceholderIsVisibleInInbox(ElementByLanguage.DeletedUserUsernamePlaceholder)
            .clickOnConversationUsingUsername(ElementByLanguage.DeletedUserUsernamePlaceholder)
        navigationRobot
            .assertNavigationBarNameText(ElementByLanguage.DeletedUserUsernamePlaceholder)
        conversationRobot
            .assertMessageVisibility(message, Visibility.Visible)
    }

    @LoginToMainThreadUser
    @Test(description = "Delete conversation and check that it is gone in the inbox")
    fun testConversationIsNotVisibleInInboxAfterDeletingIt() {
        conversationId = ConversationAPI.createConversation(sender = otherUser, recipient = loggedInUser, message = initialMessage)

        conversationWorkflowRobot.goToInboxAndAssertMessageTextIsVisibleInPreview(initialMessage)
        inboxRobot.clickOnConversationUsingUsername(otherUser.username)

        conversationRobot
            .openConversationDetails()
            .deleteConversation()
        inboxRobot
            .assertInboxIsVisible()
            .assertEmptyStateMessageTextIsVisible()
    }

    @LoginToMainThreadUser
    @TmsLink("23364")
    @Test(description = "Click on external link and check that leaving Vinted modal appears")
    fun testLeavingVintedExternalLinkModal() {
        loggedInUser.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
        val externalLink = "https://asos.com"

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = initialMessage)

        deepLink.conversation
            .goToConversation(conversationId)
            .sendMessageAndAssertItIsVisible(externalLink)
        conversationRobot
            .assertMessageVisibility(externalLink, Visibility.Visible)
            .clickOnMessageByTextBeginning(externalLink)
        modalRobot
            .assertLeavingVintedModalIsVisible()
            .checkIfContinueButtonIsVisibleAndClickable()
            .clickCancelToCloseModal()
        conversationRobot
            .assertMessageInputVisibility(Visibility.Visible)
    }

    @AfterMethod(description = "Enable push notification settings for logged in user")
    fun afterMethod() {
        loggedInUser.isNotNull().notificationSettingsApi.enableNotifications(VintedNotificationSettingsTypes.PUSH)
    }
}
