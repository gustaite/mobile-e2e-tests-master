package robot.profile.settings

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class UserAccountSettingsRobot {

    val saveSection get() = AccountProfileSaveSectionRobot()

    private val accountSettingsActionBarElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("settings_account"), scroll = false),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && (name == '${IOS.getElementValue("account_settings")}')")
        )

    private val realNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "user_profile_form_name",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeTextField[-1]")
        )

    private val changePasswordButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_profile_form_password_cell"),
            iOSBy = VintedBy.accessibilityId("change_password")
        )

    @Step("Assert account settings screen is visible")
    fun assertAccountSettingsScreenIsVisible() {
        VintedAssert.assertTrue(accountSettingsActionBarElement.isVisible(), "Account settings screen should be visible")
    }

    @Step("Click change password")
    fun clickChangePassword(): ChangePasswordRobot {
        changePasswordButton.withScrollIos().click()
        return ChangePasswordRobot()
    }

    @Step("Enter '{realName}' real name")
    fun enterRealName(realName: String): UserAccountSettingsRobot {
        realNameElement.click().withWait().clear().sendKeys(realName)
        return this
    }

    @Step("Assert real name is {realName}")
    fun assertRealName(realName: String) {
        val actualText = realNameElement.text
        VintedAssert.assertEquals(actualText, realName, "Real name should be $realName but found $actualText")
    }
}
