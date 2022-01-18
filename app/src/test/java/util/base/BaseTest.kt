package util.base

import RobotFactory.actionBarRobot
import RobotFactory.deepLink
import api.controllers.PortalConfigurationAPI
import api.controllers.item.deleteAllItems
import api.controllers.item.deleteAllItemsOlderThanNumberOfDays
import api.controllers.item.getItems
import api.controllers.user.paymentsApi
import api.data.models.VintedItem
import api.data.models.VintedUser
import api.factories.UserFactory
import api.util.ParallelApi
import commonUtil.Util.Companion.sleepWithinStep
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.extensions.isInitialized
import commonUtil.reporting.AnnotationExtractor
import commonUtil.reporting.Report
import commonUtil.testng.BeforeTestBehaviors
import commonUtil.testng.ExecutionLogs
import commonUtil.testng.config.ConfigManager.portal
import commonUtil.testng.config.PortalFactory
import commonUtil.thread
import io.qameta.allure.*
import org.openqa.selenium.NoSuchSessionException
import org.openqa.selenium.WebDriverException
import org.testng.ITestContext
import org.testng.ITestResult
import org.testng.SkipException
import org.testng.annotations.AfterMethod
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeSuite
import test.basic.BasicUserTests
import util.*
import util.EnvironmentManager.deleteAndroidFiles
import util.absfeatures.SkipTestController
import util.driver.WebDriverFactory
import util.driver.WebDriverFactory.driver
import util.image.ImageFactory.Companion.CAT
import util.image.ImageFactory.Companion.ITEM_10_PHOTO
import util.image.ImageFactory.Companion.ITEM_10a_PHOTO
import util.image.ImageFactory.Companion.ITEM_11_PHOTO
import util.image.ImageFactory.Companion.ITEM_11a_PHOTO
import util.image.ImageFactory.Companion.ITEM_12_PHOTO
import util.image.ImageFactory.Companion.ITEM_12a_PHOTO
import util.image.ImageFactory.Companion.ITEM_13_PHOTO
import util.image.ImageFactory.Companion.ITEM_13a_PHOTO
import util.image.ImageFactory.Companion.ITEM_14_PHOTO
import util.image.ImageFactory.Companion.ITEM_14a_PHOTO
import util.image.ImageFactory.Companion.ITEM_15_PHOTO
import util.image.ImageFactory.Companion.ITEM_15a_PHOTO
import util.image.ImageFactory.Companion.ITEM_16_PHOTO
import util.image.ImageFactory.Companion.ITEM_16a_PHOTO
import util.image.ImageFactory.Companion.ITEM_17_PHOTO
import util.image.ImageFactory.Companion.ITEM_17a_PHOTO
import util.image.ImageFactory.Companion.ITEM_1_PHOTO
import util.image.ImageFactory.Companion.ITEM_2_PHOTO
import util.image.ImageFactory.Companion.ITEM_3_PHOTO
import util.image.ImageFactory.Companion.ITEM_3a_PHOTO
import util.image.ImageFactory.Companion.ITEM_4_PHOTO
import util.image.ImageFactory.Companion.ITEM_4a_PHOTO
import util.image.ImageFactory.Companion.ITEM_5_PHOTO
import util.image.ImageFactory.Companion.ITEM_5a_PHOTO
import util.image.ImageFactory.Companion.ITEM_6_PHOTO
import util.image.ImageFactory.Companion.ITEM_6a_PHOTO
import util.image.ImageFactory.Companion.ITEM_7_PHOTO
import util.image.ImageFactory.Companion.ITEM_7a_PHOTO
import util.image.ImageFactory.Companion.ITEM_8_PHOTO
import util.image.ImageFactory.Companion.ITEM_8a_PHOTO
import util.image.ImageFactory.Companion.ITEM_9_PHOTO
import util.image.ImageFactory.Companion.ITEM_9a_PHOTO
import util.image.ImageFactory.Companion.PHOTO_TIPS
import util.image.ImageFactory.Companion.PHOTO_TIPS_BABIES
import util.image.ImageFactory.Companion.PHOTO_TIPS_BABIES_IN_GRID
import util.image.ImageUtil
import util.image.Screenshot
import util.reporting.AllureReport
import util.reporting.AllureReport.Companion.issueUrl
import util.reporting.AllureReport.Companion.testRailUrl
import util.reporting.Logs
import java.lang.reflect.Method

