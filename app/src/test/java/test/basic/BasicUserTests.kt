package test.basic

import RobotFactory.deepLink
import RobotFactory.deleteAccountRobot
import RobotFactory.navigationRobot
import RobotFactory.signInRobot
import RobotFactory.welcomeRobot
import RobotFactory.workflowRobot
import api.data.models.VintedUser
import api.factories.UserFactory
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.ResetAppBeforeTest
import commonUtil.testng.mobile.RunMobile
import commonUtil.thread
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import java.lang.reflect.Method

@RunMobile
@Feature("New/current user should be able to sign up/in edit password, logout and delete account")
class BasicUserTests : BaseTest() {
    private var user: VintedUser? by thread.lateinit()

    @BeforeMethod(description = "Reset loggedInUser to defaultUser")
    fun beforeMethod_a_resetLoggedInUser(method: Method) {
        if (method.name != "testUserDeletion") {
            loggedInUser = defaultUser
        }
    }

    @ResetAppBeforeTest
    @Test(description = "Sign in with username")
    fun testSignInWithUsername() {
        val user = mainUser
        welcomeRobot
            .clickSignIn()
            .signInWithEmailOrUsername(user.username, user.password)
        navigationRobot.assertProfileTabIsVisible()
    }

    @ResetAppBeforeTest
    @Test(description = "Sign up by email")
    fun testSignUpWithEmail() {
        user = UserFactory.generateUser()

        welcomeRobot
            .clickSignUp()
            .signUpWithNewEmail(user!!)
            .turnOffPushNotification(user!!)

        navigationRobot.assertProfileTabIsVisible()
        workflowRobot.logoutFromAccount()
        welcomeRobot
            .clickSignIn()
            .signInWithEmailOrUsername(user!!.email, user!!.password)

        navigationRobot.assertProfileTabIsVisible()
    }

    @Test(description = "New user changes password and re-login with new password. Also tries to log in with old password")
    @TmsLink("58")
    fun testUserPasswordChanging() {
        val user = workflowRobot.createNewAccountAndSignIn()

        workflowRobot
            .navigateToAccountSettings()
            .clickChangePassword()
            .changePassword(user.password, UserFactory.NEW_PASSWORD)

        workflowRobot.workflowAfterPasswordIsChanged()

        welcomeRobot
            .clickSignIn()
            .signInWithEmailOrUsername(user.email, user.password)

        signInRobot.assertErrorModalAppears()
        signInRobot.closeModal()

        signInRobot
            .enterPassword(UserFactory.NEW_PASSWORD)
            .clickSignIn()

        navigationRobot.assertProfileTabIsVisible()
    }

    @LoginToNewUser
    @Test(description = "Delete account")
    @TmsLink("59")
    fun testUserDeletion() {
        deepLink.profile.goToDeleteAccount()

        deleteAccountRobot
            .assertTransactionCheckboxIsNotChecked()
            .clickDeleteAccount()
            .assertValidationCount(1)
            .markTransactionCheckboxAsChecked()
            .clickDeleteAccount()

        welcomeRobot
            .clickSignIn()
            .signInWithEmailOrUsername(loggedInUser.email, loggedInUser.password)

        signInRobot.assertErrorModalAppears()
        signInRobot.closeModal()

        signInRobot.assertSignInIsVisible()
    }
}
