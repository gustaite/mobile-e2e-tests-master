package util.reporting

import io.qameta.allure.listener.TestLifecycleListener
import io.qameta.allure.model.StatusDetails
import io.qameta.allure.model.TestResult
import org.testng.SkipException
import util.base.BaseTest.Companion.skippedTests

class TestLifecycleLogger : TestLifecycleListener {

    // In order lifecycle listener to work these steps need to be done:
    // 1. Implement TestLifecycleListener interface and override required methods
    // 2. Create META-INF/services folders in project's resources root
    // 3. Create new file by the full name of this interface in the above folder
    // 4. Add the full path to your implementation class into this file

    override fun beforeTestStop(result: TestResult?) {
        val fullName = result?.fullName
        val skipMessage = skippedTests.getOrDefault(fullName, SkipException(""))
        if (!skipMessage.message.isNullOrEmpty()) {
            // Set status details if test found in skipped tests list
            val statusDetails = StatusDetails()
            statusDetails.message = skipMessage.message
            statusDetails.trace = skipMessage.stackTraceToString()
            result?.statusDetails = statusDetails
        }
    }
}
