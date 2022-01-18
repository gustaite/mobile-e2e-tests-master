package robot.upload

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.workflow.UploadFormWorkflowRobot
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage

class UploadB2cItemRobot : BaseRobot() {

    private val b2cCatalogValidationErrorElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "view_notification_text",
                ElementByLanguage.vintedProCatalogUploadErrorText
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("error"))
        )

    private val b2cCatalogErrorCloseButtonElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("close"))
        )

    @Step("Assert b2c upload to beauty catalog error is visible")
    fun assertB2cUploadToBeautyCategoryErrorIsVisible(): UploadB2cItemRobot {
        VintedAssert.assertTrue(b2cCatalogValidationErrorElement.withWait().isVisible(), "B2C upload to beauty category validation error should be visible")
        return this
    }

    @Step("iOS only: Close b2c upload to beauty catalog error")
    fun closeB2cUploadToBeautyCategoryErrorIos(): UploadFormWorkflowRobot {
        IOS.doIfiOS { b2cCatalogErrorCloseButtonElementIos.click() }
        return UploadFormWorkflowRobot()
    }
}
