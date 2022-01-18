package unitTest

import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.Config
import commonUtil.testng.config.Config.getConfigValue
import commonUtil.testng.config.ConfigManager
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import util.BuildConfig
import util.EnvironmentManager
import util.app.App
import util.app.AppConstants

class AppTestAndroid {
    companion object {
        private const val explanationMsg = "Apk path does not match"
    }

    @Before
    fun before() {
        mockkObject(EnvironmentManager)
        mockkObject(Config)
        mockkObject(ConfigManager)
        every { EnvironmentManager.isAndroid } returns true
        every { EnvironmentManager.isiOS } returns false
        every { EnvironmentManager.isRemoteSeleniumGrid } returns true
        every { EnvironmentManager.isFile(any()) } returns false
        every { ConfigManager.portal } returns VintedPortal.PL
        every { Config.getProperty(AppConstants.APK_PATH) } returns ""
    }

    @Test
    fun getAppPathForReleaseBranch() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "release/20.6"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/release/20.6/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForMasterBranch() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/master/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForVersion() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "20.6.1"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/v20.6.1/20.6.1/PlRelease-v20.6.1.apk", explanationMsg)
    }

    @Test
    fun getAppPathForPR() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "PR-123"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/PR-123/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "fix/branch"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/fix/branch/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch2() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "fixsmth"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/fixsmth/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForVersionWithSpace() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns " 20.6.1 "
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/v20.6.1/20.6.1/PlRelease-v20.6.1.apk", explanationMsg)
    }

    @Test
    fun getAppPathWhenApkPathVariableIsSet() {
        every { Config.getProperty(AppConstants.APK_PATH) } returns "/Users/steve/Downloads/Custom.apk"
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Downloads/Custom.apk", explanationMsg)
    }

    @Test
    fun getAppPathWhenApkPathVariableIsSetNewJenkins() {
        every { Config.getProperty(AppConstants.APK_PATH) } returns "/Users/steve/Dropbox/android-builds-tmp/"
        every { Config.getProperty(AppConstants.NEW_JENKINS) } returns "true"
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds-tmp/master/PlRelease.apk", explanationMsg)
        every { Config.getProperty(AppConstants.APK_PATH) } returns "/Users/steve/Dropbox/android-builds/tmp/"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds/tmp/master/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForReleaseBranch_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "release/20.6"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/nightly/release/20.6/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForMasterBranch_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/nightly/master/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForVersion_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "20.6.1"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/releases/20.6.1/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForPR_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "PR-123"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/PR/PR-123/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "fix/branch"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/nightly/fix/branch/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch2_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns "fixsmth"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/nightly/fixsmth/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathForVersionWithSpace_downloadedApk() {
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns " 20.6.1 "
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/android-builds/releases/20.6.1/PlRelease.apk", explanationMsg)
    }

    @Test
    fun getAppPathWhenApkPathVariableIsSet_downloadedApk() {
        every { Config.getProperty(AppConstants.APK_PATH) } returns "/Users/steve/Downloads/Custom.apk"
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Downloads/Custom.apk", explanationMsg)
    }

    @Test
    fun getAppPathWhenApkPathVariableIsSetNewJenkins_downloadedApk() {
        every { Config.getProperty(AppConstants.APK_PATH) } returns "/Users/steve/Dropbox/android-builds-tmp/"
        every { Config.getProperty(AppConstants.NEW_JENKINS) } returns "true"
        every { getConfigValue(AppConstants.ANDROID_APP_VERSION, BuildConfig.ANDROID_APP_VERSION, "") } returns ""
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Dropbox/android-builds-tmp/nightly/master/PlRelease.apk", explanationMsg)
    }

    @After
    fun afterTests() {
        unmockkAll()
    }
}
