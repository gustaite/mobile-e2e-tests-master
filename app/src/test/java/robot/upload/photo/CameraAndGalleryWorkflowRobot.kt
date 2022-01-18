package robot.upload.photo

import RobotFactory.cameraRobot
import RobotFactory.galleryRobot
import RobotFactory.uploadItemRobot
import RobotFactory.uploadPhotoActionRobot
import commonUtil.data.Image
import io.qameta.allure.Step
import org.openqa.selenium.NoSuchElementException
import robot.BaseRobot
import robot.upload.UploadItemRobot
import util.Android
import util.values.CameraAction
import util.values.Visibility

class CameraAndGalleryWorkflowRobot : BaseRobot() {

    @Step("Select {image.name} photo from gallery by image recognition")
    fun selectPhotoFromGalleryByImageRecognition(image: Image, threshold: Double = 0.44): UploadItemRobot {
        commonUtil.Util.retryAction(
            {
                try {
                    galleryRobot.selectPhotoByImageRecognition(image, threshold = threshold)
                    galleryRobot.clickDoneInGallery(); true
                } catch (e: Exception) {
                    when (e) {
                        is NoSuchElementException -> Android.clickBack() // Exception came from clickDoneInGallery
                    }
                    commonUtil.reporting.Report.addMessage("Exception was caught: ${e.message}")
                    false
                }
            },
            {
                Android.scrollDown()
            },
            3
        )
        return uploadItemRobot
    }

    @Step("Select photo/s {count} from gallery")
    fun selectPhotosFromGallery(count: Int = 1): UploadItemRobot {
        if (isOldGallery()) {
            uploadPhotoActionRobot.selectAction(CameraAction.PHOTO_FROM_GALLERY)
        }
        galleryRobot.selectNumberOfPhotosFromGallery(count)
        galleryRobot.clickDoneInGallery()
        return uploadItemRobot
    }

    private fun isOldGallery(): Boolean {
        return uploadPhotoActionRobot.isActionVisible()
    }

    @Step("Open camera and take {pictureNumber} photo")
    fun openCameraAndTakePhoto(pictureNumber: PictureNumber): CameraAndGalleryWorkflowRobot {
        uploadPhotoActionRobot.clickTakePhotoImage()
        cameraRobot.takePhoto(pictureNumber)
        return this
    }

    @Step("Open camera and take additional photo")
    fun openCameraAndTakeAdditionalPhoto(): UploadItemRobot {
        openCameraAndTakePhoto(PictureNumber.ADDITIONAL)
        cameraRobot.clickDone()
        return uploadItemRobot
    }

    @Step("Click back in camera view and assert picture discard modal is {visibility}")
    fun clickBackInCameraViewAndAssertModalVisibility(visibility: Visibility = Visibility.Visible): CameraRobot {
        cameraRobot
            .clickBackInCameraView()
            .assertInformationAboutCancelingTakenPhotosVisibility(visibility)
        return cameraRobot
    }
}
