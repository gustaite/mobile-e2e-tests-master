package robot

import io.qameta.allure.Step
import util.IOS
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.Wait

class IOSPermissionRobot : BaseRobot() {
    private val permissionAlertAllowElement: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("Allow"))
    private val permissionAlertOpenElement: VintedElement
        get() = VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("name == 'Open' || name == 'Ouvrir' || name == 'Öffnen' || name == 'Otwórz' || name == 'Otevřít'"))
    private val permissionAlertOkElement: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("OK"))

    @Step("Allow permission on iOS")
    fun allowPermission(): BaseRobot {
        IOS.doIfiOS { permissionAlertAllowElement.click() }
        return this
    }

    @Step("Click on permission alert 'OK' button")
    fun acceptPermissionPopup() {
        IOS.doIfiOS {
            permissionAlertOkElement.click()
            if (permissionAlertOkElement.isVisible(1)) {
                commonUtil.reporting.Report.addMessage("Click again OK button")
                permissionAlertOkElement.click()
            }
        }
    }

    @Step("iOS Only: Handle open URL via Vinted app permission")
    fun handleOpenUrlPermission() {
        IOS.doIfiOS {
            commonUtil.Util.retryUntil(
                block = {
                    clickOnPermissionIfExists { permissionAlertOpenElement.tap() }
                    !permissionAlertAllowElement.isVisible(1)
                },
                tryForSeconds = 10
            )
        }
    }

    private fun clickOnPermissionIfExists(block: () -> Unit) {
        IOS.doIfiOS {
            try {
                Wait.turnOffImplicitlyWait()
                block()
            } catch (e: Exception) {
            } finally {
                Wait.turnOnImplicitlyWait()
            }
        }
    }
}
