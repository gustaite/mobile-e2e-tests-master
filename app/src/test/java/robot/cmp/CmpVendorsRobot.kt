package robot.cmp

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.workflow.CmpWorkflowRobot
import util.Android
import util.EnvironmentManager.isAndroid
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ToggleValue

class CmpVendorsRobot : BaseRobot() {

    private val toggleElementListAndroid: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("view_toggle_switch")
        )

    private val allowAllToggleElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId("allow_all_toggle"),
        )

    private val firstVendorToggleElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId("1_toggle")
        )

    private val allowAllToggleElement: VintedElement get() = if (isAndroid) toggleElementListAndroid.first() else allowAllToggleElementIos

    private val firstVendorToggleElement: VintedElement get() = if (isAndroid) toggleElementListAndroid[1] else firstVendorToggleElementIos

    @Step("Click allow all toggle")
    fun clickAllowAllToggle(): CmpWorkflowRobot {
        allowAllToggleElement.click()
        return CmpWorkflowRobot()
    }

    @Step("Click first vendor toggle")
    fun clickFirstVendorToggle(): CmpVendorsRobot {
        firstVendorToggleElement.click()
        return this
    }

    @Step("Assert cmp vendors toggle value is {toggleValue}")
    private fun assertCmpVendorsToggleValue(toggleValue: ToggleValue, ToggleElement: VintedElement) {

        Android.doIfAndroid {
            VintedAssert.assertEquals(
                ToggleElement.isElementChecked(),
                toggleValue.value,
                "Toggle value had to be $toggleValue"
            )
        }

        IOS.doIfiOS {
            VintedAssert.assertEquals(
                ToggleElement.getValueAttribute(),
                toggleValue.name,
                "Toggle value had to be $toggleValue"
            )
        }
    }

    @Step("Assert allow all cmp vendors toggle value is {toggleValue}")
    fun assertAllowALLToggleValue(toggleValue: ToggleValue): CmpVendorsRobot {
        assertCmpVendorsToggleValue(toggleValue, allowAllToggleElement)
        return this
    }

    @Step("Assert first cmp vendors toggle value is {toggleValue}")
    fun assertFirstCmpVendorsToggleValue(toggleValue: ToggleValue): CmpVendorsRobot {
        assertCmpVendorsToggleValue(toggleValue, firstVendorToggleElement)
        return this
    }
}
