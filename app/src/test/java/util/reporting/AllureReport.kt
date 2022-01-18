package util.reporting

import commonUtil.extensions.capitalized
import commonUtil.testng.config.ConfigManager.platform
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Allure
import io.qameta.allure.Attachment
import org.testng.IInvokedMethod
import org.testng.ITestResult
import util.base.BaseTest.Companion.loggedInUser
import util.Session.Companion.sessionDetails
import util.app.App.appVersion
import commonUtil.extensions.isInitialized
import commonUtil.reporting.Report
import commonUtil.reporting.data.VintedReportEnvironment
import util.image.Screenshot
import commonUtil.testng.RetryAnalyzer.Companion.getRetryStatus
import commonUtil.testng.RetryAnalyzer.VintedRetryStatus
import util.values.Devices
import util.values.StevesMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AllureReport {
    companion object {
        const val testRailUrl = "https://vintedqa.testrail.io/index.php?/cases/view/"
        const val issueUrl = "https://vinted.atlassian.net/browse/"

        fun setEnvironment() {
            Report.setEnvironment(reportEnv = VintedReportEnvironment(platform = platform, portal = portal, appVersion = appVersion))
        }

        fun setLinksProperty() {
            System.setProperty("allure.link.tms.pattern", "$testRailUrl{}")
            System.setProperty("allure.link.issue.pattern", "$issueUrl{}")
            System.setProperty("allure.link.github.pattern", "https://github.com/vinted/{}")
        }

        @Attachment("screenshot", type = "image/png")
        fun addScreenshot(): ByteArray? {
            return Screenshot.takeScreenshot().screenshot
        }

        @Attachment("Logcat logs", type = "text/plain")
        fun saveLogcatLogs(): String {
            return Logs.getLogcatLogs()
        }

        @Attachment("iOS logs", type = "text/plain")
        fun saveIosLogs(testResult: ITestResult): ByteArray {
            return Logs.stopCollectingiOSLogs(testResult)
        }

        @Attachment("Video", type = "video/mp4")
        fun stopAndAddRecording(): ByteArray? {
            return ScreenRecording().stopRecording(saveRecording = true)
        }

        fun addTestsToSpecialFeatures(method: IInvokedMethod) {
            runCatching {
                if (getRetryStatus(method) == VintedRetryStatus.RETRIED) Allure.feature("Retried tests (for automation team)")
                Allure.feature("By device: $deviceName")
                Allure.feature("By node: ${StevesMap.getSteve(sessionDetails.node)?.name ?: sessionDetails.node}")
            }
        }

        fun addTeam(method: IInvokedMethod) {
            runCatching {
                if (method.isTestMethod) {
                    val team = ResponsibleTeam.getTeam(method.testMethod)
                    Allure.feature("Team: $team")
                    Allure.parameter("0. Team", team)
                }
            }
        }

        fun addTestParameters(method: IInvokedMethod, testResult: ITestResult) {
            val parameters = LinkedHashMap<String, String>()
            var parameterIndex = 1

            runCatching {
                val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")).toString()
                val retryStatus = getRetryStatus(method)

                when {
                    retryStatus == VintedRetryStatus.RETRIED -> {
                        parameters["Retry"] = retryStatus.name.lowercase().capitalized()
                    }
                    retryStatus == VintedRetryStatus.SKIPPED && !testResult.isSuccess -> {
                        parameters["Retry"] = retryStatus.name.lowercase().capitalized()
                    }
                }
                parameters["Device (OS)"] = "$deviceName (${sessionDetails.platformVersion})"
                parameters["App"] = "$portal $appVersion"
                if (sessionDetails.iosAppFileDate != null) {
                    parameters["App build date"] = sessionDetails.iosAppFileDate!!
                }
                parameters["Start time"] = startTime
                parameters["UDID"] = sessionDetails.deviceUdid
                if (loggedInUser.isInitialized()) {
                    parameters["User (ID)"] = "${loggedInUser.username} (${loggedInUser.id})"
                }
                val steve = StevesMap.getSteve(sessionDetails.node)
                parameters["Grid Node"] = "${steve?.name ?: "Sorry n/a"} (${sessionDetails.node}:${sessionDetails.nodePort})"
                parameters["ZeroTier"] = steve?.zeroTier ?: "N/A"
                parameters["Session Id"] = sessionDetails.id

                parameters.forEach {
                    Allure.parameter("$parameterIndex. ${it.key}", it.value)
                    parameterIndex++
                }
            }
        }

        private val deviceName: String
            get() {
                return Devices.getName(sessionDetails.deviceUdid)
            }
    }
}
