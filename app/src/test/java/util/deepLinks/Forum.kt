package util.deepLinks

import RobotFactory.deepLink
import io.qameta.allure.Step

class Forum {

    @Step("Open 'Forum'")
    fun goToForum() {
        deepLink.openURL("forum")
    }

    @Step("Open 'Forum topic'")
    fun goToForumTopic(forumTopicId: Long) {
        deepLink.openURL("forum/topic?id=$forumTopicId")
    }

    @Step("Open 'Forum topic post'")
    fun goToForumTopicPost(forumTopicId: Long, postId: Long) {
        deepLink.openURL("forum/topic?id=$forumTopicId&post=$postId")
    }
}
