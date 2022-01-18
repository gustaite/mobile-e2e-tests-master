package robot.upload.photo

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.EnvironmentManager.isAndroid
import util.VintedDriver
import util.assertVisibilityEquals
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class WebPhotoRobot : BaseRobot() {

    private val webPhotoWarningElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("view_item_form_web_photo_banner"),
            iOSBy = VintedBy.accessibilityId("web_photo_warning")
        )
    private val webPhotoWarningAcceptButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_upload_web_photo_warning_button"),
            iOSBy = VintedBy.accessibilityId("web_photo_warning_accept_button")
        )

    @Step("Assert that web photo warning is {visibility}")
    fun assertWebPhotoWarningVisibility(visibility: Visibility): WebPhotoRobot {
        VintedAssert.assertVisibilityEquals(webPhotoWarningElement, visibility, "Web photo warning expected to be with visibility value $visibility")
        return this
    }

    @Step("Click on web photo warning")
    fun clickOnWebPhotoWarning(): WebPhotoRobot {
        val yOnce = if (isAndroid) -80 else -18
        webPhotoWarningElement.tapRightBottomCorner(10, yOnce) { webPhotoWarningElement.isInvisible() }
        return this
    }

    @Step("Assert web photo warning accept button is {visibility}")
    fun assertWebPhotoWarningAcceptButtonVisibility(visibility: Visibility): WebPhotoRobot {
        VintedDriver.scrollDownABit()
        VintedAssert.assertVisibilityEquals(webPhotoWarningAcceptButtonElement, visibility, "Web photo warning accept button is expected to be with visibility value $visibility")
        return this
    }

    @Step("Click on web photo warning accept button")
    fun clickOnWebPhotoWarningAcceptButton(): WebPhotoRobot {
        VintedDriver.scrollDownABit()
        webPhotoWarningAcceptButtonElement.click()
        return this
    }
}
