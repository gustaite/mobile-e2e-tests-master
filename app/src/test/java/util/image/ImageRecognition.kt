package util.image

import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.data.VintedFolder
import commonUtil.data.Image
import io.appium.java_client.imagecomparison.OccurrenceMatchingOptions
import io.appium.java_client.imagecomparison.OccurrenceMatchingResult
import io.qameta.allure.Step
import org.apache.commons.codec.binary.Base64
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriverException
import util.EnvironmentManager.isiOS
import util.Session.Companion.isOldIos
import util.Session.Companion.sessionDetails
import util.driver.VintedElement
import util.driver.WebDriverFactory
import util.values.LaboratoryDevice

class ImageRecognition {

    companion object {

        @Step("Is image in element")
        fun isImageInElement(
            element: VintedElement?, image: Image, retryCount: Int = 10, threshold: Double = 0.5, shouldBeInElement: Boolean = true
        ):
            Pair<Boolean, OccurrenceMatchingResult?> {

            return isImageInElement(element, image.bytes, retryCount, threshold, shouldBeInElement)
        }

        @Step("Is image in element")
        fun isImageInElement(
            element: VintedElement?, fileBytes: ByteArray, retryCount: Int = 10, threshold: Double = 0.5, shouldBeInElement: Boolean = true
        ):
            Pair<Boolean, OccurrenceMatchingResult?> {
            commonUtil.reporting.Report.saveImageToReport(fileBytes, "Base image")

            val base = Base64.encodeBase64(fileBytes)
            var result: Pair<Boolean?, OccurrenceMatchingResult?> = Pair(false, null)
            loop@ for (@Suppress("UNUSED_PARAMETER") i in 0..retryCount) {
                result = occurrence(base, threshold, element)
                if (shouldBeInElement) {
                    if (result.first != null) {
                        break@loop
                    }
                } else {
                    if (result.first == null) {
                        break@loop
                    }
                }
                sleepWithinStep(500)
            }
            return Pair(result.first ?: false, result.second)
        }

        @Step("Check if {file.name} image is in screen")
        fun isImageInScreen(file: Image, retryCount: Int = 10, threshold: Double = 0.5, shouldBeInElement: Boolean = true): Boolean {
            return isImageInElement(null, file, retryCount, threshold, shouldBeInElement).first
        }

        private fun occurrence(
            base: ByteArray,
            threshold: Double,
            element: VintedElement? = null
        ): Pair<Boolean?, OccurrenceMatchingResult?> {
            var result: OccurrenceMatchingResult? = null
            val screenshot = getCurrentScreenshot(element)
            commonUtil.reporting.Report.saveImageToReport(Base64.decodeBase64(screenshot), "current window")
            try {
                result = WebDriverFactory.driver
                    .findImageOccurrence(
                        screenshot, base,
                        OccurrenceMatchingOptions()
                            .withEnabledVisualization()
                            .withThreshold(threshold)
                    )
            } catch (e: WebDriverException) {
                commonUtil.reporting.Report.addMessage(e.localizedMessage)
                if (result != null) {
                    commonUtil.reporting.Report.saveImageToReport(Base64.decodeBase64(result.visualization), "visualization")
                }
                return Pair(null, result)
            }

            commonUtil.reporting.Report.saveImageToReport(Base64.decodeBase64(result.visualization), "visualization")
            return Pair(result?.rect != null, result)
        }

        fun getCurrentScreenshot(element: VintedElement?): ByteArray {
            if (element?.mobileElement != null) {
                return Base64.encodeBase64(element.mobileElement.getScreenshotAs(OutputType.BYTES))
            }
            return Base64.encodeBase64(Screenshot.takeScreenshot().screenshot)
        }
    }
}

