package robot.profile.settings

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ChangePasswordRobot : BaseRobot() {

    private val currentPasswordElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "curent_password",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("current_password")
        )

    private val newPasswordElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "new_password",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("new_password")
        )

    private val newPasswordAgainElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "new_password_again",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("new_password_again")
        )

    private val saveButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("menu_submit_button"),
            iOSBy = VintedBy.accessibilityId("save_change_password")
        )

    @Step("Change password")
    fun changePassword(currentPassword: String, newPassword: String?) {
        saveButton.withWait()
        currentPasswordElement.sendKeys(currentPassword)
        newPasswordElement.sendKeys(newPassword)
        newPasswordAgainElement.sendKeys(newPassword)
        saveButton.click()
    }
}
