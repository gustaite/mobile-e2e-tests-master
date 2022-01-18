package util.testng

import commonUtil.testng.config.ConfigManager.portal
import org.testng.*
import util.driver.WebDriverFactory.driver
import commonUtil.extensions.isInitialized
import commonUtil.reporting.Kibana
import commonUtil.reporting.data.VintedKibanaInfo
import commonUtil.testng.ExecutionLogs
import util.base.BaseTest.Companion.loggedInUser
import util.reporting.AllureReport
import util.reporting.AllureReportTestsWithoutSessionHelper

class InvokedMethodListener : IInvokedMethodListener {
    override fun beforeInvocation(method: IInvokedMethod, testResult: ITestResult) {
        ExecutionLogs.fillExecutionLog(testResult)
        AllureReport.setLinksProperty()
        AllureReport.addTeam(method)
        AllureReportTestsWithoutSessionHelper.addDeviceNameAndUdid(method)
    }

    override fun afterInvocation(method: IInvokedMethod, testResult: ITestResult) {
        if (driver.isInitialized()) {
            // BeforeSuite method does not yet have driver initialized
            AllureReport.addTestParameters(method, testResult)
            AllureReport.addTestsToSpecialFeatures(method)
        }
        Kibana.addUrlsToReport(info = VintedKibanaInfo(user = loggedInUser, portalName = portal.portalName, addAppHealth = true))
    }
}
