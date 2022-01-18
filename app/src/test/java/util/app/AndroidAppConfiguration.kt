package util.app

import commonUtil.testng.config.ConfigManager.portal
import com.google.common.collect.ImmutableMap
import commonUtil.testng.config.Config.getConfigValue
import util.BuildConfig
import util.app.AppConstants.ANDROID_APP_VERSION
import util.app.AppConstants.APK_PATH
import util.driver.WebDriverFactory

class AndroidAppConfiguration : IAppConfiguration {

    override val appExtension: String = ".apk"
    override val appPathSystemEnvironment: String = APK_PATH
    override val appName: String by lazy { portal.mobile.appName.android }
    override val buildConfigAppVersion: String
        get() = getConfigValue(ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "").trim()

    override val pathWithoutUserDirectory by lazy { "android-builds/" }
    override val getMasterVersionPath: String by lazy { "master/$appName$buildConfigAppVersion" }

    override val buildNumberCommand: String = "versionCode"
    override val versionNumberCommand: String = "versionName"

    override fun executeCommand(output: String): String {
        return WebDriverFactory.driver
            .executeScript(
                "mobile: shell",
                ImmutableMap.of("command", "dumpsys", "args", "package ${portal.mobile.appPackage.android} | grep $output")
            ).toString()
    }

    override fun executeCommandUsingPath(output: String, pathToFile: String): String {
        throw NotImplementedError("Method is not supported for Android tests")
    }
}
