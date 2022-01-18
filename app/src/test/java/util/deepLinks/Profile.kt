package util.deepLinks

import RobotFactory.bumpsPreCheckoutRobot
import RobotFactory.deepLink
import RobotFactory.userProfileRobot
import api.data.models.VintedItem
import io.qameta.allure.Step
import robot.profile.UserProfileEditRobot
import robot.profile.tabs.UserProfileRobot

class Profile {
    @Step("Open user with id '{userId}' profile")
    fun goToUserProfile(userId: Long): UserProfileRobot {
        openProfileUrlWithRetries("user?id=$userId")
        userProfileRobot.assertClosetTabIsVisible()
        return userProfileRobot
    }

    @Step("Open current user profile")
    fun goToMyProfile(): UserProfileRobot {
        openProfileUrlWithRetries("user")
        userProfileRobot.assertClosetTabIsVisible()
        return userProfileRobot
    }

    @Step("Open profile url '{url}' with retries")
    private fun openProfileUrlWithRetries(url: String) {
        commonUtil.Util.retryUntil(
            block = {
                deepLink.openURL(url)
                userProfileRobot.isClosetTabVisible(1)
            },
            tryForSeconds = 5
        )
    }

    @Step("Open edit profile screen")
    fun goToEditProfile(): UserProfileEditRobot {
        deepLink.openURL("user/edit_profile")
        return UserProfileEditRobot()
    }

    @Step("Open edit profile and prompt to change profile photo")
    fun goToEditProfileAndChangePhoto() {
        deepLink.openURL("user/edit_profile?change_photo=1")
    }

    @Step("Open account deletion screen")
    fun goToDeleteAccount() {
        deepLink.openURL("delete_account")
    }

    @Step("Open 'Sell' screen")
    fun goToSellScreen() {
        deepLink.openURL("sell")
    }

    @Step("Open 'Leave feedback' screen")
    fun goToLeaveFeedbackScreen(userId: Long) {
        deepLink.openURL("user/leave_feedback?id=$userId")
    }

    @Step("Open 'Closet promotion' screen")
    fun goToClosetPromotion() {
        // User must have >= 5 items
        deepLink.openURL("closet_promotion")
    }

    @Step("Go to 'Push Up Select' screen")
    fun goToPushUpSelect() {
        deepLink.openURL("push_up/select")
    }

    @Step("Open 'Push Up Review Order' screen")
    fun goToPushUpReviewOrder(item: VintedItem) {
        deepLink.openURL("push_up?id=${item.id}")
        bumpsPreCheckoutRobot.closeBumpsInfoModalIfVisible()
    }
}
