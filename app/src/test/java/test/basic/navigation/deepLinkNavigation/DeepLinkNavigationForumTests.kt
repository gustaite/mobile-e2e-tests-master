package test.basic.navigation.deepLinkNavigation

import RobotFactory.createForumTopicRobot
import RobotFactory.deepLink
import RobotFactory.forumTopicInnerRobot
import RobotFactory.navigationRobot
import api.controllers.user.forumApi
import api.data.models.forum.VintedForumPost
import api.data.models.forum.VintedForumTopic
import commonUtil.asserts.VintedAssert
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.thread
import io.qameta.allure.Feature
import org.apache.commons.text.StringEscapeUtils
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile

@LoginToMainThreadUser
@RunMobile(neverRunOnSandbox = true)
@Feature("DeepLink navigation tests")
class DeepLinkNavigationForumTests : BaseTest() {

    private var forumTopic: VintedForumTopic? by thread.lateinit()
    private var forumPost: VintedForumPost? by thread.lateinit()

    @BeforeMethod(description = "Get Forum topic and post")
    fun beforeMethod_a_getTopicAndPostFromForum() {
        val forumGroup = loggedInUser.forumApi.getForumTree().forumGroups.lastOrNull { it.forums != null && it.forums!!.any { forum -> forum.topicCount > 0 } }
        val forumWithTopics = forumGroup?.forums?.lastOrNull { it.topicCount > 0 }

        VintedAssert.assertNotNull(forumWithTopics, "Forum with topics was null")
        forumTopic = loggedInUser.forumApi.getForumTopics(forumWithTopics!!.id).forumTopics?.lastOrNull { it.postCount > 0 }
        VintedAssert.assertNotNull(forumTopic, "Forum topic was null")
        forumPost = loggedInUser.forumApi.getForumPosts(forumTopic!!.id).forumPosts?.firstOrNull()
        VintedAssert.assertNotNull(forumPost, "Forum post was null")
    }

    @Test(description = "Test if deepLink navigation to 'Forum' is working")
    fun testDeepLinkNavigationToForumScreen() {
        val forumTitle = NavigationDataProviders.forumTitle.trimEnd()
        deepLink.forum.goToForum()
        navigationRobot.assertNavigationBarNameText(forumTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Forum Topic' is working")
    fun testDeepLinkNavigationToForumTopic() {
        deepLink.forum.goToForumTopic(forumTopic!!.id)
        forumTopicInnerRobot.assertTopicTitleIs(forumTopic!!.title!!.trim())
    }

    @Test(description = "Test if deepLink navigation to 'Forum Post' is working")
    @RunMobile(platform = VintedPlatform.ANDROID, message = "Test is only supported for Android")
    fun testDeepLinkNavigationToForumPost() {
        deepLink.forum.goToForumTopicPost(forumTopic!!.id, forumPost!!.id)
        forumTopicInnerRobot.assertTopicTitleIs(forumTopic!!.title!!.trim())
        val postBody = forumPost!!.body!!.let {
            var body = it.split("\r", "\n", "&#").firstOrNull() ?: it.substring(0, 20)
            if (body.length > 20) body = body.substring(0, 20)
            StringEscapeUtils.unescapeHtml4(body)
        }.trim()
        createForumTopicRobot.assertPostWithTextIsVisible(postBody)
    }
}
