package robot.profile.settings

import commonUtil.Util
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.UserProfileEditRobot
import util.EnvironmentManager
import util.VintedDriver
import util.driver.VintedElement

class AccountProfileSaveSectionRobot : BaseRobot() {
    private val submitButton: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("menu_submit_button", "save")

    @Step("Click save button")
    fun clickSave(): UserProfileEditRobot {
        when {
            EnvironmentManager.isAndroid -> submitButton.click()
            else -> {
                submitButton.tap()
                Util.sleepWithinStep(1000) // Not possible to know when it is actually saved
            }
        }
        return UserProfileEditRobot()
    }
}
