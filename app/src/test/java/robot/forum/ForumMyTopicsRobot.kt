package robot.forum

import RobotFactory.forumTopicInnerRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ForumMyTopicsRobot : BaseRobot() {

    private fun myTopicTitleElement() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("single_topic_title")
        )

    private val emptyStateElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_empty_state_body")
        )

    @Step("Assert favorite topic is same: {title}")
    fun assertFavoriteTopicIsSame(title: String): ForumMyTopicsRobot {
        VintedAssert.assertEquals(getFirstTopicName(), title, "Favorite topic title should be $title but was ${getFirstTopicName()}")
        return this
    }

    @Step("Get first topic name")
    fun getFirstTopicName(): String {
        return myTopicTitleElement().withWait().text
    }

    @Step("Assert my topic title is {title}")
    fun assertMyTopicTitleIs(title: String): ForumMyTopicsRobot {
        VintedAssert.assertEquals(getFirstTopicName(), title, "My topic title should be $title but was ${getFirstTopicName()}")
        return this
    }

    @Step("Open first topic")
    fun openMyFirstTopic(): ForumTopicInnerRobot {
        myTopicTitleElement().click()
        return forumTopicInnerRobot
    }

    @Step("Assert my topics list is empty")
    fun assertMyForumPostListIsEmpty(): ForumMyTopicsRobot {
        VintedAssert.assertTrue(emptyStateElementAndroid.isVisible(1), "Empty my topics list should be visible")
        return this
    }
}
