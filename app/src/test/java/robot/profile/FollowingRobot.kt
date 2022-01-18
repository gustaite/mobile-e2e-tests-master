package robot.profile

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class FollowingRobot : BaseRobot() {

    private fun followerUsernameElementList(username: String): List<VintedElement> {
        return VintedDriver.findElementListByText(username)
    }

    private val followingButtonList: List<VintedElement>
        get() = VintedDriver.findElementList(VintedBy.id("followers_button"), VintedBy.accessibilityId("following"))

    private val followersElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.scrollableId("actionbar_label"),
            VintedBy.iOSNsPredicateString(
                "type == 'XCUIElementTypeStaticText' && name == '${IOS.getElementValue("user_follow_list_screen_followers")}'"
            )
        )

    @Step("Assert {followType.value} number is {count} and users are: {users.username}")
    fun assertFollowers(count: Int, followType: FollowType, vararg users: FollowUser): FollowingRobot {
        for (user in users) {
            VintedAssert.assertEquals(followerUsernameElementList(user.username).size, 1, "Expected user: ${user.username}")
        }
        VintedAssert.assertEquals(followingButtonList.size, count, "In list should be  $count ${followType.value}")
        return this
    }

    @Step("Is at least one 'following' button visible")
    fun isAtleastOneFollowingButtonVisible(): Boolean {
        return VintedElement.isListVisible({ followingButtonList }, 1)
    }

    @Step("Assert followers screen is opened")
    fun assertFollowersScreenIsOpened(): FollowingRobot {
        VintedAssert.assertTrue(followersElement.isVisible(), "Followers screen name element should be visible")
        return this
    }
}

enum class FollowType(val value: String) {
    Followers("follower(s)"),
    Following("following")
}

enum class FollowUser(val username: String) {
    Skorp32("skorp32"),
    VintedGintare("vinted-gintare")
}

enum class FollowAction(val value: String) {
    Follow("follow"),
    Unfollow("unfollow")
}