open class BaseTest {
    companion object {
        val skippedTests: MutableMap<String, SkipException> = mutableMapOf()
        private var skipTest: Boolean? by thread.lateinit()
        private val baseHelper: BaseTestHelper get() = BaseTestHelper()
        private val skipTestController: SkipTestController get() = SkipTestController()
        var isInitialSetupCompleted by thread(false)
        var mainUser: VintedUser by thread.lateinit()
        var otherUser: VintedUser by thread.lateinit()

        // Use oneTestUser as user for particular test with cleanup. Do not use oneTestUser if you plan to login to it!!!
        var oneTestUser: VintedUser by thread.lateinit()
        var loggedInUser: VintedUser by thread.lateinit()
        var flexibleAddressUser: VintedUser by thread.lateinit()
        var baseUser: VintedUser? by thread.lateinit()
            private set
        val defaultUser get() = UserFactory.defaultUser()
        val businessUser get() = UserFactory.businessUser()
        val withItemsUser: VintedUser by lazy { UserFactory.createUserWithTenItems() }
        val withItemsUserItem: VintedItem by lazy { withItemsUser.getItems().first() }
        val systemConfiguration get() = PortalConfigurationAPI.getConfiguration(loggedInUser).systemConfiguration
        val itemStatuses get() = PortalConfigurationAPI.getConfiguration(loggedInUser).statuses
        val listOfItemPhotos by lazy {
            listOf(
                ITEM_17a_PHOTO,
                ITEM_16a_PHOTO,
                ITEM_15a_PHOTO,
                ITEM_14a_PHOTO,
                ITEM_13a_PHOTO,
                ITEM_12a_PHOTO,
                ITEM_11a_PHOTO,
                ITEM_10a_PHOTO,
                ITEM_9a_PHOTO,
                ITEM_8a_PHOTO,
                ITEM_7a_PHOTO,
                ITEM_6a_PHOTO,
                ITEM_5a_PHOTO,
                ITEM_4a_PHOTO,
                ITEM_3a_PHOTO,
                ITEM_17_PHOTO,
                ITEM_16_PHOTO,
                ITEM_15_PHOTO,
                ITEM_14_PHOTO,
                ITEM_13_PHOTO,
                ITEM_12_PHOTO,
                ITEM_11_PHOTO,
                ITEM_10_PHOTO,
                ITEM_9_PHOTO,
                ITEM_8_PHOTO,
                ITEM_7_PHOTO,
                ITEM_6_PHOTO,
                ITEM_5_PHOTO,
                ITEM_4_PHOTO,
                ITEM_3_PHOTO,
                ITEM_1_PHOTO,
                ITEM_2_PHOTO,
                PHOTO_TIPS_BABIES,
                PHOTO_TIPS_BABIES_IN_GRID,
                PHOTO_TIPS,
                CAT
            )
        }

        val userForAbTestOrFeatureCheck: VintedUser
            get() {
                val user = when {
                    baseUser.isInitialized() -> baseUser!!
                    loggedInUser.isInitialized() -> loggedInUser
                    else -> defaultUser
                }
                Report.addMessage("AB test/Feature checked with user: USERNAME: ${user.username} ID: ${user.id}")
                return user
            }
    }

    @BeforeSuite(description = "Before suite (b): Create one user with items")
    fun beforeSuite_b_createWithItemsUser() {
        kotlin.runCatching {
            if (withItemsUser.isInitialized() && portal.payments) {
                withItemsUser.paymentsApi.addPaymentsAccountAndValidateItExists()
            }
        }
    }

    @BeforeSuite(description = "Before suite (a): Stop test suite if minimum amount of nodes is not reached")
    fun beforeSuite_a_stopSuiteWhenMinimumAmountOfNodesNotReached(context: ITestContext) {
        SeleniumGridManager.stopSuiteWhenMinimumAmountOfNodesNotReached(context)
    }

    @BeforeMethod(description = "Before method 'base' (a): Create required users and set base user for AB tests check")
    fun beforeMethod_a_AddBaseUser(context: ITestContext, method: Method) {
        baseHelper.userCreation.createMainAndOtherUsers()
        baseHelper.userCreation.createUsersByBehaviorBeforeTest(context, method)
        setBaseUserByBehaviorBeforeTest(context, method)
    }

    @BeforeMethod(description = "Before method 'base' (b): Check if test should be skipped", alwaysRun = true)
    fun beforeMethod_b_CheckForSkip(method: Method) {
        skipTestController.skipWhenConditionMetByClassOrTestName(method).let { exception ->
            if (exception != null) {
                setExceptionAndThrow(exception, method)
            }
        }
    }

