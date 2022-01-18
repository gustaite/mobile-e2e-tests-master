package util.image

import org.openqa.selenium.*
import util.driver.WebDriverFactory

object Screenshot {

    fun takeScreenshot(): ScreenshotResult {

        val screenshotResult = ScreenshotResult()

        try {
            screenshotResult.screenshot = (WebDriverFactory.driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
        } catch (e: WebDriverException) {
            commonUtil.reporting.Report.addMessage("Sorry, was not able to take a screenshot. ${e.localizedMessage}")
            screenshotResult.errorMessage = e.localizedMessage

            if (e.localizedMessage.contains("Error: read ECONNRESET")) {
                screenshotResult.driverExceptionNeedsRestart = true
            }
        }
        return screenshotResult
    }
}

class ScreenshotResult {
    var screenshot: ByteArray? = null
    var errorMessage: String? = null
    var driverExceptionNeedsRestart: Boolean = false
}
