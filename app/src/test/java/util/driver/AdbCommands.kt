package util.driver

import com.google.common.collect.ImmutableMap
import util.values.DoNotKeepActivitiesSwitch
import util.values.NoBackgroundProcessSwitch

class AdbCommands {
    companion object {
        private const val command = "command"
        fun getKeepActivitiesCmd(settingSwitch: DoNotKeepActivitiesSwitch): Map<String, Any> =
            ImmutableMap.of<String, Any>(command, "settings put global", "args", "always_finish_activities ${settingSwitch.value}")

        fun backgroundProcessCmd(settingSwitch: NoBackgroundProcessSwitch, packageName: String): Map<String, Any> =
            ImmutableMap.of<String, Any>(command, "appops set", "args", "$packageName RUN_IN_BACKGROUND ${settingSwitch.value}")

        val checkBackgroundProcess: Map<String, Any> get() = ImmutableMap.of<String, Any>(command, "ps")
        val pingGoogle: ImmutableMap<String, Any> get() = ImmutableMap.of<String, Any>(command, "ping -c 1 google.com")
        val enableWifi: ImmutableMap<String, Any> get() = wifiEnableDisable("enable")
        val disableWifi: ImmutableMap<String, Any> get() = wifiEnableDisable("disable")
        private fun wifiEnableDisable(wifiCommand: String) = ImmutableMap.of<String, Any>(command, "svc wifi $wifiCommand")
    }
}
