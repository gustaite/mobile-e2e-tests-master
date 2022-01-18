package test.basic

import RobotFactory.createForumTopicRobot
import RobotFactory.forumHomeRobot
import RobotFactory.forumMyTopicsRobot
import RobotFactory.forumSavedTopicsRobot
import RobotFactory.forumWorkflowRobot
import RobotFactory.navigationRobot
import api.controllers.user.forumApi
import api.data.models.isNotNull
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedEnvironment.PRODUCTION
import commonUtil.testng.config.VintedPlatform.ANDROID
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import robot.forum.ForumNavigation

@Feature("Forum tests")
@LoginToMainThreadUser
@RunMobile(env = PRODUCTION, neverRunOnSandbox = true)
class ForumTests : BaseTest() {

    @RunMobile(platform = ANDROID, message = "Test is for Android only")
    @Test(description = "Test create/edit new topic in forum and delete")
    fun testCreatingEditingAndDeletingForumTopic() {

        val topicTitle = "Test ${commonUtil.Util.generateMoment()}"
        val editTitleText = "$topicTitle edited"
        val editPostText = "test"

        navigationRobot
            .openForum()
            .openDiscussionAboutVinted()
            .createForumNewTopic(topicTitle)
            .assertTopicTitleIs(topicTitle)
            .assertPostContainPhoto()
            .assertForumPostTextEqualsTo(createForumTopicRobot.post)
            .clickBackToFirstForumScreen()
        forumHomeRobot
            .navigateToSelectedForumHomeCell(ForumNavigation.MY_TOPICS)
            .assertMyTopicTitleIs(topicTitle)
            .openMyFirstTopic()
        forumWorkflowRobot
            .editTopicTitleAndAssertItWasEdited(editTitleText)
            .editForumPostTextAndAssertItWasEdited(editPostText)
            .assertTopicTitleIs(editTitleText)
            .assertPostContainPhoto()
            .deleteTopic()
        forumMyTopicsRobot.assertMyForumPostListIsEmpty()
    }

    @RunMobile(platform = ANDROID, message = "Test for Android only")
    @Test(description = "Test topic can be favored/unfavored and visible in favorite list")
    fun testForumTopicFavoriting() {

        navigationRobot
            .openForum()
            .openDiscussionAboutVinted()

        val topicName = forumMyTopicsRobot.getFirstTopicName()

        forumMyTopicsRobot
            .openMyFirstTopic()
            .clickFavoriteButton()
            .clickBackToFirstForumScreen()
            .navigateToSelectedForumHomeCell(ForumNavigation.SAVED_TOPICS)
            .assertFavoriteTopicIsSame(topicName)
            .openMyFirstTopic()
            .clickFavoriteButton()
            .clickBackToFirstForumScreen()
            .navigateToSelectedForumHomeCell(ForumNavigation.SAVED_TOPICS)
        forumSavedTopicsRobot.assertFavoriteListIsEmpty()
    }

    @AfterMethod(description = "Delete created user forum topic")
    fun cleanupForumTopic() {
        loggedInUser.isNotNull().forumApi.deleteUserForumTopics()
    }
}
