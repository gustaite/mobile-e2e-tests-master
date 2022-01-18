package test.basic

import RobotFactory.deepLink
import RobotFactory.feedRobot
import api.controllers.user.userApi
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink

@RunMobile
@Feature("Feed tests")
@LoginToMainThreadUser
class FeedTests : BaseTest() {

    @BeforeMethod
    fun turnOffFavoriteItemsNotifications() {
        loggedInUser.userApi.turnOffFavoriteItemNotification()
    }

    @Test(description = "Favorite and unfavorite item from feed")
    @TmsLink("63")
    fun testItemFavoritingAndUnfavoritingFromFeed() {
        feedRobot.scrollDownToSkipHomepageElements()
        feedRobot
            .assertFirstHeartIconIsNotRed()
            .clickOnFirstHeartIcon()
            .assertFirstHeartIconIsRed()
            .clickOnFirstHeartIcon()
            .assertFirstHeartIconIsNotRed()
        deepLink.setting
            .goToFavorites()
            .assertEmptyStateTitleIsVisible()
    }

    @Test(description = "Click on item in feed and check that item screen is opened")
    @RunMobile(neverRunOnSandbox = true)
    fun testItemOpeningFromFeed() {
        feedRobot
            .openItem()
            .assertMessageButtonIsVisibleInItemScreen()
    }

    @Test(description = "Long press on item in feed and check if image is opened fullscreen")
    @TmsLink("7613")
    fun testViewingImageInFullscreenFromFeed() {
        feedRobot
            .scrollDownToSkipHomepageElements()
            .scrollDownIfClosetPromoIsVisible()
            .longPressOnItem()
            .assertFullScreenImageIsOpen()
            .tapOnFullScreenPhoto()
            .assertCloseButtonIsVisible()
            .closeImageFullScreen()
            .assertFeedIsVisible()
    }
}
