package robot.upload.photo

import RobotFactory.uploadItemRobot
import RobotFactory.cameraAndGalleryWorkflowRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.data.Image
import io.qameta.allure.Step
import org.openqa.selenium.InvalidArgumentException
import org.openqa.selenium.StaleElementReferenceException
import org.testng.SkipException
import robot.ActionBarRobot
import robot.BaseRobot
import robot.upload.UploadItemRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.image.ImageFactory
import util.image.ImageRecognition

class GalleryRobot : BaseRobot() {
    private val actionBarRobot: ActionBarRobot get() = ActionBarRobot()
    private val galleryGridAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("photo_grid"))
    private val galleryGridImagesAndroid: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id(
                "gallery_image"
            )
        )

    private val photoInGalleryElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.xpath("//android.widget.FrameLayout[* and not(*[not(self::android.widget.ImageView)])]//android.widget.ImageView"),
            VintedBy.className("XCUIElementTypeCell")
        )

    private val selectedPhotoIconInGalleryElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("image_selection_badge"),
            VintedBy.iOSClassChain("**/XCUIElementTypeCell[\$value.length > 0\$]")
        )

    private val firstImageElementIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.className("XCUIElementTypeImage"))

    @Step("Select {image.name} photo by image recognition")
    fun selectPhotoByImageRecognition(image: Image, threshold: Double = 0.44) {
        galleryGridAndroid.withWait()
        val (isInImage, result) = ImageRecognition.isImageInElement(galleryGridAndroid, image, threshold = threshold, retryCount = 1)
        var x = galleryGridAndroid.location.x
        var y = galleryGridAndroid.location.y

        if (isInImage) {
            x += result!!.rect.point.x
            y += result.rect.point.y
        } else {
            throw NullPointerException("Was looking for ${image.name} and didn't find")
        }
        Android.tap(x, y)
    }

    @Step("Select {count} photos from gallery")
    fun selectNumberOfPhotosFromGallery(count: Int): GalleryRobot {
        IOS.doIfiOS { firstImageElementIos.isVisible(3) }
        var useOffset = true
        repeat(count) {
            if (isAndroid) {
                try {
                    val index = if (useOffset) 1 else photoInGalleryElementList.count() - 1 // Select from the end
                    photoInGalleryElementList[index].tap()
                } catch (exception: Exception) {
                    when (exception) {
                        is InvalidArgumentException,
                        is StaleElementReferenceException,
                        is IndexOutOfBoundsException -> {
                            Android.scrollDown()
                            sleepWithinStep(500)
                            useOffset = false // After scroll down select from the end
                            photoInGalleryElementList[photoInGalleryElementList.count() - 1].tapWithRetry()
                        }
                        else -> throw exception
                    }
                }
            } else {
                val index = selectedPhotoIconInGalleryElementList.count() + 1
                photoInGalleryElementList[index].tap()
            }
        }
        return this
    }

    @Step("Click done in the gallery")
    fun clickDoneInGallery() {
        actionBarRobot.submit(seconds = 2)
    }

    @Step("Select 2 specific photos in gallery")
    fun selectTwoSpecificPicturesInGallery(): UploadItemRobot {
        Android.doIfAndroid {
            cameraAndGalleryWorkflowRobot
                .selectPhotoFromGalleryByImageRecognition(ImageFactory.TIPS_IN_GRID)
                .clickAddPhoto()
                .selectPhotoFromGalleryByImageRecognition(ImageFactory.ITEM_1_PHOTO_CHECKOUT)
        }
        IOS.doIfiOS {
            photoInGalleryElementList[PictureIos.TIPS_IN_GRID.value].tap()
            photoInGalleryElementList[PictureIos.ITEM_1_PHOTO_CHECKOUT.value].tap()
            clickDoneInGallery()
        }
        return uploadItemRobot
    }

    @Step("Check if first selectable image in gallery is the one test expects otherwise skip test")
    fun assertFirstSelectableImageIsExpectedOneAndroid(images: List<Image>): GalleryRobot {
        Android.doIfAndroid {
            galleryGridAndroid.withWait()
            if (!ImageRecognition.isImageInElement(
                    element = galleryGridImagesAndroid[1],
                    image = images.first(),
                    threshold = 0.5,
                    retryCount = 1
                ).first
            ) throw SkipException("Skipping test because images were uploaded in incorrect order")
        }
        return this
    }

    @Step("Select first 2 expected photos in gallery")
    fun selectFirstTwoExpectedImagesInGallery(): UploadItemRobot {
        Android.doIfAndroid {
            galleryGridAndroid.withWait()
            galleryGridImagesAndroid[1].click() // clicking on the second element because the first element is take photo button
            galleryGridImagesAndroid[2].click()
        }

        IOS.doIfiOS {
            photoInGalleryElementList[PictureIos.TIPS_IN_GRID.value].tap()
            photoInGalleryElementList[PictureIos.ITEM_1_PHOTO_CHECKOUT.value].tap()
        }

        clickDoneInGallery()

        return uploadItemRobot
    }

    @Step("Select 1 specific photo in gallery")
    fun selectOneSpecificPictureInGallery(): UploadItemRobot {
        Android.doIfAndroid { cameraAndGalleryWorkflowRobot.selectPhotoFromGalleryByImageRecognition(image = ImageFactory.CAT_IN_GRID) }
        IOS.doIfiOS {
            repeat(3) { IOS.scrollDown() }
            photoInGalleryElementList[PictureIos.CAT_IN_GRID.value].tap()
            clickDoneInGallery()
        }
        return uploadItemRobot
    }

    @Step("Select a web photo in gallery")
    fun selectWebPhoto(): UploadItemRobot {
        Android.doIfAndroid { cameraAndGalleryWorkflowRobot.selectPhotoFromGalleryByImageRecognition(image = ImageFactory.PHOTO_TIPS_BABIES_IN_GRID, threshold = 0.4) }
        IOS.doIfiOS {
            photoInGalleryElementList[PictureIos.PHOTO_TIPS_BABIES_IN_GRID.value].tap()
            clickDoneInGallery()
        }
        return uploadItemRobot
    }
}

enum class PictureIos(val value: Int) {
    TIPS_IN_GRID(1),
    ITEM_1_PHOTO_CHECKOUT(5),
    CAT_IN_GRID(19),
    PHOTO_TIPS_BABIES_IN_GRID(2)
}
