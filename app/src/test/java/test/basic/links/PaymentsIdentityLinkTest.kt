package test.basic.links

import RobotFactory.conversationWorkflowRobot
import RobotFactory.paymentsIdentityRobot
import api.controllers.ConversationAPI
import api.controllers.user.conversationApi
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.Links
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.config.VintedCountry.PAYMENTS_NOT_PL
import commonUtil.testng.mobile.RunMobile

@RunMobile
@LoginToDefaultUser
@Feature("Conversation payments identity link test")
class PaymentsIdentityLinkTest : BaseTest() {

    @BeforeMethod(description = "Delete conversations for logged in user")
    fun beforeMethod() {
        loggedInUser.conversationApi.deleteConversations()
    }

    /*
    This test requires activated wallet in the US thus it runs on default user with activated wallet.
     */

    @RunMobile(country = PAYMENTS_NOT_PL, message = "Test only for payment countries", neverRunOnSandbox = true)
    @Test(description = "Payments identity external link should open payments identity screen in the app")
    fun testPaymentsIdentityExternalLink() {
        val paymentsIdentityURL = Links.getPaymentsIdentityExternalLink()
        val conversationId = ConversationAPI.createConversation(
            sender = otherUser,
            recipient = defaultUser,
            message = paymentsIdentityURL
        )

        conversationWorkflowRobot.goToConversationAndClickOnMessage(conversationId, paymentsIdentityURL)
        paymentsIdentityRobot
            .clickKycEducationConfirmButton()
            .assertVerificationFormLayoutIsVisible()
    }
}
