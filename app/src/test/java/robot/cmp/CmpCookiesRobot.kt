package robot.cmp

import RobotFactory.cmpWorkflowRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.thread
import io.qameta.allure.Step
import robot.BaseRobot
import robot.workflow.CmpWorkflowRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.absfeatures.FeatureController
import util.absfeatures.FeatureNames.CMP_INTEGRATION
import util.absfeatures.FeatureNames.OT_CMP_INTEGRATION
import util.assertVisibilityEquals
import util.driver.VintedBy
import util.driver.VintedElement
import java.lang.AssertionError
import java.lang.NullPointerException
import util.values.Visibility

class CmpCookiesRobot : BaseRobot() {
    companion object {
        var cmpAccepted: Boolean by thread(false)
    }

    private val cmpCookiesAcceptButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("accept_all_button"),
            iOSBy = VintedBy.accessibilityId("allow_all")
        )

    private val cmpCookiesAcceptButtonsList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.id("accept_all_button"),
            iOSBy = VintedBy.accessibilityId("allow_all")
        )

    private val cmpSettingsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("manage_cookies_button"),
            iOSBy = VintedBy.accessibilityId("manage_preferences")
        )

    @Step("Accept cmp cookies")
    fun acceptCmpCookies() {
        // Basic user tests fails on Production with this feature check so probably it would be best to remove it after cmp enabled everywhere
        var cmpEnabled = false
        var waitTimeForCmp: Long = 15
        try {
            cmpEnabled = FeatureController.isOn(CMP_INTEGRATION) || FeatureController.isOn(OT_CMP_INTEGRATION)
        } catch (e: AssertionError) {
            waitTimeForCmp = 5
            cmpEnabled =
                true // If it is not possible to get Feature, work as if CMP would be enabled  but lower wait time
            commonUtil.reporting.Report.addMessage("Assertion error was caught. Probably it is basic user test")
        }
        if (!cmpEnabled) return

        if (cmpCookiesAcceptButton.isVisible(waitTimeForCmp)) {
            Android.doIfAndroid { // For Android do not try accept cmp if it was already accepted once in same thread
                if (!cmpAccepted) {
                    handleMultipleAcceptCmpButtons()
                }
            }
            IOS.doIfiOS { handleMultipleAcceptCmpButtons() }
        }
    }

    @Step("Handle clicking accept button")
    private fun handleMultipleAcceptCmpButtons() {
        var acceptButtonsCount = 1
        sleepWithinStep(300)
        if (VintedElement.isListVisible({ cmpCookiesAcceptButtonsList }, waitSec = 2)) {
            acceptButtonsCount = cmpCookiesAcceptButtonsList.count()
        }

        if (acceptButtonsCount > 1) {
            retryClickAcceptButtonMultipleButtons(acceptButtonsCount)
        } else {
            retryClickAcceptButtonOneButton()
        }
    }

    @Step("Click cmp accept button when expecting one accept button")
    private fun retryClickAcceptButtonOneButton() {
        commonUtil.Util.retryAction(
            block = {
                !cmpCookiesAcceptButton.isVisible(2)
            },
            actions = {
                try {
                    cmpCookiesAcceptButton.click()
                } catch (e: NullPointerException) {
                    commonUtil.reporting.Report.addMessage("Exception caught while trying to click Accept button: ${e.message}")
                }
            },
            retryCount = 2
        )
    }

    @Step("Click cmp accept button when expecting more than one accept button")
    private fun retryClickAcceptButtonMultipleButtons(acceptButtonsCount: Int) {
        commonUtil.reporting.Report.addMessage("There was $acceptButtonsCount 'Accept All' buttons in the beginning")
        commonUtil.Util.retryAction(
            block = {
                if (VintedElement.isListVisible({ cmpCookiesAcceptButtonsList }, waitSec = 1)) {
                    cmpCookiesAcceptButtonsList.count()
                        .let { commonUtil.reporting.Report.addMessage("There was $it 'Accept All' buttons in the execution"); it < acceptButtonsCount }
                } else {
                    commonUtil.reporting.Report.addMessage("There was 0 'Accept All' buttons in the execution")
                    true
                }
            },
            actions = { cmpCookiesAcceptButton.click() }, retryCount = 2
        )
        cmpAccepted = true
    }

    @Step("Assert cmp accept button is {visibility}")
    fun assertCmpSettingsButtonVisibility(visibility: Visibility): CmpCookiesRobot {
        VintedAssert.assertVisibilityEquals(cmpSettingsButton, visibility, "CMP settings button should be $visibility", waitForVisible = 10)
        return this
    }

    @Step("Open cmp cookies settings")
    fun openCmpCookiesSettings(): CmpWorkflowRobot {
        cmpSettingsButton.click()
        return cmpWorkflowRobot
    }
}
