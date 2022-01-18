package robot.browsers

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy

class ChromeTermsRobot : BaseRobot() {

    private val acceptChromeTermsButton get() = VintedDriver.findElement(androidBy = VintedBy.id("com.android.chrome:id/terms_accept"))
    private val noThanksSyncButton get() = VintedDriver.findElement(androidBy = VintedBy.id("com.android.chrome:id/negative_button"))
    private val noThanksLiteButton get() = VintedDriver.findElement(androidBy = VintedBy.id("com.android.chrome:id/button_secondary"))

    @Step("Handle Chrome terms on the browser")
    fun handleChromeTerms() {
        Android.doIfAndroid {
            acceptChromeTerms()
            rejectAccountSyncOnChrome()
            rejectLiteChromeMode()
        }
    }

    @Step("Accept Chrome terms if visible")
    private fun acceptChromeTerms() {
        if (acceptChromeTermsButton.isVisible(1)) {
            acceptChromeTermsButton.click()
        }
    }

    @Step("Reject account sync on Chrome")
    private fun rejectAccountSyncOnChrome() {
        if (noThanksSyncButton.isVisible(1)) {
            noThanksSyncButton.click()
        }
    }

    @Step("Reject lite Chrome mode")
    private fun rejectLiteChromeMode() {
        if (noThanksLiteButton.isVisible(1)) {
            noThanksLiteButton.click()
        }
    }
}
