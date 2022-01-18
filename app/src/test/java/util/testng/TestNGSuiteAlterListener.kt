package util.testng

import commonUtil.testng.config.ConfigManager
import commonUtil.testng.config.ConfigManager.platform
import commonUtil.testng.config.ConfigManager.portal
import org.testng.IAlterSuiteListener
import org.testng.xml.XmlSuite
import util.EnvironmentManager
import util.app.App

class TestNGSuiteAlterListener : IAlterSuiteListener {
    override fun alter(suites: MutableList<XmlSuite>?) {
        // It is important to call setConfigManager() first here
        ConfigManager.setConfigManager(MobileTestConfigManager.suiteConfig)
        if (!EnvironmentManager.isRemoteSeleniumGrid) return
        val suite = suites!![0]
        suite.parallel = XmlSuite.ParallelMode.METHODS
        suite.threadCount = ThreadCalculator.decideThreadCount()
        suite.name = "$platform $portal ${App.currentApp.buildConfigAppVersion} suite:${suite.name}"
    }
}
