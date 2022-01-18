package robot.workflow

import RobotFactory.cmpCookiesSettingsRobot
import RobotFactory.cmpVendorsRobot
import RobotFactory.cmpWorkflowRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.cmp.CmpCookiesSettingsRobot
import robot.cmp.CmpVendorsRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.data.NavigationDataProviders.Companion.cmpVendorsListPageTitle
import util.values.ToggleValue

class CmpWorkflowRobot : BaseRobot() {

    @Step(
        "Scroll down to performance (Android) or functional (temp iOS) cookie " +
            "and assert performance cookie title is visible"
    )
    fun scrollDownToPerformanceCookieWithValueAndAssertTitleIsVisible(toggleValue: ToggleValue): CmpCookiesSettingsRobot {
        Android.doIfAndroid {
            scrollDownToPerformanceToggleTitle()
        }
        IOS.doIfiOS {
            scrollDownToNeededToggle(toggleValue)
        }
        cmpCookiesSettingsRobot.assertPerformanceAndroidOrFunctionalIosCookieTitleIsVisible()
        return cmpCookiesSettingsRobot
    }

    @Step("Assert that at least one toggle is of value {toggleValue}")
    fun assertAtLeastOneToggleValue(toggleValue: ToggleValue): CmpCookiesSettingsRobot {
        cmpCookiesSettingsRobot
            .scrollDownToCookieBasedOnToggleValueAndroid(toggleValue)
            .assertPerformanceCookieIsVisibleIos()
            .checkIfToggleValueMatchesValue(toggleValue)
        return cmpCookiesSettingsRobot
    }

    @Step("iOS only: Scroll down few times so that toggle is visible")
    fun scrollDownFewTimesSoToggleIsVisibleIos(): CmpWorkflowRobot {
        IOS.doIfiOS {
            VintedDriver.scrollDownABit()
            IOS.scrollDown()
            VintedDriver.scrollDownABit()
        }
        return this
    }

    @Step("Scroll down to needed toggle")
    private fun scrollDownToNeededToggle(toggleValue: ToggleValue): CmpWorkflowRobot {
        cmpCookiesSettingsRobot.scrollDownToCookieBasedOnToggleValueAndroid(toggleValue)
        cmpWorkflowRobot.scrollDownFewTimesSoToggleIsVisibleIos()
        return this
    }

    @Step("Scroll down to performance toggle title")
    private fun scrollDownToPerformanceToggleTitle(): CmpWorkflowRobot {
        Android.doIfAndroid {
            cmpCookiesSettingsRobot.scrollDownToPerformanceToggleTitleAndroid()
        }
        return this
    }

    @Step("Click on vendors link and assert vendors list screen is opened")
    private fun clickOnVendorsLinkAndAssertVendorsListScreenOpened(): CmpWorkflowRobot {
        cmpCookiesSettingsRobot
            .clickVendorsLinkIos()
            .assertNavigationBarNameText(cmpVendorsListPageTitle)
        return this
    }

    @Step("Assert allow all toggle is {allowAllValue} and first vendor toggle is {firsToggleValue}")
    fun assertAllowAllAndFirstVendorToggleValue(allowAllValue: ToggleValue, firsToggleValue: ToggleValue): CmpVendorsRobot {
        cmpVendorsRobot
            .assertAllowALLToggleValue(allowAllValue)
            .assertFirstCmpVendorsToggleValue(firsToggleValue)
        return cmpVendorsRobot
    }

    @Step("Open vendors link and assert allow all toggle is {allowAllValue} and first vendor toggle is {firsToggleValue}")
    fun openVendorsLinkAndAssertAllowAllAndFirstVendorToggleValue(allowAllValue: ToggleValue, firsToggleValue: ToggleValue): CmpWorkflowRobot {
        clickOnVendorsLinkAndAssertVendorsListScreenOpened()
            .assertAllowAllAndFirstVendorToggleValue(allowAllValue, firsToggleValue)
        return this
    }

    @Step("Click allow all toggle and check that allow all toggle is {allowAllValue} and first vendor toggle is {firsToggleValue}")
    fun clickAllowAllToggleAndAssertToggleValues(allowAllValue: ToggleValue, firsToggleValue: ToggleValue): CmpVendorsRobot {
        cmpVendorsRobot
            .clickAllowAllToggle()
            .assertAllowAllAndFirstVendorToggleValue(allowAllValue, firsToggleValue)
        return cmpVendorsRobot
    }

    @Step("Click first vendor toggle and assert toggle is {firstToggleValue}")
    fun clickFirstVendorToggleAndAssertToggleValue(firstToggleValue: ToggleValue): CmpVendorsRobot {
        cmpVendorsRobot
            .clickFirstVendorToggle()
            .assertFirstCmpVendorsToggleValue(firstToggleValue)
        return cmpVendorsRobot
    }

    @Step("Click confirm my choices and open privacy screen from profile tab")
    fun confirmMyChoicesAndOpenPrivacyScreenFromProfileTab(): CmpWorkflowRobot {
        cmpCookiesSettingsRobot
            .clickOnConfirmMyChoices()
            .openPrivacyScreenFromProfileTab()
        return this
    }
}
