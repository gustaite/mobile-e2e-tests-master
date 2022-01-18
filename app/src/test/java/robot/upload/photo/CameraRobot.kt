package robot.upload.photo

import RobotFactory.cameraAndGalleryWorkflowRobot
import RobotFactory.modalRobot
import RobotFactory.uploadItemRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import org.apache.commons.codec.binary.Base64
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.assertVisibilityEquals
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.image.ImageRecognition
import util.values.Visibility

class CameraRobot : BaseRobot() {

    private val doneButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("done_button"),
            iOSBy = VintedBy.accessibilityId("save")
        )

    private val capturePhotoButtonAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("image_capture_button")
        )

    private val rotateElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("image_rotate_button"),
            iOSBy = VintedBy.accessibilityId("rotateSquareIcon")
        )

    private val capturedPictureCancellationModalElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("modal_title", Android.getElementValue("general_delete_prompt_title"))
        )

    private val modalNoButtonElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("modal_secondary_button")
        )

    private val cameraViewPhotoElementsAndroid: List<VintedElement> get() = VintedDriver.findElementList(androidBy = VintedBy.id("media_view"))

    private val cameraViewPlaceholderElementsAndroid: List<VintedElement> get() = VintedDriver.findElementList(androidBy = VintedBy.id("placeholder_view"))

    private val cameraViewBackButtonElementAndroid: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("back_button"))

    @Step("Click done")
    fun clickDone() {
        doneButton.withWait(seconds = 20).click()
    }

    @Step("Take photo {pictureNumber}")
    fun takePhoto(pictureNumber: PictureNumber) {
        capturePhotoButtonAndroid.withWait()
        sleepWithinStep(800)
        val base = Base64.decodeBase64(
            ImageRecognition.getCurrentScreenshot(
                if (pictureNumber.value) {
                    cameraViewPlaceholderElementsAndroid.first().withWait(WaitFor.Visible)
                } else { cameraViewPhotoElementsAndroid.last().withWait(WaitFor.Visible) }
            )
        )

        capturePhotoButtonAndroid.tap()
        var isSameImage = true
        loop@ for (i in 1..5) {
            isSameImage = ImageRecognition.isImageInElement(
                if (pictureNumber.value) {
                    cameraViewPhotoElementsAndroid.first().withWait(WaitFor.Visible)
                } else { cameraViewPhotoElementsAndroid.last().withWait(WaitFor.Visible) },
                base, retryCount = 0, shouldBeInElement = false
            ).first
            if (!isSameImage) break@loop
            sleepWithinStep(500)
            commonUtil.reporting.Report.addMessage("Retried: $i")
        }
        VintedAssert.assertFalse(isSameImage, "Image has not changed after 5 retries")
    }

    @Step("Rotate photo")
    fun rotate(): CameraRobot {
        rotateElement.tap()
        return this
    }

    @Step("Android only: Rearrange 2 photos")
    fun rearrangePhotosInCameraView(): CameraRobot {
        uploadItemRobot.clickOnPhoto()
        cameraViewPhotoElementsAndroid.first().performDragAndDrop(cameraViewPlaceholderElementsAndroid.first())
        return this
    }

    @Step("Click no in captured photo cancellation modal")
    fun clickNoInCapturedPhotoCancellationModal(): CameraRobot {
        modalNoButtonElementAndroid.click()
        return this
    }

    @Step("Click back in camera view")
    fun clickBackInCameraView(): CameraRobot {
        cameraViewBackButtonElementAndroid.click()
        return this
    }

    @Step("Android only: Assert information message is {visibility} after canceling taken photos from camera view")
    fun assertInformationAboutCancelingTakenPhotosVisibility(visibility: Visibility): CameraAndGalleryWorkflowRobot {
        modalRobot.isModalVisible()
        VintedAssert.assertVisibilityEquals(capturedPictureCancellationModalElementAndroid, visibility, "Information message about canceling captured photo should be $visibility")
        return cameraAndGalleryWorkflowRobot
    }
}

enum class PictureNumber(val value: Boolean) {
    FIRST(true),
    ADDITIONAL(false)
}
