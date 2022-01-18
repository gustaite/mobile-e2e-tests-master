package robot.personalization

import io.qameta.allure.Step
import robot.BaseRobot
import api.data.models.VintedUser
import commonUtil.asserts.VintedAssert
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class FollowedMembersRobot : BaseRobot() {

    private val followersButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("followers_button"),
            iOSBy = VintedBy.accessibilityId("following")
        )

    private fun followedUserElement(username: String) = VintedDriver.findElementByText(username)

    @Step("Assert followed user is visible and unfollow")
    fun assertFollowedUserIsVisibleAndUnfollow(followedUser: VintedUser) {
        if (followedUserElement(followedUser.username).isInvisible()) VintedAssert.fail("Element with text '${followedUser.username}' was not found")
        followersButton.tap()
    }
}
