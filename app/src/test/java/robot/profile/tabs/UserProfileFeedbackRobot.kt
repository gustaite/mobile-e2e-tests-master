package robot.profile.tabs

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class UserProfileFeedbackRobot : BaseRobot() {

    private fun usernameElementList(username: String): List<VintedElement> {
        return VintedDriver.findElementList(
            VintedBy.id("view_cell_title"),
            VintedBy.iOSNsPredicateString("name == '$username'")
        )
    }

    private fun feedbackTextElementList(feedback: String): List<VintedElement> {
        return VintedDriver.findElementList(VintedBy.id("feedback_text"), VintedBy.accessibilityId(feedback))
    }

    private val replyButtonList: List<VintedElement>
        get() = VintedDriver.findElementList(VintedBy.id("feedback_action_reply"), VintedBy.accessibilityId("reply"))

    @Step("Assert feedback")
    fun assertFeedback(replayButtonVisibility: Visibility, username: String): UserProfileFeedbackRobot {
        assertFeedbackText()
        assertUsername(username)
        assertReplyButtonIs(replayButtonVisibility)
        return this
    }

    @Step("Assert that reply button is {visibility}")
    private fun assertReplyButtonIs(visibility: Visibility): UserProfileFeedbackRobot {
        if (visibility.value) {
            VintedAssert.assertEquals(replyButtonList.size, 1, "Expected reply one button to be visible")
        } else {
            VintedAssert.assertFalse(VintedElement.isListVisible({ replyButtonList }), "No reply button should be visible")
        }

        return this
    }

    @Step("Assert feedback comment {feedback}")
    fun assertFeedbackText(feedback: String = "test comment"): UserProfileFeedbackRobot {
        VintedAssert.assertEquals(feedbackTextElementList(feedback).size, 1, "Comment count should be 1")
        VintedAssert.assertEquals(feedbackTextElementList(feedback)[0].text, feedback, "Comment text should be '$feedback'")
        return this
    }

    @Step("Assert feedback username is visible and it is '{expectedUsername}'")
    private fun assertUsername(expectedUsername: String): UserProfileFeedbackRobot {
        usernameElementList(expectedUsername).let {
            VintedAssert.assertEquals(it.size, 1, "should be only one comment from $expectedUsername")
            it[0].text.let { username ->
                VintedAssert.assertEquals(
                    username, expectedUsername,
                    "Feedback username should be $expectedUsername, but was: $username"
                )
            }
        }
        return this
    }
}
