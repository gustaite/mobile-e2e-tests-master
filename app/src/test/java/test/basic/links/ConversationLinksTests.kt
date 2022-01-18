package test.basic.links

import RobotFactory.conversationWorkflowRobot
import RobotFactory.itemRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileClosetRobot
import api.controllers.ConversationAPI
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.conversationApi
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.*
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import util.base.BaseTest

@RunMobile
@Feature("Conversation links tests")
@LoginToMainThreadUser
class ConversationLinksTests : BaseTest() {
    private var conversationId by thread(0L)

    @BeforeMethod(description = "Delete conversations for current user")
    fun beforeMethod() {
        loggedInUser.conversationApi.deleteConversations()
    }

    @Test(description = "Item upload form external link should open upload form in the app")
    fun testItemUploadFormExternalLink() {
        val uploadURL = Links.getItemUploadFormExternalLink()

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = uploadURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, uploadURL)
        uploadItemRobot.assertTitleInputFieldIsVisible()
    }

    @Test(description = "Item upload form deep link should open upload form in the app")
    fun testItemUploadFormDeepLink() {
        val uploadURL = Links.getItemUploadDeepLink()

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = uploadURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, uploadURL)
        uploadItemRobot.assertTitleInputFieldIsVisible()
    }

    @Test(description = "Item external link should open item in the app")
    fun testItemExternalLink() {
        val item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        val itemURL = Links.getItemExternalLink(item)

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = itemURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, itemURL)
        itemRobot.assertItemTitle(item.title)
    }

    @Test(description = "Item deep link should open item in the app")
    fun testItemDeepLink() {
        val item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        val itemURL = Links.getItemDeepLink(item)

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = itemURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, itemURL)
        itemRobot.assertItemTitle(item.title)
    }

    @Test(description = "User profile external link should open profile in the app")
    fun testUserProfileExternalLink() {
        val senderProfileURL = Links.getUserProfileExternalLink(defaultUser)

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = senderProfileURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, senderProfileURL)
        userProfileClosetRobot.shortUserInfo.assertUsername(defaultUser.username)
    }

    @Test(description = "User profile deep link should open profile in the app")
    fun testUserProfileDeepLink() {
        val senderProfileURL = Links.getUserProfileDeepLink(defaultUser)

        conversationId = ConversationAPI.createConversation(sender = defaultUser, recipient = loggedInUser, message = senderProfileURL)

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, senderProfileURL)
        userProfileClosetRobot.shortUserInfo.assertUsername(defaultUser.username)
    }
}
