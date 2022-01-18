package util.testng

import commonUtil.data.enums.OS
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.VintedSuiteConfig
import commonUtil.testng.config.Config
import util.BuildConfig

object MobileTestConfigManager {
    private val portal get() = VintedPortal.valueOf(Config.getConfigValue(BuildConfig.PORTAL, VintedPortal.LT.toString()))
    private val platform get() = OS.valueOf(Config.getConfigValue(BuildConfig.PLATFORM, OS.Android.toString()))
    private val androidAppVersion get() = System.getProperty("ANDROID_APP_API_VERSION")
    private val iosAppVersion get() = System.getProperty("IOS_APP_API_VERSION")
    private val maxRetryCount
        get() = Config.getConfigValue("MAX_RETRY_COUNT", BuildConfig.MAX_RETRY_COUNT, "1").toIntOrNull() ?: 1
    private val runAllOnSandbox = Config.getConfigValue(BuildConfig.RUN_ALL_ON_SANDBOX, defaultValue = "false").toBoolean()
    private val alwaysLogApiResponse get() = Config.getConfigValue(BuildConfig.ALWAYS_LOG_API_RESPONSE, "false").toBoolean()
    val suiteConfig get() = VintedSuiteConfig(portal = portal, platform = platform, androidAppApiVersion = androidAppVersion, iosAppApiVersion = iosAppVersion, maxRetryCount = maxRetryCount, runAllOnSandbox = runAllOnSandbox, alwaysLogApiResponse = alwaysLogApiResponse)
}
