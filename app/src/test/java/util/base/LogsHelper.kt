package util.base

import api.controllers.getAbTests
import commonUtil.extensions.isInitialized
import io.qameta.allure.Step
import util.base.BaseTest.Companion.loggedInUser
import util.reporting.Logs

class LogsHelper {
    @Step("User info")
    fun addUserInfo() {
        if (loggedInUser.isInitialized()) {
            try {
                commonUtil.reporting.Report.addMessage("Logged in user's ${loggedInUser.username} ab tests: ${loggedInUser.getAbTests()}")
            } catch (e: AssertionError) {
                val message =
                    "Exception was caught while trying get user AB tests. Probably we don`t need to have AB tests values for BasicUserTests"
                commonUtil.reporting.Report.addMessage("Logged in user's ${loggedInUser.username} \n$message")
            }
        }
    }

    @Step("Get logs")
    fun getLogs() {
        Logs.getLogcatLogs()
    }
}