    @BeforeMethod(description = "Before method 'base' (c): Get or set driver (and reset app if necessary)")
    fun beforeMethod_c_GetOrSetDriver(context: ITestContext, method: Method) {
        try {
            WebDriverFactory.getOrSetDriver(method)
            if (isInitialSetupCompleted) baseHelper.performResetAppBeforeTestBehaviour(context, method)
        } catch (e: WebDriverException) {
            Report.addMessage("WebDriverException was caught (probably during reset app phase): ${e.message}")
            WebDriverFactory.getOrSetDriver(method)
            isInitialSetupCompleted = false
        }
    }

    @BeforeMethod(description = "Before method 'base' (d): Check if test should be skipped on device", alwaysRun = true)
    fun beforeMethod_d_CheckIfTestShouldBeSkippedForDevice(method: Method) {
        Android.doIfAndroid {
            if (driver.isInitialized()) {
                skipTestController.skipParticularDeviceByTestName(method.name).let { exception ->
                    if (exception != null) {
                        skipTest = true
                        setExceptionAndThrow(exception, method)
                    }
                }
            }
        }
    }

    @BeforeMethod(description = "Before method 'base' (e): Setup app if necessary")
    fun beforeMethod_e_SetupApp(testResult: ITestResult) {
        if (skipTest != true) {
            Logs.startCollectingiOSLogs(testResult)
            if (!isInitialSetupCompleted) {
                ImageUtil.uploadImageToDeviceOnceInDay(listOfItemPhotos)

                try {
                    baseHelper.recording.startRecording()
                    baseHelper.app.iOSActivateApp()
                    baseHelper.app.selectSandboxAndLanguage()
                } catch (e: WebDriverException) {
                    VintedAssert.fail("Before class failed, ${e.message}")
                } finally {
                    AllureReport.stopAndAddRecording()
                }

                isInitialSetupCompleted = true
            }
        }
    }

    @BeforeMethod(description = "Before method 'base' (f): Start recording and log in if needed", alwaysRun = true)
    fun beforeMethod_f_StartRecordingAndLoginIfNeeded(context: ITestContext, method: Method) {
        if (!driver.isInitialized() || skipTest == true) {
            skipTest = false
            return
        }
        baseHelper.logs.getLogs()
        baseHelper.recording.startRecording()
        baseHelper.performBeforeTestBehavior(context, method)
    }

    @BeforeMethod(description = "Before method 'base' (h): Reset iOS language to portal default")
    fun beforeMethod_h_resetLanguageIos() {
        deepLink.selectLanguageiOS()
    }

    @AfterMethod(description = "After method 'base' (b): Logs & user info", alwaysRun = true)
    fun afterMethod_b_addLogsAndUserInfo(result: ITestResult) {
        if (!driver.isInitialized()) return

        AllureReport.saveLogcatLogs()
        AllureReport.saveIosLogs(result)
        baseHelper.logs.addUserInfo()
    }

    @AfterMethod(description = "After method 'base' (c): Clean users after test")
    fun afterMethod_c_CleanUsers(method: Method) {
        if (method.declaringClass == BasicUserTests::class.java) return

        val loggedInUserCleanupTasks = baseHelper.userCleanup.getLoggedInUserCleanupTasks()
        val otherUserCleanupTasks = baseHelper.userCleanup.getUserCleanupTask(user = otherUser, userType = "Other")
        val oneTestUserCleanupTasks =
            baseHelper.userCleanup.getUserCleanupTask(user = oneTestUser, userType = "OneTest")
        val flexibleAddressUserCleanupTasks =
            baseHelper.userCleanup.getUserCleanupTask(user = flexibleAddressUser, userType = "FlexibleAddressUser")

        val defaultUserItemsCleanupTasks = listOf {
            if (portal.name.lowercase().contains("sb")) defaultUser.deleteAllItemsOlderThanNumberOfDays(4)
        }

        val parallelCount = null // if (portal.isSandbox) 1 else null
        ParallelApi.run((loggedInUserCleanupTasks + otherUserCleanupTasks + flexibleAddressUserCleanupTasks + defaultUserItemsCleanupTasks + oneTestUserCleanupTasks), parallelCount)
    }

    @AfterMethod(description = "Screenshot | Video", alwaysRun = true)
    fun afterMethod_a_HandleResult(result: ITestResult) {
        if (!driver.isInitialized()) return
        if (!result.isSuccess) {
            val screenshotResult = Screenshot.takeScreenshot()

            Report.addImage(screenshotResult.screenshot)
            AllureReport.stopAndAddRecording()
            AllureReport.saveLogcatLogs()
            if (screenshotResult.driverExceptionNeedsRestart) {
                WebDriverFactory.quitDriver()
                WebDriverFactory.getOrSetDriver()
                isInitialSetupCompleted = false
                return
            }
        } else {
            baseHelper.recording.stopRecording(saveRecording = false)
        }
        if (deleteAndroidFiles) {
            ImageUtil.deleteAllInternalStorageFiles()
        }
    }

