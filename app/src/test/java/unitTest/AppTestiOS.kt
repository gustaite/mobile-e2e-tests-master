package unitTest

import commonUtil.asserts.VintedAssert
import commonUtil.testng.config.ConfigManager
import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.Config
import commonUtil.testng.config.Config.getConfigValue
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import util.app.App
import util.BuildConfig
import util.EnvironmentManager
import util.app.AppConstants

class AppTestiOS {

    companion object {
        private const val explanationMsg = "App path does not match expected"
    }

    @Before
    fun before() {
        mockkObject(EnvironmentManager)
        mockkObject(Config)
        mockkObject(ConfigManager)
        every { EnvironmentManager.isAndroid } returns false
        every { EnvironmentManager.isiOS } returns true
        every { EnvironmentManager.isRemoteSeleniumGrid } returns true
        every { EnvironmentManager.isFile(any()) } returns false
        every { ConfigManager.portal } returns VintedPortal.PL
        every { Config.getProperty(AppConstants.APP_PATH) } returns ""
    }

    @Test
    fun getAppPathWhenAppPathVariableIsSet() {
        every { Config.getProperty(AppConstants.APP_PATH) } returns "/Users/steve/Downloads/Custom.app"
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns ""
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Downloads/Custom.app", explanationMsg)
    }

    @Test
    fun getAppPathWhenAppPathVariableIsSetNewJenkins() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns ""
        every { Config.getProperty(AppConstants.NEW_JENKINS) } returns "true"
        every { Config.getProperty(AppConstants.APP_PATH) } returns "/Users/steve/Downloads/"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/Downloads/PLVinted.ipa", explanationMsg)
        every { Config.getProperty(AppConstants.APP_PATH) } returns "/Users/steve/ios-builds/my-simulator/"
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/my-simulator/PLVinted.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForReleaseBranch_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns "release/20.6"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/release/20.6/PLVinted.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForMasterBranch_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns ""
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/PLVinted.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForVersion_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns "20.6.1"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/v20.6.1/20.6.1/PLVinted-v20.6.1.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns "fix/branch"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/fix/branch/PLVinted.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForOtherBranch2_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns "fixSmth"
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/fixSmth/PLVinted.ipa", explanationMsg)
    }

    @Test
    fun getAppPathForVersionWithSpace_downloadedIpa() {
        every { getConfigValue(AppConstants.IOS_APP_VERSION, BuildConfig.IOS_APP_VERSION, "") } returns " 20.6.1 "
        every { EnvironmentManager.isFile(any()) } returns true
        VintedAssert.assertEquals(App.appPath(), "/Users/steve/ios-builds/v20.6.1/20.6.1/PLVinted-v20.6.1.ipa", explanationMsg)
    }

    @After
    fun afterTests() {
        unmockkAll()
    }
}
