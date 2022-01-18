package robot.inbox.conversation

import RobotFactory.conversationRobot
import RobotFactory.fullImageRobot
import commonUtil.asserts.VintedAssert
import commonUtil.data.Image
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver

class SuspiciousPhotoRobot : BaseRobot() {
    private val suspiciousPhotoTextElement
        get() = VintedDriver.elementByIdAndTranslationKey("view_chat_suspicious_photo_text", "suspicious_photo_label")

    private val unsafeConversationQuestionElement
        get() = VintedDriver.elementByIdAndTranslationKey("report_suggestion", "unsafe_conversation_question_label")

    private val unsafeConversationReportButton
        get() = VintedDriver.elementByIdAndTranslationKey("report_suggestion_action", "unsafe_conversation_report_suggestion_label")

    @Step("Click on a photo to open it in fullscreen, then close it")
    fun clickOnPhotoToEnlargeItThenCloseIt(): SuspiciousPhotoRobot {
        conversationRobot.clickOnPhoto()
        fullImageRobot
            .assertFullScreenImageIsOpen()
            .closeFullScreenImage()
        return this
    }

    @Step("Click on suspicious photo text to reveal the photo")
    fun clickOnSuspiciousPhotoTextToRevealThePhoto(): ConversationRobot {
        suspiciousPhotoTextElement.tap()
        return ConversationRobot()
    }

    @Step("Assert suspicious photo text is visible")
    fun assertSuspiciousPhotoTextIsVisible(): SuspiciousPhotoRobot {
        VintedAssert.assertTrue(
            suspiciousPhotoTextElement.isVisible(),
            "Suspicious photo thumbnail text should be visible"
        )
        return this
    }

    @Step("Assert unsafe conversation report action is visible")
    fun assertUnsafeConversationReportActionIsVisible(): SuspiciousPhotoRobot {
        VintedAssert.assertTrue(
            unsafeConversationQuestionElement.isVisible(),
            "Report action question should be visible"
        )
        VintedAssert.assertTrue(unsafeConversationReportButton.isVisible(), "Report action button should be visible")
        return this
    }

    @Step("Assert revealed photo thumbnail is visible and close conversation")
    fun assertRevealedPhotoThumbnailIsVisibleAndCloseConversation(file: Image) {
        ConversationRobot().assertPhotoThumbnailIsVisible(file)
        clickBack()
    }
}
