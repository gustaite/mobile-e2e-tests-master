package robot.workflow

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import api.controllers.user.helpCenterApi
import api.controllers.user.userApi
import api.data.models.faq.VintedFaqEntry
import api.data.models.transaction.VintedTransaction
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.HelpSectionTexts
import robot.webview.WebViewRobot
import util.base.BaseTest.Companion.defaultUser
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.image.ImageFactory
import util.values.Visibility

class ProblemWorkflowRobot {

    fun haveProblemSuspendTransactionBuyerSideViews(transaction: VintedTransaction) {
        conversationRobot.goToInboxAndCloseNoteIfVisible()
        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertHaveProblemAndEverythingIsOkButtonsVisibility(Visibility.Visible)
            .clickHaveProblemButton()
            .assertTransactionItemTitleVisible()
            .clickHelpSection(HelpSectionTexts.RECEIVED_NOT_EXPECTED)
            .clickHelpSection(HelpSectionTexts.IT_IS_DAMAGED)
        WebViewRobot()
            .assertWebViewIsVisible()
            .clickProvideProofButton()
            .assertContactUsPageTitle()
            .provideProof("Got damaged item")
            .assertTransactionSuspendedElementVisible()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
    }

    fun haveProblemSuspendTransactionTryEscalatingToSupportBuyerSideViews(transaction: VintedTransaction) {
        loggedInUser.helpCenterApi.createHelpCenterTicketWithProof(
            faqEntry = HelpSectionTexts.IT_IS_DAMAGED.faqEntry,
            transactionId = transaction.id,
            imageFile = ImageFactory.ITEM_1_PHOTO
        )

        conversationRobot.goToInboxAndCloseNoteIfVisible()
        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertTransactionSuspendedElementVisible()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
            .clickViewIssueDetails()
            .assertProofImageResolveButtonAndMoreInfoLinkIsVisible()
            .submitToVinted()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
            .assertTransactionSubmittedToSupportElementVisible()
    }

    fun haveProblemSuspendTransactionSellerSideViews(transaction: VintedTransaction) {
        defaultUser.helpCenterApi.createHelpCenterTicketWithProof(
            faqEntry = HelpSectionTexts.IT_IS_DAMAGED.faqEntry,
            transactionId = transaction.id,
            imageFile = ImageFactory.ITEM_1_PHOTO
        )

        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertTransactionSuspendedElementVisible()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
            .clickResolveIssueOrCancelAndKeepButton()
            .assertTransactionCancelledElementVisibility(Visibility.Visible)
    }

    fun haveProblemSuspendTransactionTryEscalatingToSupportSellerSideViews(transaction: VintedTransaction) {
        defaultUser.helpCenterApi.createHelpCenterTicketWithProof(
            faqEntry = HelpSectionTexts.IT_IS_DAMAGED.faqEntry,
            transactionId = transaction.id,
            imageFile = ImageFactory.ITEM_1_PHOTO
        )

        deepLink.conversation.goToConversation(transaction.conversationId)
            .assertTransactionSuspendedElementVisible()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
            .clickViewIssueDetails()
            .assertProofImageResolveButtonAndMoreInfoLinkIsVisible()
            .submitToVinted()
            .assertIssueDetailsAndResolveIssueOrCancelAndKeepButtonsVisible()
            .assertTransactionSubmittedToSupportElementVisible()
    }

    @Step("Get random FAQ app link")
    fun getRandomFaqAppLink(): String {
        val faqEntries = loggedInUser.helpCenterApi.getFaqEntries().faqEntries
        val titleUrlMaps = VintedFaqEntry.Companion.getFaqTitleUrlMap(faqEntries = faqEntries)
        val randomValue = titleUrlMaps.values.random().toString()
        val appLink = loggedInUser.userApi.getAppLinks(randomValue)

        val links = appLink.values.firstOrNull()
        val iOS = links?.ios?.firstOrNull()
        val android = links?.android?.firstOrNull()
        val url = if (isAndroid) android?.url else iOS?.url
        VintedAssert.assertFalse(url.isNullOrEmpty(), "Url was null or empty")
        return url!!
    }
}
