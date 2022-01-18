package robot.upload

import RobotFactory.actionBarRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.ActionBarRobot
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class PhotoTipModalRobot : BaseRobot() {

    private val photoTipsTitleElement: VintedElement get() = VintedDriver.elementByIdAndTranslationKey("photo_tips_container", "photo_tips_title")

    private val imageElementIos: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("photo_tip_image"), iOSBy = VintedBy.className("XCUIElementTypeImage"))

    @Step("Assert if elements in photo tips modal are visible")
    fun assertPhotoTipsModalIsLoaded(): PhotoTipModalRobot {
        VintedAssert.assertTrue(photoTipsTitleElement.isVisible(), "Photo tips title should be visible")
        VintedAssert.assertTrue(imageElementIos.isVisible(), "Photo tips image should be visible")
        return this
    }

    @Step("Assert if elements in photo tips modal are NOT visible")
    fun assertPhotoTipsModalIsNotLoaded(): PhotoTipModalRobot {
        VintedAssert.assertTrue(photoTipsTitleElement.isInvisible(), "Photo tips title should NOT be visible")
        VintedAssert.assertTrue(imageElementIos.isInvisible(), "Photo tips image should NOT be visible")
        return this
    }

    @Step("Assert if elements in photo tips modal are visible or invisible {photoTipsVisibility}")
    fun assertPhotoTipsVisibility(photoTipsVisibility: PhotoTipsVisibility): ActionBarRobot {
        when (photoTipsVisibility) {
            PhotoTipsVisibility.Visible -> assertPhotoTipsModalIsLoaded()
            PhotoTipsVisibility.Invisible -> assertPhotoTipsModalIsNotLoaded()
        }
        return actionBarRobot
    }
}

enum class PhotoTipsVisibility {
    Visible,
    Invisible
}
