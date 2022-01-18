package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.webview.WebViewRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class VintedGuideRobot : BaseRobot() {

    private val vintedGuideTitleElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("actionbar_label", "user_menu_vinted_guide")

    private val sectionElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("faq_cell"),
            VintedBy.className("XCUIElementTypeCell")
        )

    @Step("Check if Vinted guide screen is visible")
    fun assertVintedGuideIsDisplayed(): VintedGuideRobot {
        VintedAssert.assertTrue(vintedGuideTitleElement.isVisible(15), "Vinted guide title should be visible")
        return this
    }

    @Step("Open random Vinted guide section")
    fun openVintedGuideSection(): WebViewRobot {
        while (VintedElement.isListVisible({ sectionElementList })) {
            sectionElementList.random().click()
        }
        return WebViewRobot()
    }
}
