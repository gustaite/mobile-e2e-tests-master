package robot.inbox.conversation

import RobotFactory.parcelSizeRobot
import api.controllers.GlobalAPI
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ShippingOptionsEducationRobot : BaseRobot() {

    private fun shippingOptionEducationTitleElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText("shipping_option_title", text),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name == '$text'")
        )

    private val submitButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("cancel"))

    @Step("Assert carriers and prices are visible for expected package size")
    fun assertCarriersAndPricesInfoForPackageSizeIsVisible(packageSizeId: Long): ShippingOptionsEducationRobot {
        val shippingOptionsList = GlobalAPI.getPackageSizeShipmentOptions(user = loggedInUser, packageSizeId = packageSizeId)
        commonUtil.reporting.Report.addMessage("Shipping options list: $shippingOptionsList, package sizeId = $packageSizeId")
        VintedAssert.assertTrue(shippingOptionsList.isNotEmpty(), "Shipping options list should not be empty")
        shippingOptionsList.forEach {
            val shippingOption = shippingOptionEducationTitleElement("${it.title}")
            VintedAssert.assertTrue(shippingOption.isVisible(), "Expected shipping option ${it.title} is not visible")
        }
        return this
    }

    @Step("Go back to parcel size selection form")
    fun goBackToParcelSizeSelectionForm(): ParcelSizeRobot {
        if (isAndroid) {
            clickBack()
        } else {
            submitButtonIos.click()
        }
        return parcelSizeRobot
    }
}
