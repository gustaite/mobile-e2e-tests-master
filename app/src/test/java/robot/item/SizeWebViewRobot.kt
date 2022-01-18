package robot.item

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class SizeWebViewRobot : BaseRobot() {

    private val sizeWebViewElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.className("android.webkit.WebView"),
            iOSBy = VintedBy.className("XCUIElementTypeWebView")
        )

    @Step("Assert sizes web view is visible")
    fun assertSizesWebViewIsVisible() {
        VintedAssert.assertTrue(sizeWebViewElement.isVisible(15), "Sizes web view should be visible")
    }
}
