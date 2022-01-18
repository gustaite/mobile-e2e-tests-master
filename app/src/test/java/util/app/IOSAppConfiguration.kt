package util.app

import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.config.Config.getConfigValue
import util.BuildConfig
import util.app.AppConstants.APP_PATH
import util.app.AppConstants.IOS_APP_VERSION
import util.driver.IosAppSync
import util.driver.IosInfoKey
import java.io.BufferedReader
import java.io.InputStreamReader

class IOSAppConfiguration : IAppConfiguration {
    override val appExtension = ".ipa"
    override val appPathSystemEnvironment: String = APP_PATH
    override val appName: String by lazy { portal.mobile.appName.ios }
    override val buildConfigAppVersion: String
        get() = getConfigValue(IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "").trim()

    override val pathWithoutUserDirectory: String by lazy { "ios-builds/" }
    override val getMasterVersionPath: String by lazy { "$appName$buildConfigAppVersion" }

    override val buildNumberCommand: String = IosInfoKey.CFBundleVersion.name
    override val versionNumberCommand: String = IosInfoKey.CFBundleShortVersionString.name

    override fun executeCommand(output: String): String {
        return IosAppSync.getInfoAboutApp(IosInfoKey.getKeyFromValue(output)) ?: ""
    }

    override fun executeCommandUsingPath(output: String, pathToFile: String): String {
        commonUtil.reporting.Report.addMessage("PATH to file: '$pathToFile'")
        val command = "/usr/libexec/PlistBuddy -c print \"$pathToFile\" | grep $output"
        val process = ProcessBuilder("bash", "-c", command).start()
        return BufferedReader(InputStreamReader(process.inputStream)).use { it.lines().findFirst().get() }
    }
}