    @AfterMethod(description = "After method 'base' (d): Restore app state and logout", alwaysRun = true)
    fun afterMethod_d_RestoreAppStateAndLogout() {
        if (!driver.isInitialized()) return
        baseHelper.app.restoreAppState()
        deepLink.logout()
        kotlin.runCatching {
            // Do screenshot after logout to see if no additional modals visible
            sleepWithinStep(300)
            Report.addImage(Screenshot.takeScreenshot().screenshot)
            if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.UK)) actionBarRobot.closeBottomSheetComponentIfVisible()
        }
    }

    @AfterMethod(description = "After method 'base' (e): Reset app if necessary", alwaysRun = true)
    fun afterMethod_e_ResetAppIfNecessary(context: ITestContext, method: Method) {
        baseHelper.performAfterTestAppReset(context, method)
    }

    @AfterSuite(description = "After suite (a): Delete apps and quit drivers")
    fun afterSuite_a_DeleteAppsAndQuitDrivers() {
        val bundleId: String = portal.mobile.appPackage.getPackage()
        WebDriverFactory.drivers.distinct().parallelStream().forEach {
            try {
                IOS.doIfiOS { it.closeApp() }
                val wasAppUninstalled = it.removeApp(bundleId)
                Report.addMessage("App uninstall was $wasAppUninstalled")
            } catch (e: NoSuchSessionException) {
                Report.addMessage("Got error (NoSuchSessionException) performing task, $e")
            } catch (e: WebDriverException) {
                Report.addMessage("Got error (WebDriverException) performing task, $e")
            }
        }
        WebDriverFactory.quitAll()
    }

    @AfterSuite(description = "After suite (b): Delete withItemsUser items")
    fun afterSuite_b_deleteWithItemsUserItems() {
        kotlin.runCatching {
            val withItemsUserCleanupTasks = listOf { withItemsUser.deleteAllItems() }
            ParallelApi.run((withItemsUserCleanupTasks))
        }
    }

    @AfterSuite(description = "After suite (c): Set environment and add execution", alwaysRun = true)
    fun afterSuite_c_SetEnvironmentAddExecutionLog() {
        try {
            Allure.addAttachment("Execution Log", "${ExecutionLogs.getExecutionLog()}")
            AllureReport.setEnvironment()
            Report.addMessage("Created user count in suite run: ${UserFactory.userCount}")
        } catch (e: NullPointerException) {
            Report.addMessage("Exception was caught in after suite: ${e.message}")
        }
    }

    @AfterSuite(description = "After suite (d): Collect tests by issue annotation", alwaysRun = true)
    fun afterSuite_d_CollectTestsByIssueAnnotation() {
        kotlin.runCatching {
            AnnotationExtractor.attachAnnotationsHtmlToReport(Issue::class, issueUrl, "ISSUES on tests")
            AnnotationExtractor.attachAnnotationsHtmlToReport(TmsLink::class, testRailUrl, "TestRail cases on tests")
        }
    }

    @Step("Set base user by behavior before test")
    private fun setBaseUserByBehaviorBeforeTest(context: ITestContext, method: Method) {
        when (context.getAttribute(method.name)) {
            BeforeTestBehaviors.LOGIN_TO_MAIN_THREAD_USER -> {
                baseUser = mainUser
            }
            BeforeTestBehaviors.LOGIN_TO_DEFAULT_USER -> {
                baseUser = defaultUser
            }
            BeforeTestBehaviors.LOGIN_TO_NEW_USER -> {
                val user = UserFactory.createRandomUser()
                baseUser = user
            }
            BeforeTestBehaviors.LOGIN_TO_WITH_ITEMS_USER -> {
                baseUser = withItemsUser
            }
            BeforeTestBehaviors.LOGIN_TO_BUSINESS_USER -> {
                val user = UserFactory.businessUser()
                baseUser = user!!
            }
            else -> {
                baseUser = null
                Report.addMessage("BaseUser was not set here because test had no user annotation")
            }
        }
    }

    @Step("Add exception to skippedTests and throw it")
    private fun setExceptionAndThrow(exception: Throwable, method: Method) {
        val fullName = "${method.declaringClass.name}.${method.name}"
        skippedTests[fullName] = exception as SkipException
        throw exception
    }
}
