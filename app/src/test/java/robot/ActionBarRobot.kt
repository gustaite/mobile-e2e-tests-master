package robot

import io.qameta.allure.Step
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ActionBarRobot : BaseRobot() {

    private val submitButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdMatches(".*submit_button"),
            iOSBy = VintedBy.accessibilityId("done")
        )

    private val submitInParcelSizeScreenButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("packaging_options_submit_button"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'done' || name == 'price_done_button'")
        )

    private val submitInColorScreenButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("submit_button"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'color_picker_done_button' || name == 'done'")
        )

    private val closeBottomSheetButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("navigation_right_action"),
            iOSBy = VintedBy.accessibilityId("close")
        )

    @Step("Click submit button")
    fun submit(seconds: Long = 5) {
        submitButton.withWait(seconds = seconds).click()
    }

    @Step("Click submit button in Parcel Size screen in Upload form")
    fun submitInParcelSizeScreen(seconds: Long = 5) {
        submitInParcelSizeScreenButton.withWait(seconds = seconds).click()
    }

    @Step("Click submit button in Color screen in Upload form")
    fun submitInColorScreen() {
        submitInColorScreenButton.click()
    }

    @Step("Click close in bottom sheet")
    private fun closeBottomSheetComponent() {
        closeBottomSheetButton.click()
    }

    @Step("Click close in bottom sheet if visible")
    fun closeBottomSheetComponentIfVisible() {
        if (closeBottomSheetButton.isVisible(2)) closeBottomSheetComponent()
    }
}
