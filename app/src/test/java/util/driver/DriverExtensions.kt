package util.driver

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.qameta.allure.Step
import org.awaitility.Awaitility.given
import org.awaitility.core.ConditionTimeoutException
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.WebDriverWait
import util.EnvironmentManager.ImplicitlyWaitTimeout
import util.EnvironmentManager.isiOS
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@Step("Find element {by}")
fun AppiumDriver<MobileElement>.findElementWithPolling(by: By, duration: Long = ImplicitlyWaitTimeout): MobileElement? {
    Wait.turnOffImplicitlyWait()
    var element: MobileElement? = null
    try {
        val time = measureTimeMillis {
            var condition = given()
            if (isiOS) condition = condition.pollDelay(400, TimeUnit.MILLISECONDS)
            condition
                .ignoreExceptions()
                .await()
                .atMost(duration, TimeUnit.SECONDS)
                .pollInterval(50, TimeUnit.MILLISECONDS)
                .until {
                    this.findElement(by)?.let { element = it } != null
                }
            if (isiOS) element = this.findElement(by)
        }
        commonUtil.reporting.Report.addMessage("Max duration: $duration sec. and time for finding element [$by] is: ${time / 1000.00} sec.")
    } catch (e: ConditionTimeoutException) {
    } catch (e: WebDriverException) {
        commonUtil.reporting.Report.addMessage(e.localizedMessage)
    } finally {
        Wait.turnOnImplicitlyWait()
    }

    return element
}

@Step("Find element list {by}")
fun AppiumDriver<MobileElement>.findElementListWithPolling(by: By, duration: Long = ImplicitlyWaitTimeout): List<MobileElement> {
    var elements: List<MobileElement> = emptyList()
    try {
        Wait.turnOffImplicitlyWait()
        val time = measureTimeMillis {
            var condition = given()
            if (isiOS) condition = condition.pollDelay(400, TimeUnit.MILLISECONDS)
            condition
                .ignoreExceptions()
                .await()
                .atMost(duration, TimeUnit.SECONDS)
                .pollInterval(50, TimeUnit.MILLISECONDS)
                .until {
                    this.findElements(by).count() > 0
                }
            elements = this.findElements(by)
        }
        commonUtil.reporting.Report.addMessage("Max duration: $duration sec. and time for finding elements [$by] is: ${time / 1000.00} sec.")
    } catch (e: ConditionTimeoutException) {
    } catch (e: WebDriverException) {
        commonUtil.reporting.Report.addMessage(e.localizedMessage)
    } finally {
        Wait.turnOnImplicitlyWait()
    }
    return elements
}

@Step("Find element list without polling {by}")
fun AppiumDriver<MobileElement>.findElementListWithoutPolling(by: By, duration: Long = ImplicitlyWaitTimeout): List<MobileElement> {
    var elements: List<MobileElement> = emptyList()
    try {
        Wait.turnOffImplicitlyWait()
        val wait = WebDriverWait(WebDriverFactory.driver, duration)
        val time = measureTimeMillis {
            wait.until {
                this.findElements(by).let { elements = it; it.count() > 0 }
            }
        }
        commonUtil.reporting.Report.addMessage("Time for finding elements [$by] is: ${time / 1000.00} sec.")
    } finally {
        Wait.turnOnImplicitlyWait()
    }
    return elements
}
