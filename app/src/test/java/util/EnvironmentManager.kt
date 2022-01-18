package util

import api.data.models.transaction.VintedTransactionShipmentType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import commonUtil.data.enums.OS
import commonUtil.data.enums.VintedPortal.DE
import commonUtil.data.enums.VintedPortal.SB_DE
import commonUtil.testng.config.Config.getConfigValue
import commonUtil.testng.config.Config.getEnvironmentVariable
import commonUtil.testng.config.ConfigManager.platform
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.thread
import util.data.CreditCardDetails
import java.io.File
import java.util.stream.Collectors

object EnvironmentManager {
    var ImplicitlyWaitTimeout: Long by thread.lateinit()
    var isAndroid = platform == OS.Android
    var isiOS = platform == OS.Ios
    val isRemoteSeleniumGrid = getConfigValue(BuildConfig.REMOTE_SELENIUM_GRID, "true").toBoolean()
    fun isFile(path: String) = File(path).exists()
    val deleteAndroidFiles = getConfigValue(BuildConfig.DELETE_ANDROID_FILES, defaultValue = "false").toBoolean()
    val threadCount = getConfigValue(BuildConfig.THREAD_COUNT, defaultValue = "18").toInt()
    private val deviceName = getConfigValue(BuildConfig.DEVICE_NAME, defaultValue = "")
    val specificDevices: MutableList<String> =
        deviceName.split(",").stream().map(String::trim).collect(Collectors.toList()).let { it.remove(""); it }
    val isRealIosDevice = System.getenv("REAL_DEVICE")?.toBoolean() ?: false

    const val screenshotComparisonFolder = "./resources/"

    // on SB_DE portal instruction untracked shipment type options are not found, while not sure which to use going with null (or random shipping option)
    // TODO: when root cause for this problem will become clear will update code respectively
    val preferredShipmentType: VintedTransactionShipmentType?
        get() = when (portal) {
            SB_DE -> null
            DE -> VintedTransactionShipmentType.instructionsUntracked
            else -> VintedTransactionShipmentType.labelled
        }

    fun creditCardCredentials(): CreditCardDetails? {
        val jsonAdapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(CreditCardDetails::class.java)
        val file = getConfigValue("CREDIT_CARD_DETAILS", BuildConfig.CREDIT_CARD_DETAILS, "")
        val json = File(file).readText()

        return jsonAdapter.fromJson(json)
    }

    fun getPath(systemEnv: String, default: String): String {
        val path = getEnvironmentVariable(systemEnv)
        return path ?: default
    }
}
