package robot.workflow

import RobotFactory.forumTopicInnerRobot
import RobotFactory.forumInnerRobot
import RobotFactory.forumWorkflowRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.forum.ForumTopicInnerRobot

class ForumWorkflowRobot : BaseRobot() {

    @Step("Create new forum topic")
    fun createForumNewTopic(title: String): ForumTopicInnerRobot {
        forumInnerRobot
            .clickAddNewTopicButton()
            .enterSubjectTitle(title)
            .enterDefaultPostText()
            .addPhotoFromGallery()
            .assertPhotoCounterEqualsTo(1)
            .saveForumTopic()
        return forumTopicInnerRobot
    }

    @Step("Edit post text to {text}")
    fun editForumPostText(text: String): ForumTopicInnerRobot {
        forumTopicInnerRobot
            .clickEditForumPostInputText()
            .editForumPostInput(text)
            .saveForumTopic()
        return forumTopicInnerRobot
    }

    @Step("Edit topic title to {title}")
    fun editTopicTitle(title: String): ForumTopicInnerRobot {
        forumTopicInnerRobot
            .clickEditTopicTitle()
            .editForumTopicTitle(title)
            .saveForumTopic()
        return forumTopicInnerRobot
    }

    @Step("Edit topic title to {editTitleText} and assert it was edited")
    fun editTopicTitleAndAssertItWasEdited(editTitleText: String): ForumWorkflowRobot {
        editTopicTitle(editTitleText).assertTopicTitleIs(editTitleText)
        return forumWorkflowRobot
    }

    @Step("Edit forum post text to {editPostText} and assert it was edited")
    fun editForumPostTextAndAssertItWasEdited(editPostText: String): ForumTopicInnerRobot {
        editForumPostText(editPostText).assertForumPostTextEqualsTo(editPostText)
        return forumTopicInnerRobot
    }
}
