package robot.welcome

import RobotFactory.cmpCookiesRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class SignInRobot : BaseRobot() {

    private val emailElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("login_username_input"),
            iOSBy = VintedBy.accessibilityId("username_or_email")
        )

    private val passwordElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("login_password_input"),
            iOSBy = VintedBy.accessibilityId("password")
        )

    private val loginButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("login_login"),
            iOSBy = VintedBy.accessibilityId("sign_in_button")
        )

    @Step("Sign in with: email {emailOrUsername} / password {password}")
    fun signInWithEmailOrUsername(emailOrUsername: String, password: String) {
        emailElement.sendKeys(emailOrUsername)
        enterPassword(password)
        clickSignIn()
    }

    @Step("Enter password {password}")
    fun enterPassword(password: String): SignInRobot {
        passwordElement.clear()
        passwordElement.sendKeys(password)
        return this
    }

    @Step("Click sign in")
    fun clickSignIn() {
        loginButton.click()
        cmpCookiesRobot.acceptCmpCookies()
    }

    @Step("Assert sign in is visible")
    fun assertSignInIsVisible() {
        VintedAssert.assertTrue(loginButton.isVisible(), "Sign in button should be visible")
    }
}
