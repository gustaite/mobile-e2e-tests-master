package util.image

import com.google.common.collect.ImmutableMap
import commonUtil.data.Image
import io.qameta.allure.Step
import org.apache.commons.codec.binary.Base64
import org.openqa.selenium.WebDriverException
import util.Android
import util.EnvironmentManager.deleteAndroidFiles
import util.EnvironmentManager.isAndroid
import util.Session
import util.driver.*
import util.values.LaboratoryDevice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ImageUtil {
    companion object {
        private const val INTERNAL_STORAGE_PATH_ANDROID = "/storage/emulated/0/"
        private const val CAMERA_PATH_ANDROID_REGULAR = "${INTERNAL_STORAGE_PATH_ANDROID}DCIM/Camera/"
        private const val CAMERA_PATH_ANDROID_MOTOROLA_ONE_ZOOM = "${INTERNAL_STORAGE_PATH_ANDROID}Pictures/AB_Camera/"
        private val CAMERA_PATH_ANDROID get() = when (Session.sessionDetails.deviceUdid) {
            LaboratoryDevice.MOTOROLA_ONE_ZOOM.udid -> CAMERA_PATH_ANDROID_MOTOROLA_ONE_ZOOM
            else -> CAMERA_PATH_ANDROID_REGULAR
        }

        private fun executeTerminalCommand(command: String, arguments: String = "$CAMERA_PATH_ANDROID*.*"): String {
            val args = listOf(arguments)
            val cmd: Map<String, Any> = ImmutableMap.of(
                "command", command,
                "args", args
            )
            return try {
                WebDriverFactory.driver.executeScript("mobile: shell", cmd) as String
            } catch (e: WebDriverException) {
                ""
            }
        }

        private fun getCameraFilesInfoAsString(): String {
            return executeTerminalCommand("ls -l")
        }

        @Step("Delete all camera files")
        private fun deleteAllCameraFiles() {
            executeTerminalCommand("rm")
            doMediaRescan(CAMERA_PATH_ANDROID)
        }

        private fun getCameraFilesInfoAsList(): List<String> {
            return getCameraFilesInfoAsString().trimEnd().split("\n")
        }

        private fun areAllImagesUploadedToday(): Boolean {
            val filesList = getCameraFilesInfoAsList()
            val date: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            filesList.forEach {
                if (!it.contains(date)) return false
            }
            return true
        }

        private fun areAllRequiredImagesAvailable(images: List<String>): Boolean {
            val filesInfo = getCameraFilesInfoAsString()
            images.forEach {
                if (!filesInfo.contains(it)) return false
            }
            return true
        }

        private fun shouldUploadToDevice(imageList: List<Image>): Boolean {
            if (isAndroid) {
                val uploadImages = !(areAllRequiredImagesAvailable(imageList.map { it.name }) && areAllImagesUploadedToday())
                commonUtil.reporting.Report.addMessage("Upload images: $uploadImages Delete android files: $deleteAndroidFiles")
                return uploadImages
            } else {
                return try {
                    /*
                    There are 5 or 6 initial stock photos so after seeding our photos,
                    the last file name would be IMG_0010 or IMG_0011 because iOS replaces file names,
                    so we should only seed photos if file with IMG_00010 name doesn't exist in the camera.
                     */
                    val driver = WebDriverFactory.driver.asIOSDriver()
                    driver.pullFile("Media/DCIM/100APPLE/IMG_0010.JPG")
                    false
                } catch (e: WebDriverException) {
                    e.message!!.contains("does not exist")
                }
            }
        }

        @Step("Upload images to Camera folder once")
        fun uploadImageToDeviceOnceInDay(imageList: List<Image>) {
            if (shouldUploadToDevice(imageList)) {
                if (deleteAndroidFiles) {
                    deleteAllCameraFiles()
                }
                uploadImagesToDevice(imageList)
            }
        }

        @Step("Upload images to Camera folder")
        fun uploadImagesToDevice(imageList: List<Image>) {
            if (isAndroid) {
                val driver = WebDriverFactory.driver.asAndroidDriver()
                for (i in imageList.indices) {
                    driver.pushFile("$CAMERA_PATH_ANDROID${imageList[i].name}", Base64.encodeBase64(imageList[i].bytes))
                    commonUtil.reporting.Report.addMessage("Uploaded ${imageList[i].name} photo")
                }
            } else {
                val driver = WebDriverFactory.driver.asIOSDriver()
                for (i in imageList.indices) {
                    driver.pushFile(imageList[i].name, Base64.encodeBase64(imageList[i].bytes))
                    commonUtil.reporting.Report.addMessage("Uploaded ${imageList[i].name} photo")
                }
            }
        }

        @Step("Delete all files from internal storage")
        fun deleteAllInternalStorageFiles() {
            Android.doIfAndroid {
                executeTerminalCommand("rm", "$INTERNAL_STORAGE_PATH_ANDROID*.*")
                doMediaRescan(INTERNAL_STORAGE_PATH_ANDROID)
            }
        }

        @Step("Media rescan done on path: {path}")
        fun doMediaRescan(path: String) {
            executeTerminalCommand("command", "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://$path")
        }
    }
}
