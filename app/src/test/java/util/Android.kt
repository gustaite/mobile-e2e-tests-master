package util

import commonUtil.testng.config.ConfigManager.portal
import api.controllers.GlobalAPI
import com.google.common.collect.ImmutableMap
import commonUtil.Util.Companion.retryUntil
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedLanguage
import io.appium.java_client.MobileBy
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent
import io.appium.java_client.touch.TapOptions
import io.appium.java_client.touch.offset.PointOption
import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import util.Android.Scroll.COMMON_PREFIX
import util.AppTexts.userByCountry
import util.EnvironmentManager.isiOS
import util.driver.AdbCommands
import util.driver.VintedElement
import util.driver.Wait
import util.driver.WebDriverFactory.driver
import util.driver.asAndroidDriver
import util.values.DoNotKeepActivitiesSwitch
import util.values.NoBackgroundProcessSwitch
import util.values.ScrollDirection

class Android {
    companion object {
        private val languageCodeTextValuesMap: MutableMap<VintedLanguage, Map<String, String>> = mutableMapOf()
        val ID get() = "${portal.mobile.appPackage.android}:id/"
        val INPUT_FIELD_ID = "${ID}view_input_value"
        val INPUT_VALIDATION_FIELD_ID = "${ID}view_input_validation"
        val CELL_VALIDATION_FIELD_ID = "${ID}view_cell_validation"
        val CELL_TITLE_FIELD_ID = "${ID}view_cell_title"
        val CELL_SUBTITLE_FIELD_ID = "${ID}view_cell_subtitle"
        val CELL_TITLE_LINE_FIELD_ID = "${ID}view_cell_title_line"
        val CELL_BODY_FIELD_ID = "${ID}view_cell_body"

        private val AndroidElementValues
            get(): Map<String, String> {
                val language = userByCountry.country.language
                if (languageCodeTextValuesMap[language] == null) {
                    languageCodeTextValuesMap[language] = GlobalAPI.getAndroidTexts(user = userByCountry, country = userByCountry.country)
                }
                return languageCodeTextValuesMap[language]
                    ?: throw IllegalArgumentException("Texts map was not found for language '$language'")
            }

        fun getElementValue(key: String): String {
            val value = AndroidElementValues.getValue(key)
            commonUtil.reporting.Report.addMessage("Android key: $key, value: $value")
            return value
        }

        @Step("Click home button with Android key")
        fun clickHome() {
            doIfAndroid {
                android().pressKey(KeyEvent().withKey(AndroidKey.HOME))
            }
        }

        @Step("Click back with Android key")
        fun clickBack() {
            doIfAndroid {
                android().pressKey(KeyEvent().withKey(AndroidKey.BACK))
            }
        }

        @Step("Click enter button with Android key")
        fun clickEnter() {
            doIfAndroid {
                android().pressKey(KeyEvent().withKey(AndroidKey.ENTER))
            }
        }

        @Step("Terminate app")
        fun terminateApp(packageName: String) {
            doIfAndroid {
                android().terminateApp(packageName)
            }
        }

        @Step("Check if app not running in the background")
        fun assertIfAppIsNotRunningInBackground(packageName: String) {
            val backgroundProcess = android().executeScript("mobile: shell", AdbCommands.checkBackgroundProcess)
            commonUtil.reporting.Report.addMessage("execute adb command: \'shell ps\'")
            VintedAssert.assertFalse(packageName in backgroundProcess.toString(), "$packageName should not be running in the background")
        }

        @Step("Turn \'no backgroud process\' setting {settingSwitch}")
        fun noBackgroundProcessSetting(settingSwitch: NoBackgroundProcessSwitch, packageName: String) {
            val backgroundProcessCmd = AdbCommands.backgroundProcessCmd(settingSwitch, packageName)
            android().executeScript("mobile: shell", backgroundProcessCmd)
            commonUtil.reporting.Report.addMessage("execute adb command: \'shell appops set $packageName RUN_IN_BACKGROUND ${settingSwitch.value}\'")
        }

        @Step("Turn \'Do not keep activities \' setting {settingSwitch}")
        fun doNotKeepActivitiesSetting(settingSwitch: DoNotKeepActivitiesSwitch) {
            val keepActivitiesCmd = AdbCommands.getKeepActivitiesCmd(settingSwitch)
            android().executeScript("mobile: shell", keepActivitiesCmd)
            commonUtil.reporting.Report.addMessage("execute adb command: \'shell settings put global always_finish_activities ${settingSwitch.value}\'")
        }

        @Step("Reopen app")
        fun reopenApp() {
            doIfAndroid {
                android().launchApp()
            }
        }

        fun getPackageName(): String {
            return android().currentPackage
        }

        @Step("Assert if keyboard is open and close it")
        fun checkIfKeyboardIsOpenAndClose() {
            doIfAndroid {
                VintedAssert.assertTrue(android().isKeyboardShown, "Keyboard should be open")
                closeKeyboard()
            }
        }

        fun sendKeysUsingKeyboard(text: String) {
            doIfAndroid {
                for (char in text.toCharArray()) {
                    android().keyboard.sendKeys(char.toString())
                }
            }
        }

        @Step("Close Android keyboard")
        fun closeKeyboard() {
            doIfAndroid {
                if (android().isKeyboardShown) android().hideKeyboard()
            }
        }

        private fun tap(x: Int, y: Int, tapCount: Int) {
            doIfAndroid {
                AndroidTouchAction(driver)
                    .tap(
                        TapOptions()
                            .withPosition(PointOption.point(x, y))
                            .withTapsCount(tapCount)
                    )
                    .perform()
            }
        }

        @Step("Tap on x: {x} y: {y}")
        fun tap(x: Int, y: Int) {
            tap(x, y, 1)
        }

        @Step("Double tap on x: {x} y: {y}")
        fun doubleTap(x: Int, y: Int) {
            tap(x, y, 2)
        }

        @Step("Scroll with direction: {scrollDirection}, until element is visible")
        fun VintedElement.scrollUntilVisibleAndroid(scrollDirection: ScrollDirection): VintedElement {
            doIfAndroid {
                if (!this.isScrollable() && !this.isVisible(1)) {
                    scrollToTop()
                    retryUntil(
                        block = {
                            when (scrollDirection) {
                                ScrollDirection.UP -> scrollUpABit()
                                ScrollDirection.DOWN -> scrollDownABit()
                            }
                            this.isVisible(1)
                        },
                        tryForSeconds = 30
                    )
                }
                if (!this.isVisible()) throw Exception("Element was expected to be visible, but was not visible")
            }

            return this
        }

        fun scrollDown() {
            doIfAndroid { VintedDriver.scrollDown() }
        }

        fun scrollDownABit() {
            doIfAndroid { VintedDriver.scrollDownABit() }
        }

        fun scrollUp() {
            doIfAndroid { VintedDriver.scrollUpABit(0.2, 0.8) }
        }

        fun scrollUpABit() {
            doIfAndroid { VintedDriver.scrollUpABit() }
        }

        @Step("Scroll to the top")
        fun scrollToTop(numberOfRetries: Int = 10) {
            doIfAndroid {
                driver.findElement(MobileBy.AndroidUIAutomator("$COMMON_PREFIX.scrollToBeginning($numberOfRetries)"))
            }
        }

        fun tap(element: MobileElement?) {
            tap(element!!.center.getX(), element.center.getY())
        }

        fun closeNotificationsTray() {
            doIfAndroid {
                driver.executeScript("mobile: shell", ImmutableMap.of("command", "cmd statusbar", "args", "collapse"))
            }
        }

        private fun android(): AndroidDriver<MobileElement> {
            return driver.asAndroidDriver()
        }

        @Step("Find list of android elements")
        fun findElementList(androidBy: By): List<VintedElement> {
            return VintedDriver.findElementList(androidBy = androidBy, iOSBy = null)
        }

        @Step("FindAll android element")
        fun findAllElement(androidBy1: By, androidBy2: By): VintedElement {
            if (VintedElement { VintedDriver.findElement(androidBy = androidBy1).mobileElementForVisibilityCheck }.isVisible(1)) {
                return VintedDriver.findElement(androidBy = androidBy1)
            }
            return VintedDriver.findElement(androidBy = androidBy2)
        }

        fun doIfAndroid(block: () -> Unit) {
            if (isiOS) return
            block()
        }

        fun findOneOfPossibleElements(byList: List<By>): MobileElement? {
            var element: MobileElement? = null
            var times = 1

            while (element == null && times < 5) {
                for (by in byList) {
                    try {
                        Wait.turnOffImplicitlyWait()
                        element = driver.findElement(by)
                    } catch (e: Exception) {
                    } finally {
                        Wait.turnOnImplicitlyWait()
                    }

                    if (element != null) break
                }
                times++
            }
            return element
        }

        fun terminateChrome() {
            doIfAndroid {
                driver.executeScript("mobile: shell", ImmutableMap.of("command", "command", "args", "am force-stop com.android.chrome"))
            }
        }

        @Step("[INFO] Check if wifi was disconnected and re-enable it")
        fun wasWifiDisconnected(driver: AndroidDriver<MobileElement>): Boolean {
            var shouldThrow = false
            runCatching {
                if (!isPingOk(driver)) {
                    disableAndReenableWifi(driver)
                    shouldThrow = true
                }
            }
            return shouldThrow
        }

        @Step("Disable and re-enable wifi")
        private fun disableAndReenableWifi(driver: AndroidDriver<MobileElement>) {
            driver.executeScript("mobile: shell", AdbCommands.disableWifi)
            driver.executeScript("mobile: shell", AdbCommands.enableWifi)
        }

        @Step("Check if ping works")
        private fun isPingOk(driver: AndroidDriver<MobileElement>): Boolean {
            val pingResult = try {
                driver.asAndroidDriver().executeScript("mobile: shell", AdbCommands.pingGoogle)
            } catch (e: WebDriverException) {
                commonUtil.reporting.Report.addMessage("Exception occurred while trying execute ping")
                null
            }
            commonUtil.reporting.Report.addMessage("Ping result: $pingResult")
            return pingResult != null
        }
    }

    object Scroll {
        const val COMMON_PREFIX = "UiScrollable(UiSelector().scrollable(true).instance(0))"
        const val SCROLL_INTO_VIEW_PREFIX = "$COMMON_PREFIX.scrollIntoView(UiSelector().resourceId(\""
        const val SUFFIX = "\").instance(0))"
    }
}
