package util.reporting

import RobotFactory.deepLink
import commonUtil.Util.Companion.sleepWithinStep
import org.testng.ITestResult
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isiOS
import util.Session
import util.Util
import util.driver.WebDriverFactory.driver
import commonUtil.extensions.isInitialized
import java.time.LocalDateTime

class Logs {
    companion object {
        fun getLogcatLogs(): String {
            return if (isAndroid) {
                try {
                    val list = driver.manage().logs().get("logcat").all.toList()
                    val result = StringBuilder()
                    for (log in list) {
                        result.append(log.toString())
                        result.append("\n")
                    }
                    result.toString()
                } catch (e: Exception) {
                    commonUtil.reporting.Report.addMessage("Failed to capture logcat logs")
                    ""
                }
            } else ""
        }

        fun startCollectingiOSLogs(testResult: ITestResult) {
            if (!driver.isInitialized() || isAndroid) return

            runCatching {
                deepLink.startLogsCollection(testResult.method.methodName)
            }
        }

        fun stopCollectingiOSLogs(testResult: ITestResult): ByteArray {
            var logs: ByteArray = byteArrayOf()

            if (driver.isInitialized() && isiOS) {
                runCatching {
                    val time = LocalDateTime.now()
                    val uuid = Session.sessionDetails.deviceUdid
                    val fileName = "${testResult.method.methodName}-$time.log"
                    val logPath = "${System.getProperty("user.home")}/Library/Developer/CoreSimulator/Devices/$uuid/data/tmp/$fileName"
                    deepLink.stopLogsCollection(logPath)

                    Util.retryOnException(
                        block = {
                            sleepWithinStep(25)
                            logs = driver.pullFile("/tmp/$fileName")
                        },
                        count = 20
                    )
                }
            }

            return logs
        }
    }
}
