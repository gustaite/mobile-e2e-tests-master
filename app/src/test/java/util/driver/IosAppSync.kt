package util.driver

import commonUtil.extensions.isInitialized
import commonUtil.extensions.throwIfNull
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.thread
import io.qameta.allure.Step
import util.app.App
import util.driver.WebDriverFactory.driver
import util.getPackage
import java.io.File
import java.io.FileOutputStream

object IosAppSync {
    private var filePath: String by thread.lateinit()

    @Step("Get app info about {key}")
    fun getInfoAboutApp(key: IosInfoKey): String? {
        kotlin.runCatching {
            if (filePath.isInitialized()) filePath else filePath = pullInfoAndWriteToTempFile()
            val output = App.currentApp.executeCommandUsingPath(key.name, filePath)
            commonUtil.reporting.Report.addMessage("OUTPUT was: $output")
            return Regex("(?<=\\=).*").find(output)?.value
        }
        return null
    }

    @Step("Pull Info.plist bytes and write it to temp file")
    private fun pullInfoAndWriteToTempFile(): String {
        val fileBytes = driver.pullFile("@${portal.mobile.appPackage.getPackage()}/Info.plist")
        val file: File = File.createTempFile("IOS_", "_INFO")
        file.deleteOnExit()
        val filePath = file.path
        FileOutputStream(filePath).use { it.write(fileBytes) }
        return filePath
    }
}

enum class IosInfoKey {
    CFBundleVersion,
    CFBundleShortVersionString,
    BuildDate;

    companion object {
        fun getKeyFromValue(value: String): IosInfoKey {
            return values().firstOrNull { it.name == value }.throwIfNull("'$value' value was not found in IosInfoKey")
        }
    }
}
