package test.basic.profile

import RobotFactory.catalogRobot
import RobotFactory.deepLink
import RobotFactory.userProfileRobot
import api.controllers.user.conversationApi
import api.controllers.user.userApi
import io.qameta.allure.Feature
import org.testng.annotations.Test
import robot.profile.FollowAction
import util.base.BaseTest
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.LoginToNewUser
import api.factories.UserFactory
import commonUtil.asserts.VintedAssert
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.mobile.RunMobile
import util.testng.*
import util.values.Visibility

@RunMobile
@Feature("User profile reset tests")
class UserProfileResetTests : BaseTest() {
    @LoginToNewUser
    @CreateOneTestUser
    @Test(description = "Test follow button in profile about tab")
    fun testFollowButtonInProfileAboutTab() {
        val followersCountBeforeFollow = "0"
        val followersCountAfterFollow = "1"

        deepLink.profile.goToUserProfile(oneTestUser.id)
        userProfileRobot
            .openAboutTab()
            .assertFollowersCountBeforeFollow(followersCountBeforeFollow)
            .clickFollowButton()
            .assertFollowersCountAfterFollow(followersCountAfterFollow)
    }

    @LoginToDefaultUser
    @Test(description = "Test if message button works in profile closet tab.")
    fun testMessageButtonButtonWorksInClosetTab() {
        val defaultText = "Hello from ${UserFactory.defaultUser().username}"

        deepLink.profile.goToUserProfile(otherUser.id)
        userProfileRobot.closetScreen
            .clickMessageButton()
            .assertRecipientIsPrefilledComingFromProfile(otherUser.username)
            .sendMessage(defaultText)
            .assertMessageVisibility(defaultText, Visibility.Visible)

        val conversationList = otherUser.conversationApi.getConversations()

        val filteredMessageList = conversationList.filter { it.subtitle == defaultText }
        VintedAssert.assertTrue(
            filteredMessageList.isNotEmpty(),
            "User should get $defaultText. Message list is: $conversationList"
        )
        VintedAssert.assertEquals(
            filteredMessageList.size, 1,
            "User should see only one message with text $defaultText. List is: $conversationList"
        )
    }

    @LoginToNewUser
    @CreateOneTestUser
    @Test(description = "Test if follow button works in profile closet tab")
    fun testFollowButtonButtonWorksInClosetTab() {
        deepLink.profile.goToUserProfile(oneTestUser.id)
        userProfileRobot.closetScreen
            .clickOnFollowUnfollowButtonAndAssertChangesInFollowersSection(FollowAction.Follow, 1)
            .clickOnFollowUnfollowButtonAndAssertChangesInFollowersSection(FollowAction.Unfollow, 0)
    }

    @LoginToNewUser
    @CreateOneTestUser
    @RunMobile(neverRunOnSandbox = true)
    @Test(description = "Hashtag in profile description leads to catalog")
    fun testHashtagInProfileDescriptionLeadsToCatalog() {
        val textAboutWithHashtag = "#Converse #Converse #Converse #Converse #Converse #Converse #Converse"
        oneTestUser.userApi.updateInfo(textAboutWithHashtag)

        deepLink.profile.goToUserProfile(oneTestUser.id)
        userProfileRobot.openAboutTab()
        userProfileRobot.aboutScreen.clickOnDescription()
        catalogRobot.assertSuggestedBrandNameIsVisibleInCatalog("converse")
    }
}
