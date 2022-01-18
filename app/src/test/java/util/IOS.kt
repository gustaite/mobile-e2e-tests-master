package util

import api.controllers.GlobalAPI
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedLanguage
import io.appium.java_client.ios.IOSTouchAction
import io.appium.java_client.remote.HideKeyboardStrategy
import io.appium.java_client.touch.TapOptions
import io.appium.java_client.touch.offset.PointOption
import io.qameta.allure.Step
import org.openqa.selenium.By
import util.AppTexts.userByCountry
import util.EnvironmentManager.isAndroid
import util.driver.MobileSelector
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WebDriverFactory.driver
import util.driver.asIOSDriver

class IOS {

    companion object {
        private val iOSScroll = IOSScroll()
        private const val currencyRegex = "'.*€.*||.*$.*||.*zł.*||.*£.*||.*Kč.*'"
        const val predicateWithCurrencySymbolsGrouped = "(value MATCHES $currencyRegex OR label MATCHES $currencyRegex)"
        const val predicateWithCurrencySymbols = "value CONTAINS '€' OR value CONTAINS '$' OR value CONTAINS 'zł' OR value CONTAINS '£' OR value CONTAINS 'Kč'"
        const val predicateWithCurrencySymbolsByName = "name CONTAINS '€' OR name CONTAINS '$' OR name CONTAINS 'zł' OR name CONTAINS '£' OR name CONTAINS 'Kč'"
        private val languageCodeTextValuesMap: MutableMap<VintedLanguage, Map<String, String>> = mutableMapOf()

        private val iOSElementValues
            get(): Map<String, String> {
                val language = userByCountry.country.language
                if (languageCodeTextValuesMap[language] == null) {
                    languageCodeTextValuesMap[language] = GlobalAPI.getIosTexts(user = userByCountry, country = userByCountry.country)
                }
                return languageCodeTextValuesMap[language].also { commonUtil.reporting.Report.addMessage("Used language '$language' as key'") }
                    ?: throw IllegalArgumentException("Texts map was not found for language '$language'")
            }

        private fun getKeys(): List<String> {
            return iOSElementValues.keys.toList()
        }

        fun getVoiceOverElementValue(key: String): String {
            // Return only first word For example: 'Followers: %{followed_count} followers, %{following_count} following' 'Followers:' would be returned
            return getElementValue(key).substringBefore(" ")
        }

        fun getElementValue(key: String): String {
            val value = iOSElementValues.getValue(key)
            commonUtil.reporting.Report.addMessage("IOS key: $key, value: $value \n Map keys: ${languageCodeTextValuesMap.keys}")
            return value
        }

        fun getRandomKeyValuePair(): Pair<String, String> {
            val key = getKeys().random()
            val value = getElementValue(key)
            return Pair(key, value)
        }

        fun findElementByTranslationKey(key: String): VintedElement = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId(getElementValue(key)))

        fun findElementByTextContains(text: String, type: ElementType = ElementType.STATIC_TEXT): VintedElement =
            VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("type == '${type.typeName}' AND value CONTAINS '$text'"))

        @Step("FindAll iOS element")
        fun findAllElement(iosBy1: By, iosBy2: By): VintedElement {
            if (VintedElement { VintedDriver.findElement(iOSBy = iosBy1).mobileElementForVisibilityCheck }.isVisible(1)) {
                return VintedDriver.findElement(iOSBy = iosBy1)
            }
            return VintedDriver.findElement(iOSBy = iosBy2)
        }

        private fun tap(x: Int, y: Int, tapCount: Int) {
            doIfiOS {
                IOSTouchAction(driver)
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

        @Step("Assert if keyboard is open and close it")
        fun checkIfKeyboardIsOpenAndClose() {
            doIfiOS {
                VintedAssert.assertTrue(driver.asIOSDriver().isKeyboardShown, "Keyboard should be open")
                hideKeyboard()
            }
        }

        fun pressDoneInKeyboard() = doIfiOS {
            pressSelectedKeyInKeyboard(KeyName.DONE)
        }

        fun pressSearchInKeyboard() = doIfiOS {
            pressSelectedKeyInKeyboard(KeyName.SEARCH)
        }

        fun pressReturnInKeyboard() = doIfiOS {
            pressSelectedKeyInKeyboard(KeyName.RETURN)
        }

        fun pressGoInKeyboard() = doIfiOS {
            pressSelectedKeyInKeyboard(KeyName.GO)
        }

        fun pressDeleteInKeyboard() = doIfiOS {
            pressSelectedKeyInKeyboard(KeyName.DELETE)
        }

        private fun pressSelectedKeyInKeyboard(keyName: KeyName) = doIfiOS {
            driver.asIOSDriver().hideKeyboard(HideKeyboardStrategy.PRESS_KEY, keyName.key)
        }

        fun hideKeyboard() {
            doIfiOS {
                driver.asIOSDriver().hideKeyboard()
            }
        }

        fun doIfiOS(block: () -> Unit) {
            if (isAndroid) return
            block()
        }

        fun terminateSafari() {
            doIfiOS {
                driver.terminateApp("com.apple.mobilesafari")
            }
        }

        fun scrollDown(performSwipeBefore: Boolean = false) {
            iOSScroll.scrollDown(performSwipeBefore)
        }

        fun scrollUp() {
            iOSScroll.scrollUp()
        }

        fun iOSScrollDownToElementNewWay(accessibilityId: String) {
            iOSScroll.iOSScrollDownToElementNewWay(accessibilityId)
        }

        fun iOSScrollDownToElementNewWay(locator: Pair<MobileSelector, String>?) {
            iOSScroll.iOSScrollDownToElementNewWay(locator)
        }
    }

    enum class ElementType(val typeName: String) {
        STATIC_TEXT("XCUIElementTypeStaticText"),
        TEXT_FIELD("XCUIElementTypeTextField"),
        BUTTON("XCUIElementTypeButton"),
        ANY("")
    }

    enum class KeyName(val key: String) {
        DONE("Done"),
        SEARCH("Search"),
        RETURN("return"),
        GO("Go"),
        DELETE("Delete")
    }
}
