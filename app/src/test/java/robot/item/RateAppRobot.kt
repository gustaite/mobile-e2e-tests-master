package robot.item

import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class RateAppRobot : BaseRobot() {

    private val rateAppLaterButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("rate_app_later"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'Not Now' || name == 'Später' || name == 'Nie teraz' || name == 'Teď ne' ")
        )

    @Step("Click rate app later")
    fun clickRateAppLater() {
        if (isRateAppVisible()) rateAppLaterButton.click()
    }

    private fun isRateAppVisible(): Boolean {
        return rateAppLaterButton.isVisible()
    }
}
