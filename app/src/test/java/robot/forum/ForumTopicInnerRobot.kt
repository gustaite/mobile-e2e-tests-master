package robot.forum

import RobotFactory.createForumTopicRobot
import RobotFactory.forumHomeRobot
import RobotFactory.forumMyTopicsRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor

class ForumTopicInnerRobot : BaseRobot() {

    private val topicTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_label"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeNavigationBar/XCUIElementTypeStaticText")
        )

    private val photoInPostElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("post_image_grid")
        )

    private val postTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("post_body_text")
        )

    private val backFromPostsButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("forum_actionbar_back_button")
        )

    private val moreOptionsForumPostElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.accessibilityId("More options"),
            iOSBy = VintedBy.accessibilityId("more")
        )

    private val editForumTitleElement: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("content"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("post_list_edit_topic_action"))
        )

    private val forumPostMoreActionsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("post_more_actions_button"),
            iOSBy = VintedBy.accessibilityId("action_button")
        )

    private val editForumPostElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("post_list_edit_action"))
        )

    private val favoriteButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("save_topic_button"),
            iOSBy = VintedBy.accessibilityId("save_button")
        )

    @Step("Assert topic title is '{expectedTitle}'")
    fun assertTopicTitleIs(expectedTitle: String): ForumTopicInnerRobot {
        val text = topicTitleElement.withWait(WaitFor.Visible).text.trim()
        VintedAssert.assertEquals(text, expectedTitle, "Topic title should be $expectedTitle but was $text")
        return this
    }

    @Step("Assert post contains photo")
    fun assertPostContainPhoto(): ForumTopicInnerRobot {
        VintedAssert.assertTrue(photoInPostElement.isVisible(1), "Photo in post should be visible")
        return this
    }

    @Step("Post text is equal to {expectedText}")
    fun assertForumPostTextEqualsTo(expectedText: String): ForumTopicInnerRobot {
        val text = postTextElement.withWait(WaitFor.Visible).text.trim()
        VintedAssert.assertEquals(text, expectedText, "Post text should be $expectedText but was $text")
        return this
    }

    @Step("Navigate from forum post to first Forum tab")
    fun clickBackToFirstForumScreen(): ForumHomeRobot {
        clickBack()
        backFromPostsButtonElement.click()
        return forumHomeRobot
    }

    @Step("Click edit forum topic title")
    fun clickEditTopicTitle(): CreateForumTopicRobot {
        moreOptionsForumPostElement.click()
        editForumTitleElement[1].click()
        return createForumTopicRobot
    }

    @Step("Click edit forum post input text")
    fun clickEditForumPostInputText(): CreateForumTopicRobot {
        forumPostMoreActionsElement.click()
        editForumPostElement.click()
        return createForumTopicRobot
    }

    @Step("Delete topic")
    fun deleteTopic(): ForumMyTopicsRobot {
        moreOptionsForumPostElement.click()
        editForumTitleElement[2].click()
        closeModal()
        return forumMyTopicsRobot
    }

    @Step("Click favorite button")
    fun clickFavoriteButton(): ForumTopicInnerRobot {
        favoriteButtonElement.click()
        return this
    }
}
