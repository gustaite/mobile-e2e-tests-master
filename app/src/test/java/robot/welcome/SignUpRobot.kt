package robot.welcome

import RobotFactory.cmpCookiesRobot
import api.data.models.VintedUser
import api.controllers.AuthAPI
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.Session.Companion.sessionDetails
import api.controllers.absfeatures.VintedAbTest
import api.controllers.user.notificationSettingsApi
import api.controllers.user.userApi
import commonUtil.asserts.VintedAssert
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.data.enums.VintedNotificationSettingsTypes

class SignUpRobot : BaseRobot() {

    private val fullNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "email_register_real_name",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("full_name")
        )

    private val usernameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "email_register_username",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("username")
        )

    private val emailElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "email_register_email",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("email")
        )

    private val passwordElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "email_register_password",
                Android.INPUT_FIELD_ID
            ),
            iOSBy = VintedBy.accessibilityId("password")
        )

    private val signUpButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("email_register_sign_up"),
            iOSBy = VintedBy.accessibilityId("sign_up_button")
        )

    private val primaryIntentElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("sign_up_selling_intent_title"), scroll = true),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("sign_up_selling_intent_title"))
        )

    @Step("Sign up with: username - {user.username}, email - {user.email}, password - {user.password}")
    fun signUpWithNewEmail(user: VintedUser): SignUpRobot {
        VintedAssert.assertTrue(passwordElement.isVisible(), "PasswordElement should be visible")
        VintedAbTest.run {
            if (usernameElement.isVisible(1)) {
                clickAndSendKeys(usernameElement, user.username)
            }
        }

        enterFullName(user.realName!!)
        clickAndSendKeys(emailElement, user.email)
        workAroundXiaomiSuggestion()
        clickAndSendKeys(passwordElement, user.password)
        Android.closeKeyboard()
        IOS.hideKeyboard()
        if (primaryIntentElement.isVisible()) selectPrimaryIntent()
        IOS.pressDoneInKeyboard()
        signUpButton.click()
        cmpCookiesRobot.acceptCmpCookies()

        return this
    }

    @Step("Workaround Xiaomi 'Frequently used email' suggestion")
    private fun workAroundXiaomiSuggestion() {
        if (sessionDetails.deviceManufacturer == "Xiaomi" && usernameElement.isVisible(1)) {
            usernameElement.click()
        }
    }

    @Step("Enter full name {fullName}")
    fun enterFullName(fullName: String) {
        fullNameElement.sendKeys(fullName)
    }

    @Step("Select primary intent")
    fun selectPrimaryIntent() {
        primaryIntentElement.click()
    }

    @Step("Turn off push notification")
    fun turnOffPushNotification(user: VintedUser) {
        user.token = AuthAPI.getToken(user.username, user.password, userApiFactory = user.userApiFactory)
        user.id = user.userApi.getInfo().id
        user.notificationSettingsApi.disableNotifications(VintedNotificationSettingsTypes.PUSH)
    }

    private fun clickAndSendKeys(element: VintedElement, text: String) {
        Android.doIfAndroid {
            element.click()
        }
        element.sendKeys(text)
    }
}
