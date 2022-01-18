package robot.holidaymode

import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement

class HolidayModeRobot : BaseRobot() {
    private val androidToggleElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.id("view_toggle_switch"))
    private val iOSHolidayCellElement: VintedElement get() = IOS.findElementByTranslationKey("holiday_switch_label")

    @Step("Toggle holiday mode")
    fun toggleHolidayMode() {
        if (isAndroid) {
            androidToggleElement.tap()
        } else {
            val x = iOSHolidayCellElement.center.getX() + 160
            val y = iOSHolidayCellElement.center.getY()
            VintedDriver.tap(x, y)
        }
    }
}
