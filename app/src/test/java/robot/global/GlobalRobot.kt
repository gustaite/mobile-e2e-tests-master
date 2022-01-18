package robot.global

import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class GlobalRobot : BaseRobot() {

    private val confirmButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("country_selection_confirm_button"),
            iOSBy = VintedBy.accessibilityId("Confirm")
        )

    private fun sandboxElement(sandboxName: String) = VintedDriver.findElement(androidBy = VintedBy.androidTextByBuilder(text = sandboxName), iOSBy = VintedBy.accessibilityId(sandboxName))

    @Step("Select sandbox and confirm")
    fun selectSandboxAndConfirm() {
        commonUtil.Util.retryUntil(
            block = { selectSandboxConfirmAndCheckScreenChanged() },
            tryForSeconds = 10
        )
    }

    @Step("Preselect sandbox, click confirm, check if confirm button still visible after that")
    private fun selectSandboxConfirmAndCheckScreenChanged(): Boolean {
        sandboxElement(portal.sandboxName!!.value).click()
        confirmButton.click()
        return confirmButton.isInvisible(2)
    }
}