class ImageFactory {
    companion object {
        private fun getImageByDeviceModel(a30sImage: String, regularImage: String): String {
            return getImageByDeviceModel(hashMapOf(LaboratoryDevice.A30s.model to a30sImage), regularImage)
        }

        private fun getImageByDeviceModel(deviceModelSpecificImage: HashMap<String, String>, regularImage: String): String {
            return if (deviceModelSpecificImage.containsKey(sessionDetails.deviceModel)) {
                deviceModelSpecificImage.getValue(sessionDetails.deviceModel)
            } else regularImage
        }

        val PHOTO_TIPS = Image("photo_tips.png")
        val PHOTO_TIPS_BABIES = Image("photo_tips_babies.png")
        val PHOTO_TIPS_BABIES_IN_GRID = Image("photo_tips_babies_in_grid.png")
        val CAT = Image("cat.jpg")
        val CAT_IN_GRID
            get() = Image(
                getImageByDeviceModel(
                    hashMapOf(
                        LaboratoryDevice.A30s.model to "cat_in_grid_A30s.jpg",
                        LaboratoryDevice.NOKIA_53.model to "cat_in_grid_nokia.png"
                    ),
                    "cat_in_grid.jpg"
                )
            )
        val TIPS_IN_GRID
            get() = Image(getImageByDeviceModel("tips_in_grid_A30s.jpeg", "tips_in_grid.jpeg"))
        val CROPPED_ROTATED_CAT
            get() = Image(getImageByDeviceModel("cropped_rotated_cat_A30s.jpg", "cropped_rotated_cat.jpg"))
        val CROPPED_CAT
            get() = Image(
                getImageByDeviceModel(
                    hashMapOf(
                        LaboratoryDevice.A30s.model to "cropped_cat_A30s.jpg",
                        LaboratoryDevice.NOKIA_53.model to "cropped_cat_nokia.png"
                    ),
                    "cropped_cat.jpg"
                )
            )
        val NUMBER_2
            get() = Image(
                if (isiOS) "number2_ios.png" else {
                    getImageByDeviceModel(
                        hashMapOf(
                            LaboratoryDevice.NOKIA_53.model to "number2_Nokia.png",
                            LaboratoryDevice.A30s.model to "number2_A30s.png",
                            LaboratoryDevice.GALAXY_NOTE_10_PLUS.model to "number2_GalaxyNote10.png",
                            LaboratoryDevice.ONE_PLUS_8_PRO.model to "number2_OnePlus8Pro.png"
                        ),
                        "number2.png"
                    )
                },
                VintedFolder.PROFILE
            )
        val PROFILE_PHOTO_CAT = Image("profile_photo_cat.jpg", VintedFolder.PROFILE)
        val ITEM_1_PHOTO = Image("item_photo_1.jpg", VintedFolder.ITEM)
        val ITEM_2_PHOTO = Image("item_photo_2.jpg", VintedFolder.ITEM)
        val ITEM_2_PHOTO_CROPPED = Image("item_photo_2_cropped.png", VintedFolder.ITEM)
        val ITEM_1_PHOTO_CROPPED = Image("item_photo_1_cropped.png", VintedFolder.ITEM)
        val ITEM_1_PHOTO_CHECKOUT = Image("item_photo_1_checkout.jpg", VintedFolder.ITEM)
        val ITEM_1_PHOTO_ITEM_BOX = Image("item_photo_1_item_box.jpg", VintedFolder.ITEM)
        val ITEM_3_PHOTO = Image("item_photo_3.jpg", VintedFolder.ITEM)
        val ITEM_4_PHOTO = Image("item_photo_4.jpg", VintedFolder.ITEM)
        val ITEM_5_PHOTO = Image("item_photo_5.jpg", VintedFolder.ITEM)
        val ITEM_6_PHOTO = Image("item_photo_6.jpg", VintedFolder.ITEM)
        val ITEM_7_PHOTO = Image("item_photo_7.jpg", VintedFolder.ITEM)
        val ITEM_8_PHOTO = Image("item_photo_8.jpg", VintedFolder.ITEM)
        val ITEM_9_PHOTO = Image("item_photo_9.jpg", VintedFolder.ITEM)
        val ITEM_10_PHOTO = Image("item_photo_10.jpg", VintedFolder.ITEM)
        val ITEM_11_PHOTO = Image("item_photo_11.jpg", VintedFolder.ITEM)
        val ITEM_12_PHOTO = Image("item_photo_12.jpg", VintedFolder.ITEM)
        val ITEM_13_PHOTO = Image("item_photo_13.jpg", VintedFolder.ITEM)
        val ITEM_14_PHOTO = Image("item_photo_14.jpg", VintedFolder.ITEM)
        val ITEM_15_PHOTO = Image("item_photo_15.jpg", VintedFolder.ITEM)
        val ITEM_16_PHOTO = Image("item_photo_16.jpg", VintedFolder.ITEM)
        val ITEM_17_PHOTO = Image("item_photo_17.jpg", VintedFolder.ITEM)
        val ITEM_3a_PHOTO = Image("item_photo_3a.jpg", VintedFolder.ITEM)
        val ITEM_4a_PHOTO = Image("item_photo_4a.jpg", VintedFolder.ITEM)
        val ITEM_5a_PHOTO = Image("item_photo_5a.jpg", VintedFolder.ITEM)
        val ITEM_6a_PHOTO = Image("item_photo_6a.jpg", VintedFolder.ITEM)
        val ITEM_7a_PHOTO = Image("item_photo_7a.jpg", VintedFolder.ITEM)
        val ITEM_8a_PHOTO = Image("item_photo_8a.jpg", VintedFolder.ITEM)
        val ITEM_9a_PHOTO = Image("item_photo_9a.jpg", VintedFolder.ITEM)
        val ITEM_10a_PHOTO = Image("item_photo_10a.jpg", VintedFolder.ITEM)
        val ITEM_11a_PHOTO = Image("item_photo_11a.jpg", VintedFolder.ITEM)
        val ITEM_12a_PHOTO = Image("item_photo_12a.jpg", VintedFolder.ITEM)
        val ITEM_13a_PHOTO = Image("item_photo_13a.jpg", VintedFolder.ITEM)
        val ITEM_14a_PHOTO = Image("item_photo_14a.jpg", VintedFolder.ITEM)
        val ITEM_15a_PHOTO = Image("item_photo_15a.jpg", VintedFolder.ITEM)
        val ITEM_16a_PHOTO = Image("item_photo_16a.jpg", VintedFolder.ITEM)
        val ITEM_17a_PHOTO = Image("item_photo_17a.jpg", VintedFolder.ITEM)
        val ITEM_FAVORITE_HEART_ACTIVE
            get() = Image(getImageByDeviceModel(hashMapOf(LaboratoryDevice.A30s.model to "item_favorite_heart_active_A30s.jpg", LaboratoryDevice.NOKIA_53.model to "item_favorite_heart_active_nokia.png"), "item_favorite_heart_active.jpg"), VintedFolder.ITEM)
        val ITEM_FAVORITE_HEART_INACTIVE = Image("item_favorite_heart_inactive.jpg", VintedFolder.ITEM)
        val SUSPICIOUS_PHOTO_THUMBNAIL get() = Image(if (isOldIos) "suspicious_photo_thumbnail_oldiOS.jpg" else "suspicious_photo_thumbnail.jpg")
        val DEFAULT_AVATAR = Image("default_avatar.jpg")
        val TRACKING_LINK = Image("tracking_link.png")
    }
}
