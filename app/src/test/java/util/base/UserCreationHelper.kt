package util.base

import api.controllers.user.notificationSettingsApi
import api.controllers.user.userApi
import api.factories.UserFactory
import commonUtil.data.enums.VintedNotificationSettingsTypes
import commonUtil.extensions.isInitialized
import commonUtil.testng.Attributes
import commonUtil.testng.BeforeTestBehaviors
import io.qameta.allure.Step
import org.testng.ITestContext
import util.base.BaseTest.Companion.flexibleAddressUser
import util.base.BaseTest.Companion.mainUser
import util.base.BaseTest.Companion.oneTestUser
import util.base.BaseTest.Companion.otherUser
import java.lang.reflect.Method

class UserCreationHelper {
    @Step("Create main and other users")
    fun createMainAndOtherUsers() {
        createMainUser()
        createOtherUser()
    }

    @Step("Create main user")
    private fun createMainUser() {
        if (mainUser.isInitialized()) {
            commonUtil.reporting.Report.addMessage("Reused already created ${mainUser.username} main user")
        } else {
            mainUser = UserFactory.createRandomUser().also {
                commonUtil.reporting.Report.addMessage("Created thread main user: ${it.username}")
                it.notificationSettingsApi
                    .disableNotifications(VintedNotificationSettingsTypes.PUSH)
                    .userApi.markAsReadAllNotifications()
            }
        }
    }

    @Step("Create other user")
    private fun createOtherUser() {
        if (otherUser.isInitialized()) {
            commonUtil.reporting.Report.addMessage("Reused already created ${otherUser.username} other user")
        } else {
            otherUser =
                UserFactory.createRandomUser()
                    .also { commonUtil.reporting.Report.addMessage("Created thread other user: ${it.username}") }
        }
    }

    @Step("Create users by behavior before test")
    fun createUsersByBehaviorBeforeTest(context: ITestContext, method: Method) {
        if (context.getAttribute(method.name + Attributes.FLEXIBLE_ADDRESS_USER) == BeforeTestBehaviors.CREATE_FLEXIBLE_ADDRESS_USER) {
            flexibleAddressUser = UserFactory.createRandomUser()
                .also { commonUtil.reporting.Report.addMessage("Created flexibleAddressUser user: ${it.username}") }
        }
        if (context.getAttribute(method.name + Attributes.ONE_TEST_USER) == BeforeTestBehaviors.CREATE_ONE_TEST_USER) {
            oneTestUser = UserFactory.createRandomUser()
                .also { commonUtil.reporting.Report.addMessage("Created oneTestUser user: ${it.username}") }
        }
    }
}
