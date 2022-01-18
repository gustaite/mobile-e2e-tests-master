package test.basic

import RobotFactory.deviceSettingsChangeWorkflowRobot
import RobotFactory.globalRobot
import RobotFactory.workflowRobot
import api.data.models.VintedUser
import commonUtil.testng.config.PortalFactory
import api.factories.UserFactory
import commonUtil.extensions.isInitialized
import commonUtil.thread
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.Android
import util.base.BaseTest
import util.driver.WebDriverFactory.driver
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import util.values.DoNotKeepActivitiesSwitch

@RunMobile
class DeviceSettingsChangeTests : BaseTest() {

    private var user: VintedUser by thread.lateinit()
    private var appPackageName: String by thread.lateinit()

    @BeforeMethod(description = "Get app package name")
    fun getAppPackage() {
        Android.doIfAndroid {
            appPackageName = Android.getPackageName()
        }
    }

    @RunMobile(platform = VintedPlatform.ANDROID)
    @Test(description = "Turn on \'No background process\' and \'Do not keep activities\' setting and check if app doesn't crash")
    fun testNoBackgroundProcessAndDoNotKeepActivitiesSettingOn() {

        Android.doNotKeepActivitiesSetting(DoNotKeepActivitiesSwitch.ON)
        deviceSettingsChangeWorkflowRobot
            .closeAppTurnOnNoBackgroundProcessSettingAndReopenApp(appPackageName)

        user = UserFactory.createRandomUser()
        if (PortalFactory.isSandbox) globalRobot.selectSandboxAndConfirm()
        workflowRobot
            .signIn(user)
            .goToFeedAndSearchSendAppToBackgroundAndGoToFeedAndSearch()
    }

    @AfterMethod(description = "Set default device settings", alwaysRun = true)
    fun setDefaultDeviceSettings() {
        Android.doIfAndroid {
            if (driver.isInitialized()) {
                deviceSettingsChangeWorkflowRobot
                    .turnOffNoBackgroundProcessAndDoNotKeepActivitiesSettings(appPackageName)
            }
        }
    }
}
