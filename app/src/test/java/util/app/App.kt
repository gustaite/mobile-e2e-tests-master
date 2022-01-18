package util.app

import commonUtil.testng.config.Config
import util.EnvironmentManager
import util.EnvironmentManager.isAndroid
import util.EnvironmentManager.isRemoteSeleniumGrid
import util.app.AppConstants.NEW_JENKINS

interface IAppConfiguration {
    val pathWithoutUserDirectory: String
    val appPathSystemEnvironment: String
    val appExtension: String
    val appName: String
    val versionNumberCommand: String
    val buildNumberCommand: String
    val buildConfigAppVersion: String
    val getMasterVersionPath: String
    fun executeCommand(output: String): String
    fun executeCommandUsingPath(output: String, pathToFile: String): String
}

object App {
    private const val release = "release/"
    private val userDirectory = if (isRemoteSeleniumGrid) "/Users/steve/" else "${System.getProperty("user.home")}/"

    val currentApp get() = if (isAndroid) AndroidAppConfiguration() else IOSAppConfiguration()
    val buildNumber by lazy { getBuildNmbr() }
    val appVersion by lazy {
        if (isRelease(currentApp.buildConfigAppVersion)) removeRelease(currentApp.buildConfigAppVersion)
        else getVersionNmbr()
    }

    private fun getFinalPath(appPath: String?, defaultPath: String, appName: String, appNameNew: String): String {
        val isNewJenkins = Config.getProperty(NEW_JENKINS).toBoolean()
        val pathToDownloadedFile = getFinalPathByAppName(isNewJenkins, appPath, defaultPath, appNameNew)
        val pathOld = getFinalPathByAppName(isNewJenkins, appPath, defaultPath, appName).replace("${userDirectory}android-builds/", "${userDirectory}Dropbox/android-builds/")
        return if (EnvironmentManager.isFile(pathToDownloadedFile)) pathToDownloadedFile else pathOld
    }

    private fun getFinalPathByAppName(isNewJenkins: Boolean, appPath: String?, defaultPath: String, appName: String): String {
        return when {
            isNewJenkins && appPath.isNullOrBlank() -> defaultPath + appName
            isNewJenkins && !appPath.isNullOrBlank() -> appPath + appName
            else -> if (appPath.isNullOrBlank()) defaultPath + appName else appPath
        }
    }

    fun appPath(): String = getFinalPath(Config.getProperty(currentApp.appPathSystemEnvironment), "$userDirectory${currentApp.pathWithoutUserDirectory}", appName(), appNameNew())

    private fun appName(): String {
        val appName = getPath(currentApp.buildConfigAppVersion)
        return "$appName${currentApp.appExtension}"
    }

    private fun appNameNew(): String {
        val appName =
            if (isAndroid) {
                when {
                    isTag(currentApp.buildConfigAppVersion) -> "releases/${currentApp.buildConfigAppVersion}/${currentApp.appName}"
                    currentApp.buildConfigAppVersion.isEmpty() -> "nightly/${currentApp.getMasterVersionPath}"
                    currentApp.buildConfigAppVersion.contains("PR-") -> "PR/${currentApp.buildConfigAppVersion}/${currentApp.appName}"
                    else -> "nightly/${currentApp.buildConfigAppVersion}/${currentApp.appName}"
                }
            } else {
                getPath(currentApp.buildConfigAppVersion)
            }
        return "$appName${currentApp.appExtension}"
    }

    private fun getPath(buildConfigAppVersion: String): String {
        return if (buildConfigAppVersion.isEmpty()) currentApp.getMasterVersionPath else getReleaseVersionPath()
    }

    private fun getReleaseVersionPath(): String {
        return if (isTag(currentApp.buildConfigAppVersion)) {
            "v${currentApp.buildConfigAppVersion}/${currentApp.buildConfigAppVersion}/${currentApp.appName}-v${currentApp.buildConfigAppVersion}"
        } else {
            "${currentApp.buildConfigAppVersion}/${currentApp.appName}"
        }
    }

    private fun getVersionNmbr(): String {
        val version = currentApp.executeCommand(currentApp.versionNumberCommand)
        return Regex("(\\d.+)").find(version)?.value ?: "Not available"
    }

    private fun getBuildNmbr(): String {
        val build = currentApp.executeCommand(currentApp.buildNumberCommand)
        return Regex("\\d{5}").find(build)?.value ?: "Not available"
    }

    private fun removeRelease(buildConfigAppVersion: String): String {
        return buildConfigAppVersion.lowercase().replace(release, "")
    }

    private fun isRelease(buildConfigAppVersion: String): Boolean {
        return buildConfigAppVersion.isNotEmpty() && buildConfigAppVersion.lowercase().contains(release)
    }

    private fun isTag(buildConfigAppVersion: String): Boolean {
        return Regex("^[0-9]").containsMatchIn(buildConfigAppVersion)
    }
}
