package robot.forum

import RobotFactory.createForumTopicRobot
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ForumInnerRobot : BaseRobot() {

    private val addTopicButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("forum_actionbar_new_topic_button"),
            iOSBy = VintedBy.accessibilityId("compose")
        )

    @Step("Click add new forum topic button")
    fun clickAddNewTopicButton(): CreateForumTopicRobot {
        addTopicButtonElement.click()
        return createForumTopicRobot
    }
}
