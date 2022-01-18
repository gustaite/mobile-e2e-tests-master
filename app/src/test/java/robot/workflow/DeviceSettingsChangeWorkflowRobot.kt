package robot.workflow

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.values.DoNotKeepActivitiesSwitch
import util.values.NoBackgroundProcessSwitch

class DeviceSettingsChangeWorkflowRobot : BaseRobot() {

    @Step("Close app turn on \'No background process\' settings and reopen app")
    fun closeAppTurnOnNoBackgroundProcessSettingAndReopenApp(appPackageName: String) {
        Android.run {
            terminateApp(appPackageName)
            noBackgroundProcessSetting(NoBackgroundProcessSwitch.ON, appPackageName)
            assertIfAppIsNotRunningInBackground(appPackageName)
            reopenApp()
        }
    }

    @Step("Turn off \'No background process\' and \'Do not keep activities\' settings")
    fun turnOffNoBackgroundProcessAndDoNotKeepActivitiesSettings(appPackageName: String) {
        Android.doNotKeepActivitiesSetting(DoNotKeepActivitiesSwitch.OFF)
        Android.noBackgroundProcessSetting(NoBackgroundProcessSwitch.OFF, appPackageName)
    }
}
