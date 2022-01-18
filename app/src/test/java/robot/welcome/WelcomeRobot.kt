package robot.welcome

import RobotFactory
import RobotFactory.cmpCookiesRobot
import RobotFactory.navigationRobot
import RobotFactory.welcomeRobot
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.NavigationRobot
import util.EnvironmentManager.isAndroid
import util.driver.*
import util.IOS
import util.VintedDriver
import util.values.ElementByLanguage.Companion.continueWithEmailText

class WelcomeRobot : BaseRobot() {

    private val signUpOptionsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("show_registration_options_button"),
            iOSBy = VintedBy.accessibilityId("show_registration_options")
        )

    private val continueWithEmailButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.androidText(continueWithEmailText), iOSBy = VintedBy.iOSNsPredicateString("name == '$continueWithEmailText' || name == 'Arba tęsti su el. pašto adresu'"))

    private val loginOptionsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("show_login_options_button"),
            iOSBy = VintedBy.accessibilityId("show_login_options")
        )

    private val skipButtonPredicateString = "name == 'Überspringen' || name == 'Ignorer' || name == 'Ignora' || name == 'Praleisti' ||  name == 'Overslaan' || name == 'Saltar' || name == 'Skip' || name == 'Pomiń' || name == 'Přeskočit' || name == 'Ignorar'"
    private val skipAuthenticationButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString(skipButtonPredicateString))
    private val confirmButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("confirm"))

    @Step("Click sign in")
    fun clickSignIn(): SignInRobot {
        IOS.doIfiOS { sleepWithinStep(2000) } // for a couple of seconds in ios button is not clickable
        loginOptionsButton.click()
        continueWithEmailButton.click()

        return RobotFactory.signInRobot
    }

    @Step("Click sign up")
    fun clickSignUp(): SignUpRobot {
        IOS.doIfiOS { sleepWithinStep(2000) } // for a couple of seconds in ios button is not clickable
        signUpOptionsButton.click()
        continueWithEmailButton.click()

        return RobotFactory.signUpRobot
    }

    @Step("Click skip (only iOS)")
    fun clickSkip(): NavigationRobot {
        IOS.doIfiOS {
            skipAuthenticationButtonIos.click()
            cmpCookiesRobot.acceptCmpCookies()
        }
        return navigationRobot
    }

    @Step("Save preselected language")
    fun savePreselectedLanguage() {
        if (isAndroid) {
            commonUtil.Util.retryUntil(
                block = {
                    closeModal()
                    isModalInvisible()
                },
                tryForSeconds = 5
            )
        } else {
            confirmButtonIos.click()
        }
    }

    @Step("Checking if welcome screen is visible")
    fun assertWelcomeScreenIsVisible(): WelcomeRobot {
        VintedAssert.assertTrue(loginOptionsButton.isVisible(20), "Welcome screen should be visible")
        return welcomeRobot
    }

    @Step("Checking if show login options are visible")
    fun isShowLoginOptionsVisible(waitTime: Long = 5): Boolean {
        return loginOptionsButton.isVisible(waitTime)
    }
}
