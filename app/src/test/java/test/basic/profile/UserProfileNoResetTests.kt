package test.basic.profile

import RobotFactory.deepLink
import RobotFactory.userProfileRobot
import api.controllers.user.feedbackApi
import api.data.models.isNotNull
import api.data.requests.feedback.VintedFeedbackCreateRequest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@RunMobile(neverRunOnSandbox = true)
@Feature("User profile no reset tests")
class UserProfileNoResetTests : BaseTest() {

    @BeforeMethod(description = "Create feedback from otherUser to mainUser with comment")
    fun createComment() {
        val feedbackRequest = VintedFeedbackCreateRequest(
            userId = mainUser.id,
            feedback = "test comment",
            feedbackRate = 4,
            rating = 3
        )
        otherUser.feedbackApi.createFeedback(feedbackRequest)
    }

    @LoginToNewUser
    @Test(description = "Test if user can see in other user profile feedback about that user")
    fun testThatFeedbackCanBeSeenByOtherUsers() {
        deepLink.profile.goToUserProfile(mainUser.id)

        userProfileRobot.closetScreen.shortUserInfo
            .assertOneFeedbackVisible()
            .clickFeedbackSection()
        userProfileRobot.feedbackScreen
            .assertFeedback(replayButtonVisibility = Visibility.Invisible, username = otherUser.username)

        deepLink.profile.goToUserProfile(mainUser.id)
        userProfileRobot
            .clickFeedbackTab()
            .assertFeedback(replayButtonVisibility = Visibility.Invisible, username = otherUser.username)
    }

    @LoginToMainThreadUser
    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Test if user can see in profile feedback")
    @TmsLink("266")
    fun testFeedbackInProfile() {
        deepLink.profile.goToMyProfile()

        userProfileRobot.closetScreen.shortUserInfo
            .assertOneFeedbackVisible()
            .clickFeedbackSection()
        userProfileRobot.feedbackScreen
            .assertFeedback(Visibility.Visible, otherUser.username)

        deepLink.profile.goToMyProfile()
        userProfileRobot
            .clickFeedbackTab()
            .assertFeedback(Visibility.Visible, otherUser.username)
    }

    @AfterMethod(description = "Delete feedback after test")
    fun deleteFeedbacks() {
        val feedbacks = otherUser.isNotNull().feedbackApi.getFeedbackList(mainUser.id)
        feedbacks.forEach {
            otherUser.isNotNull().feedbackApi.deleteFeedback(it.id)
        }
    }
}
