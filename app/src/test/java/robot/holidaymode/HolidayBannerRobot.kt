package robot.holidaymode

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage

class HolidayBannerRobot : BaseRobot() {
    /*
    Both platforms use same translation keys
     */
    private val holidayBannerTitleElement: VintedElement
        get() = {
            val text = ElementByLanguage.getElementValueByPlatform(key = "holiday_banner_title")
            VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
            )
        }()

    private val holidayBannerSubtitleElement: VintedElement
        get() = {
            val text = ElementByLanguage.getElementValueByPlatform(key = "holiday_banner_subtitle")
            VintedDriver.findElement(
                androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
                iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
            )
        }()

    private val holidayBannerToggleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("holiday_mode_end_button"),
            iOSBy = VintedBy.accessibilityId("toggle_holiday_mode")
        )

    @Step("Assert holiday banner is visible")
    fun assertHolidayBannerIsVisible() {
        VintedAssert.assertTrue(holidayBannerTitleElement.isVisible(), "Holiday banner title should be visible")
        VintedAssert.assertTrue(
            holidayBannerSubtitleElement.isVisible(),
            "Holiday banner subtitle should be visible"
        )
        VintedAssert.assertTrue(holidayBannerToggleElement.isVisible(), "Holiday banner toggle should be visible")
    }
}
