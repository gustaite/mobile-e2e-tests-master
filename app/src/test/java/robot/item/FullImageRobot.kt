package robot.item

import RobotFactory.feedRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.FeedRobot
import util.Android
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class FullImageRobot : BaseRobot() {

    private val fullImageElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("media_full_screen_media_pager"),
            iOSBy = VintedBy.className("XCUIElementTypeImage")
        )
    private val closeButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("close"))

    private val closeFullScreenButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("media_full_screen_media_close"),
            iOSBy = VintedBy.accessibilityId("close")
        )

    @Step("Assert full screen image is open")
    fun assertFullScreenImageIsOpen(): FullImageRobot {
        VintedAssert.assertTrue(
            { if (isAndroid) fullImageElement else closeButtonIos }().isVisible(),
            "Full image screen should be visible"
        )
        return this
    }

    @Step("Swipe left to next image")
    fun swipeLeftToNextImage(): FullImageRobot {
        fullImageElement.swipeLeft()
        return this
    }

    @Step("Close full screen image")
    fun closeFullScreenImage() {
        if (isiOS) closeButtonIos.click() else Android.clickBack()
    }

    @Step("Android only: Tap on fullscreen photo for close button to appear")
    fun tapOnFullScreenPhoto(): FullImageRobot {
        Android.doIfAndroid {
            fullImageElement.tap()
        }
        return this
    }

    @Step("Assert close button is visible")
    fun assertCloseButtonIsVisible(): FullImageRobot {
        VintedAssert.assertTrue(closeFullScreenButton.isVisible(), "Close button has to be visible")
        return this
    }

    @Step("Close image fullscreen")
    fun closeImageFullScreen(): FeedRobot {
        closeFullScreenButton.click()
        return feedRobot
    }
}
