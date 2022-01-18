package robot.upload

import RobotFactory.brandAuthenticationRobot
import RobotFactory.uploadItemRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class BrandAuthenticationRobot : BaseRobot() {

    private val closeButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("authenticity_notice_action_button"),
            iOSBy = VintedBy.accessibilityId("got_it")
        )

    private val navigationTitleTextElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("page_title_brand_authenticity")),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("page_title_brand_authenticity"))
        )

    @Step("Check that brand authentication modal is visible")
    fun checkThatBrandAuthenticationModalIsVisible(): BrandAuthenticationRobot {
        VintedAssert.assertTrue(navigationTitleTextElement.isVisible(), "Brand authentication modal should be visible")
        return brandAuthenticationRobot
    }

    @Step("Close brand authentication modal")
    fun closeBrandAuthenticationModal(): UploadItemRobot {
        closeButton.click()
        return uploadItemRobot
    }
}
