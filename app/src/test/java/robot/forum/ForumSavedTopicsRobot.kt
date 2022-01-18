package robot.forum

import RobotFactory.forumHomeRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ForumSavedTopicsRobot : BaseRobot() {

    private val emptyStateElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_empty_state_body")
        )

    @Step("Assert that favorite list is empty")
    fun assertFavoriteListIsEmpty(): ForumSavedTopicsRobot {
        commonUtil.Util.retryAction(
            { emptyStateElement.isVisible(1) },
            {
                forumHomeRobot.navigateToSelectedForumHomeCell(ForumNavigation.ALL_DISCUSSIONS)
                forumHomeRobot.navigateToSelectedForumHomeCell(ForumNavigation.SAVED_TOPICS)
            }
        )

        VintedAssert.assertTrue(emptyStateElement.isVisible(1), "Empty favorite list should be visible")
        return this
    }
}
