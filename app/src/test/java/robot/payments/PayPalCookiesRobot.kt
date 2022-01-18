package robot.payments

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class PayPalCookiesRobot : BaseRobot() {

    private val acceptCookiesButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"acceptAllButton\")"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'Accepter les cookies' || name == 'Cookies akzeptieren' || name == 'Accept Cookies'")
        )

    private val modalElementAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.androidUIAutomator("UiSelector().resourceId(\"mainModal\")"))
    private val confirmModalButtonAndroid: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.className("android.widget.Button"))

    @Step("Accept cookies")
    fun acceptCookies() {
        if (acceptCookiesButton.isVisible()) {
            acceptCookiesButton.click()
            Android.doIfAndroid {
                if (modalElementAndroid.isVisible()) {
                    confirmModalButtonAndroid.click()
                }
            }
        }
    }
}
