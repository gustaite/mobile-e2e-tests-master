package util.driver

import commonUtil.extensions.removeSpecialSpaceAndMinus
import io.qameta.allure.Step
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*
import util.EnvironmentManager
import java.util.concurrent.TimeUnit

class Wait {
    companion object {

        private fun waitForCondition(expectedCondition: () -> ExpectedCondition<*>, waitSec: Long = 5) {
            val wait = WebDriverWait(WebDriverFactory.driver, waitSec)
            EnvironmentManager.ImplicitlyWaitTimeout = waitSec
            try {
                turnOffImplicitlyWait()
                wait.until(expectedCondition())
            } catch (e: Exception) {
            } finally {
                EnvironmentManager.ImplicitlyWaitTimeout = 10
            }
        }

        @Step("Wait for element text to change")
        fun forElementTextToChange(element: VintedElement, text: String) {
            waitForCondition({ ExpectedCondition { element.mobileElement.text?.removeSpecialSpaceAndMinus() != text.removeSpecialSpaceAndMinus() } })
        }

        @Step("Wait for element text to match {text}")
        fun forElementTextToMatch(element: VintedElement, text: String) {
            waitForCondition({ ExpectedCondition { element.mobileElement.text?.removeSpecialSpaceAndMinus() == text.removeSpecialSpaceAndMinus() } })
        }

        @Step("Wait for element to have text")
        fun forElementToHaveText(element: VintedElement, waitSec: Long = 2) {
            waitForCondition({ ExpectedCondition { element.mobileElement.text?.isNotEmpty() } }, waitSec)
        }

        @Step("Wait for element to be clickable for {waitSec}")
        fun forElementToBeClickable(element: VintedElement, waitSec: Long = 5) {
            waitForCondition({ ExpectedConditions.elementToBeClickable(element.mobileElement) }, waitSec)
        }

        @Step("Wait for element to be visible for {waitSec}")
        fun forElementToBeVisible(element: VintedElement, waitSec: Long = 5) {
            waitForCondition({ ExpectedConditions.visibilityOf(element.mobileElement) }, waitSec)
        }

        @Step("Wait for element to disappear for {waitSec}")
        fun forElementToDisappear(element: VintedElement, waitSec: Long = 5) {
            waitForCondition({ ExpectedConditions.invisibilityOf(element.mobileElement) }, waitSec)
        }

        fun waitForElementCount(byLocator: By?, count: Int) {
            waitForCondition({ ExpectedConditions.numberOfElementsToBe(byLocator, count) }, 3)
        }

        @Step("Wait for alert to be visible")
        fun waitForAlert() {
            waitForCondition({ ExpectedConditions.alertIsPresent() }, waitSec = 1)
        }

        fun turnOffImplicitlyWait() {
            WebDriverFactory.driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS)
        }

        fun turnOnImplicitlyWait() {
            WebDriverFactory.driver.manage().timeouts().implicitlyWait(EnvironmentManager.ImplicitlyWaitTimeout, TimeUnit.SECONDS)
        }
    }
}

enum class WaitFor {
    Click,
    Visible
}
