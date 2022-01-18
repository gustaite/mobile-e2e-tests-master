package util

import commonUtil.Util
import commonUtil.reporting.Report
import io.qameta.allure.Step
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriverException
import util.driver.MobileSelector
import util.driver.WebDriverFactory
import util.values.ScrollDirection

class IOSScroll {
    fun scrollUp() {
        // click on status bar
        IOS.doIfiOS { VintedDriver.tap(1, 1) }
    }

    @Step("Scroll down to element for 30 seconds max")
    fun iOSScrollDownToElementNewWay(locator: Pair<MobileSelector, String>?) {
        IOS.doIfiOS {
            Util.retryUntil(
                block = {
                    scrollDownBitWhenExceptionMet(locator)
                },
                tryForSeconds = 30
            )
        }
    }

    @Step("Scroll down to element for 30 seconds max")
    fun iOSScrollDownToElementNewWay(accessibilityId: String) {
        IOS.doIfiOS {
            val locator = Pair(MobileSelector.IosAccessibilityId, accessibilityId)
            iOSScrollDownToElementNewWay(locator)
        }
    }

    @Step("Scroll down was executed")
    fun scrollDown(performSwipeBefore: Boolean = false) {
        IOS.doIfiOS {
            val js = WebDriverFactory.driver as JavascriptExecutor
            val scrollObject = HashMap<String, String>()
            scrollObject["direction"] = ScrollDirection.DOWN.name.lowercase()
            if (performSwipeBefore) {
                // Sometimes scroll cannot be executed because of open keyboard, swipe closes it
                js.executeScript("mobile: swipe", scrollObject)
            }
            js.executeScript("mobile: scroll", scrollObject)
        }
    }

    @Step("iOS scroll down to element or scroll a bit down if exception met")
    private fun scrollDownBitWhenExceptionMet(locator: Pair<MobileSelector, String>?): Boolean {
        val useScroll = try {
            scrollDownToElementByMobileSelector(locator)
            true
        } catch (e: WebDriverException) {
            Report.addMessage("message: $e")
            false
        }
        return useScroll.also { VintedDriver.scrollDownABit(0.8, 0.7) }
    }

    @Step("Scroll down to element by mobile selector was executed")
    private fun scrollDownToElementByMobileSelector(locator: Pair<MobileSelector, String>?, performSwipeBefore: Boolean = false) {
        IOS.doIfiOS {
            val js = WebDriverFactory.driver as JavascriptExecutor
            val scrollObject = HashMap<String, String>()
            when (locator?.first) {
                MobileSelector.IosAccessibilityId -> {
                    val predicate = "name == '${locator.second}' && visible == 1"
                    scrollObject["predicateString"] = predicate
                    Report.addMessage("Scroll by predicateString: $predicate")
                }
                MobileSelector.IosPredicateString -> {
                    val predicate = "${locator.second} && visible == 1"
                    scrollObject["predicateString"] = predicate
                    Report.addMessage("Scroll by predicateString: $predicate")
                }
                else -> {
                    scrollObject["direction"] = ScrollDirection.DOWN.name.lowercase()
                    Report.addMessage("Scroll by direction: down")
                }
            }
            if (performSwipeBefore) {
                // Sometimes scroll cannot be executed because of open keyboard, swipe closes it
                js.executeScript("mobile: swipe", scrollObject)
            }
            js.executeScript("mobile: scroll", scrollObject)
        }
    }
}
