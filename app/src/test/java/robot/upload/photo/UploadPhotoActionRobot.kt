package robot.upload.photo

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.CameraAction

class UploadPhotoActionRobot : BaseRobot() {

    private val cameraActionElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("android:id/text1"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeSheet/**/XCUIElementTypeButton")
        )

    private val takePhotoImageAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("gallery_image"))

    @Step("Click take photo image")
    fun clickTakePhotoImage() {
        takePhotoImageAndroid.click()
    }

    @Step("Assert take photo image is visible")
    fun assertTakePhotoImageIsVisible() {
        VintedAssert.assertTrue(takePhotoImageAndroid.withWait().isVisible(), "Take photo image should be visible")
    }

    @Step("Select action: {action}")
    fun selectAction(action: CameraAction) {
        cameraActionElementList[action.index].click()
    }

    @Step("Is old gallery")
    fun isActionVisible(): Boolean {
        return VintedElement.isListVisible({ cameraActionElementList })
    }
}
