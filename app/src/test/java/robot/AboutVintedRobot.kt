package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.webview.WebViewRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class AboutVintedRobot : BaseRobot() {

    private val aboutVintedCellElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("item_button"),
            VintedBy.className("XCUIElementTypeCell")
        )

    @Step("Open random cell from about vinted screen")
    fun openRandomCell(): WebViewRobot {
        IOS.doIfiOS {
            VintedAssert.assertTrue(VintedElement.isListVisible({ aboutVintedCellElementList }), "AboutVintedCellElementList should be visible")
        }
        aboutVintedCellElementList.take(5).random().click()
        return WebViewRobot()
    }
}
