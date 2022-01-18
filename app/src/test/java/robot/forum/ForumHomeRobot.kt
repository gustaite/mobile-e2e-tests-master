package robot.forum

import RobotFactory.forumMyTopicsRobot
import RobotFactory.forumWorkflowRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.workflow.ForumWorkflowRobot
import util.VintedDriver
import util.IOS
import util.driver.*
import util.values.ElementByLanguage.Companion.forumTopicDiscussionsAboutVintedText

class ForumHomeRobot : BaseRobot() {

    private val allDiscussionsCellElement: VintedElement
        get() = VintedDriver.findElementByText(forumTopicDiscussionsAboutVintedText)

    private val forumNewsCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("forum_news_cell"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("forum_home_news"))
        )

    private val myTopicsCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("my_topics_cell"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("forum_home_my_topics"))
        )

    private val savedTopicsCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("saved_topics_cell"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("forum_home_saved_topics"))
        )

    @Step("Open discussion all discussions Vinted cell")
    fun openDiscussionAboutVinted(): ForumWorkflowRobot {
        navigateToSelectedForumHomeCell(ForumNavigation.ALL_DISCUSSIONS)
        return forumWorkflowRobot
    }

    @Step("Navigate to {navigationTitle}")
    fun navigateToSelectedForumHomeCell(navigationTitle: ForumNavigation): ForumMyTopicsRobot {
        val forumNavigationTitleElement =
            when (navigationTitle) {
                ForumNavigation.MY_TOPICS -> myTopicsCellElement
                ForumNavigation.ALL_DISCUSSIONS -> allDiscussionsCellElement
                ForumNavigation.SAVED_TOPICS -> savedTopicsCellElement
                ForumNavigation.FORUM_NEWS -> forumNewsCellElement
            }
        IOS.scrollDown()
        forumNavigationTitleElement.withScrollIos().click()
        return forumMyTopicsRobot
    }
}

enum class ForumNavigation(val index: Int) {
    ALL_DISCUSSIONS(0),
    SAVED_TOPICS(1),
    MY_TOPICS(2),
    FORUM_NEWS(0)
}
