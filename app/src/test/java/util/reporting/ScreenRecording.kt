package util.reporting

import io.appium.java_client.android.AndroidStopScreenRecordingOptions
import io.appium.java_client.ios.IOSStartScreenRecordingOptions
import io.appium.java_client.ios.IOSStopScreenRecordingOptions
import io.appium.java_client.screenrecording.ScreenRecordingUploadOptions
import io.qameta.allure.Step
import org.openqa.selenium.WebDriverException
import util.EnvironmentManager
import util.driver.*
import java.util.*

class ScreenRecording {
    @Step("Start recording")
    fun startRecording() {
        try {
            when {
                EnvironmentManager.isAndroid -> {
                    WebDriverFactory.driver.asAndroidDriver().startRecordingScreen()
                }
                else -> {
                    WebDriverFactory.driver.asIOSDriver().startRecordingScreen(IOSStartScreenRecordingOptions().withVideoType("libx264"))
                }
            }
        } catch (e: WebDriverException) {
            commonUtil.reporting.Report.addMessage("Sorry could not start to record a video. Maybe a device restart is needed")
            commonUtil.reporting.Report.addMessage("Error: $e")
        }
    }

    fun stopRecording(saveRecording: Boolean): ByteArray? {
        val noPlaceUrl = "http://this.place.does.not.exist"
        return try {
            when {
                EnvironmentManager.isAndroid -> {
                    if (saveRecording) {
                        Base64.getDecoder().decode(WebDriverFactory.driver.asAndroidDriver().stopRecordingScreen())
                    } else {
                        WebDriverFactory.driver.asAndroidDriver()
                            .stopRecordingScreen(
                                AndroidStopScreenRecordingOptions().withUploadOptions(
                                    ScreenRecordingUploadOptions().withRemotePath(
                                        noPlaceUrl
                                    )
                                )
                            )
                        null
                    }
                }
                else -> {
                    if (saveRecording) {
                        Base64.getDecoder().decode(WebDriverFactory.driver.asIOSDriver().stopRecordingScreen())
                    } else {
                        WebDriverFactory.driver.asIOSDriver()
                            .stopRecordingScreen(
                                IOSStopScreenRecordingOptions().withUploadOptions(
                                    ScreenRecordingUploadOptions().withRemotePath(
                                        noPlaceUrl
                                    )
                                )
                            )
                        null
                    }
                }
            }
        } catch (e: WebDriverException) {
            if (!e.toString().contains(noPlaceUrl.replace("http://", ""))) {
                commonUtil.reporting.Report.addMessage("Sorry no video today :(")
                commonUtil.reporting.Report.addMessage("Error: $e")
            }
            null
        }
    }
}
