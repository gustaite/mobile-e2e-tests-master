package util.base

import RobotFactory.deepLink
import api.controllers.user.*
import api.data.models.VintedUser
import commonUtil.testng.Attributes
import commonUtil.testng.BeforeTestBehaviors
import commonUtil.testng.AfterTestBehaviours
import io.qameta.allure.Step
import org.testng.ITestContext
import util.base.BaseTest.Companion.baseUser
import util.base.BaseTest.Companion.businessUser
import util.base.BaseTest.Companion.isInitialSetupCompleted
import util.base.BaseTest.Companion.withItemsUser
import java.lang.reflect.Method

class BaseTestHelper {

    val userCleanup get() = UserCleanupHelper()
    val userCreation get() = UserCreationHelper()
    val logs get() = LogsHelper()
    val recording get() = RecordingHelper()
    val app = AppHelper()

    @Step("Perform reset app before test behaviour")
    fun performResetAppBeforeTestBehaviour(context: ITestContext, method: Method) {
        if (context.getAttribute(method.name + Attributes.RESET_APP) == BeforeTestBehaviors.RESET_APP_BEFORE_TEST) {
            app.restartApp()
        }
    }

    @Step("Perform before test behavior")
    fun performBeforeTestBehavior(context: ITestContext, method: Method) {
        loginToUserWithBeforeTestBehavior(context, method)
    }

    @Step("Perform after test app reset")
    fun performAfterTestAppReset(context: ITestContext, method: Method) {
        if (context.getAttribute(method.name + Attributes.RESET_APP) == AfterTestBehaviours.RESET_APP_AFTER_TEST && isInitialSetupCompleted) {
            app.restartApp()
        }
    }

    @Step("Login to user using before test behavior")
    private fun loginToUserWithBeforeTestBehavior(context: ITestContext, method: Method) {
        /**
         * Context attributes are saved in [MethodInterceptor]
         */
        when (context.getAttribute(method.name)) {
            BeforeTestBehaviors.LOGIN_TO_MAIN_THREAD_USER -> {
                deepLinkLogin(baseUser!!, "current thread")
            }
            BeforeTestBehaviors.LOGIN_TO_DEFAULT_USER -> {
                deepLinkLogin(baseUser!!, "default")
            }
            BeforeTestBehaviors.LOGIN_TO_NEW_USER -> {
                deepLinkLogin(baseUser!!, "newly created")
            }
            BeforeTestBehaviors.LOGIN_TO_WITH_ITEMS_USER -> {
                withItemsUser.skipPartCleanup = true
                deepLinkLogin(baseUser!!, "with items")
            }
            BeforeTestBehaviors.LOGIN_TO_BUSINESS_USER -> {
                businessUser!!.skipPartCleanup = true
                deepLinkLogin(baseUser!!, "business")
            }
            else -> commonUtil.reporting.Report.addMessage("Login was skipped here because test had no user annotation")
        }
    }

    @Step("Deep-link Login")
    private fun deepLinkLogin(user: VintedUser, userType: String) {
        deepLink.loginToAccount(user)
        commonUtil.reporting.Report.addMessage("Logging in to $userType user ${user.username} before test")
    }
}
