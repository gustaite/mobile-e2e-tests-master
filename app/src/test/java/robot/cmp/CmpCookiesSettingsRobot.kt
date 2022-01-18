package robot.cmp

import RobotFactory.cmpCookiesSettingsRobot
import RobotFactory.cmpWorkflowRobot
import RobotFactory.navigationRobot
import RobotFactory.navigationWorkflowRobot
import RobotFactory.workflowRobot
import commonUtil.Util
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.NavigationRobot
import robot.workflow.CmpWorkflowRobot
import robot.workflow.NavigationWorkflowRobot
import robot.workflow.WorkflowRobot
import util.Android
import util.Android.Companion.CELL_TITLE_FIELD_ID
import util.Android.Companion.scrollUntilVisibleAndroid
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.deviceCharacteristicsCmpToggleText
import util.values.ElementByLanguage.Companion.functionalCmpToggleText
import util.values.ElementByLanguage.Companion.performanceCmpToggleText
import util.values.FunctionalCookieIos
import util.values.ScrollDirection
import util.values.ToggleName
import util.values.ToggleValue

class CmpCookiesSettingsRobot : BaseRobot() {

    private val cmpCookieToggleElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.scrollableId("view_toggle_switch"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '_toggle'")
        )

    private val cmpCookieToggleElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_toggle_switch")
        )

    private fun cmpCookieToggleBasedOnValueElementAndroid(toggleValue: ToggleValue): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithIsCheckedFlag("view_toggle_switch", toggleValue.value)
        )

    private fun cmpCookieToggleElementIos(number: Int): VintedElement = VintedDriver.findElement(
        iOSBy = VintedBy.accessibilityId("${number}_toggle")
    )

    private val deviceCharacteristicsToggleElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(deviceCharacteristicsCmpToggleText, scroll = false)
        )

    private val cmpAllowAllButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("allow_all_btn"),
            iOSBy = VintedBy.accessibilityId("allow_all_button")
        )

    private val confirmMyChoicesButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("confirm_choices_btn"),
            iOSBy = VintedBy.accessibilityId("confirm_button")
        )

    private val performanceToggleTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(CELL_TITLE_FIELD_ID, performanceCmpToggleText),
            iOSBy = VintedBy.accessibilityId(performanceCmpToggleText)
        )

    private val functionalToggleTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(CELL_TITLE_FIELD_ID, functionalCmpToggleText),
            iOSBy = VintedBy.iOSTextByBuilder(functionalCmpToggleText)
        )

    private val vendorsLinkElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '_vendors_link'")
        )

    @Step("Check if all cmp cookie toggle values are {toggleValue}")
    fun assertAllCmpToggleValues(toggleValue: ToggleValue): CmpWorkflowRobot {
        repeat(2) { IOS.scrollDown() }
        cmpCookieToggleElementAndroid.scrollUntilVisibleAndroid(ScrollDirection.DOWN)

        assertTogglesCountIos()
        assertCmpCookiesToggleListIsNotEmpty()
        assertCmpToggleValues(toggleValue)
        assertLastCmpToggleValueAndroid(toggleValue)
        return cmpWorkflowRobot
    }

    @Step("iOS only: Assert toggles count is 17")
    private fun assertTogglesCountIos(): WorkflowRobot {
        IOS.doIfiOS {
            val cmpCookiesCountIos = cmpCookieToggleElementList.count()
            VintedAssert.assertTrue(
                cmpCookiesCountIos == 17,
                "Expected 17 cookies but was $cmpCookiesCountIos"
            )
        }
        return workflowRobot
    }

    @Step("Assert cmp toggle values are {toggleValue}")
    private fun assertCmpToggleValues(toggleValue: ToggleValue) {
        val softAssert = VintedSoftAssert()

        Android.doIfAndroid {
            var count = 0
            while (deviceCharacteristicsToggleElementAndroid.isInvisible(1) && count < 14) {
                cmpCookieToggleElementList.forEach { toggle ->
                    softAssert.assertEquals(
                        toggle.isElementChecked(),
                        toggleValue.value,
                        "Toggle value expected to be $toggleValue"
                    )
                    commonUtil.reporting.Report.addMessage("Toggle state of checked is  $toggle - ${toggle.isElementChecked()}")
                }

                Android.scrollDownABit()
                count++

                commonUtil.reporting.Report.addMessage("COUNT - $count")
            }
        }

        IOS.doIfiOS {
            cmpCookieToggleElementList.forEach { toggle ->
                softAssert.assertEquals(
                    toggle.getValueAttribute(),
                    toggleValue.name,
                    "Toggle value had to be ${toggleValue.name}"
                )
            }
        }
        softAssert.assertAll()
    }

    @Step("Assert cmp cookies toggle list is not empty")
    private fun assertCmpCookiesToggleListIsNotEmpty() {
        VintedAssert.assertTrue(cmpCookieToggleElementList.isNotEmpty(), "Cmp toggles element list should not be empty")
    }

    @Step("Android only: Assert last cmp toggle value")
    private fun assertLastCmpToggleValueAndroid(toggleValue: ToggleValue) {
        repeat(3) { Android.scrollDown() }
        Android.doIfAndroid {
            if (!cmpCookieToggleElementAndroid.isVisible(1)) cmpCookieToggleElementAndroid.withScrollDownUntilElementIsInTopThirdOfScreen().also { commonUtil.reporting.Report.addMessage("CMP toggle was  not visible in the screen so scrolled up") }
            VintedAssert.assertEquals(
                cmpCookieToggleElementAndroid.isElementChecked(),
                toggleValue.value,
                "Toggle value had to be $toggleValue"
            )
            commonUtil.reporting.Report.addMessage("Toggle value was ${cmpCookieToggleElementAndroid.text}")
        }
    }

    @Step("Click on toggle {toggleName} with value {initialToggleValue} and assert new value is {expectedToggleValue}")
    fun clickOnToggleAndAssertValue(toggleName: ToggleName, initialToggleValue: ToggleValue, expectedToggleValue: ToggleValue): CmpWorkflowRobot {
        clickOnToggle(toggleName, toggleValue = initialToggleValue)
        assertOneCmpToggleValue(toggleName, toggleValue = expectedToggleValue)
        return cmpWorkflowRobot
    }

    @Step("Click on toggle {toggleName}")
    fun clickOnToggle(toggleName: ToggleName, toggleValue: ToggleValue): WorkflowRobot {
        Android.doIfAndroid {
            scrollDownToCookieBasedOnToggleValueAndroid(toggleValue = toggleValue)
            cmpCookieToggleElementAndroid.click()
        }
        IOS.doIfiOS {
            cmpCookieToggleElementIos(toggleName.value).withScrollIos().click()
        }
        return workflowRobot
    }

    @Step("Android only: Scroll down to cookie which toggle value is {toggleValue}")
    fun scrollDownToCookieBasedOnToggleValueAndroid(toggleValue: ToggleValue): CmpCookiesSettingsRobot {
        Android.doIfAndroid {
            Util.retryAction(
                { cmpCookieToggleBasedOnValueElementAndroid(toggleValue).isVisible(1) },
                { VintedDriver.scrollDownABit(beginY = 0.6, endY = 0.4) },
                10
            )
        }
        return this
    }

    @Step("Android only: Scroll down to performance cookie title")
    fun scrollDownToPerformanceToggleTitleAndroid(): CmpCookiesSettingsRobot {
        Android.doIfAndroid {
            Util.retryAction(
                { performanceToggleTitleElement.isVisible(1) },
                { VintedDriver.scrollDownABit(beginY = 0.6, endY = 0.4) },
                10
            )
        }
        return this
    }

    @Step("iOS only: Assert performance cookie is visible")
    fun assertPerformanceCookieIsVisibleIos(): CmpCookiesSettingsRobot {
        IOS.doIfiOS {
            // TODO to change to performance title when bug ADS-1260 is fixed
            if (functionalToggleTitleElement.isInvisible(1)) {
                VintedDriver.scrollUpABit()
            }
            VintedAssert.assertTrue(
                functionalToggleTitleElement.withScrollIos().isVisible(1),
                "Functional toggle title element should be visible"
            )
        }
        return this
    }

    @Step("Assert performance (Android) or functional (temp iOS) cookie title is visible")
    fun assertPerformanceAndroidOrFunctionalIosCookieTitleIsVisible(): CmpCookiesSettingsRobot {
        Android.doIfAndroid { // TODO temp change - ios has a bug
            VintedAssert.assertTrue(
                performanceToggleTitleElement.withScrollIos().isVisible(1),
                "Performance cookie title should be visible"
            )
        }
        IOS.doIfiOS {
            // TODO to change to performance title when bug ADS-1260 is fixed
            if (functionalToggleTitleElement.isInvisible(1)) {
                VintedDriver.scrollUpABit()
            }
            VintedAssert.assertTrue(
                functionalToggleTitleElement.withScrollIos().isVisible(1),
                "Functional toggle title element should be visible"
            )
        }
        return cmpCookiesSettingsRobot
    }

    @Step("Assert one cmp toggle value is {toggleValue}")
    fun assertOneCmpToggleValue(toggleName: ToggleName, toggleValue: ToggleValue): CmpWorkflowRobot {
        Android.doIfAndroid {
            scrollDownToCookieBasedOnToggleValueAndroid(toggleValue = toggleValue)
            VintedAssert.assertEquals(
                cmpCookieToggleElementAndroid.isElementChecked(), toggleValue.value, "Toggle value had to be $toggleValue"
            )
        }

        IOS.doIfiOS {
            VintedAssert.assertEquals(
                cmpCookieToggleElementIos(toggleName.value).withScrollIos().getValueAttribute(),
                toggleValue.name,
                "Toggle value had to be ${toggleValue.name}"
            )
        }
        return cmpWorkflowRobot
    }

    @Step("Allow all cmp cookies")
    fun allowAllCmpCookies(): WorkflowRobot {
        VintedAssert.assertTrue(cmpAllowAllButtonElement.isVisible(), "Cmp allow all button should be visible")
        cmpAllowAllButtonElement.click()
        return workflowRobot
    }

    @Step("Click on confirm my choices")
    fun clickOnConfirmMyChoices(): NavigationWorkflowRobot {
        confirmMyChoicesButtonElement.click()
        return navigationWorkflowRobot
    }

    // TODO : apply for Android when links ids ADS-1458 are implemented
    @Step("iOS only: Click on vendors link")
    fun clickVendorsLinkIos(): NavigationRobot {
        IOS.doIfiOS {
            repeat(2) { IOS.scrollDown() }
            vendorsLinkElementIos.withScrollIos().click()
        }
        return navigationRobot
    }

    @Step("Check if toggle value matches {toggleValue}")
    fun checkIfToggleValueMatchesValue(toggleValue: ToggleValue): CmpCookiesSettingsRobot {
        Android.doIfAndroid {
            VintedAssert.assertEquals(
                cmpCookieToggleBasedOnValueElementAndroid(toggleValue).isElementChecked(),
                toggleValue.value,
                "Toggle with value $toggleValue had to be visible"
            )
        }
        IOS.doIfiOS {
            if (cmpCookieToggleElementIos(FunctionalCookieIos.FUNCTIONAL_COOKIE.value).withScrollIos().isInvisible(1)) {
                VintedDriver.scrollDownABit()
            }
            VintedAssert.assertEquals(
                cmpCookieToggleElementIos(FunctionalCookieIos.FUNCTIONAL_COOKIE.value).getValueAttribute(),
                toggleValue.name,
                "Toggle of value $toggleValue had to be visible"
            )
        }
        return cmpCookiesSettingsRobot
    }
}
