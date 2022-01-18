package robot.inbox.conversation

import RobotFactory.conversationRobot
import RobotFactory.shippingOptionsEducationRobot
import api.controllers.GlobalAPI
import api.controllers.user.shipmentApi
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.ActionBarRobot
import robot.BaseRobot
import util.Android
import util.IOS
import util.base.BaseTest.Companion.loggedInUser
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class ParcelSizeRobot : BaseRobot() {
    private val actionBarRobot: ActionBarRobot = ActionBarRobot()
    private val parcelSizeFirstElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("item_packaging_standard"),
            iOSBy = VintedBy.accessibilityId("package_size_cell_0")
        )

    private val parcelSizeCellElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.scrollableId("view_cell_body_container"),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther/XCUIElementTypeOther")
        )

    private val noShippingParcelSizeCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("view_cell_title", "common.package_type.bundles.no_shipping")

    private fun parcelSizeTitleElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.androidIdAndText("title", text),
                    androidBy2 = VintedBy.androidIdAndText("item_packaging_title", text)
                )
            },
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name == '$text'")
        )

    private val measurementsAndPricesLinkElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("standard_packaging_label"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("shipping_options_see_education"))
        )

    @Step("Select parcel size and submit")
    fun selectParcelSizeAndSubmit(): ConversationRobot {
        parcelSizeFirstElement.click()
        actionBarRobot.submitInParcelSizeScreen()
        return conversationRobot
    }

    @Step("Assert expected parcel size is not visible because of low bound rule")
    fun assertParcelSizeIsNotVisible(packageSizeId: Long): ParcelSizeRobot {
        val invisibleParcelSize = GlobalAPI.getPackageSizes(user = loggedInUser).first { it.id == packageSizeId }.title

        VintedAssert.assertTrue(parcelSizeTitleElement(invisibleParcelSize).isInvisible(), "$invisibleParcelSize package size should not be visible")
        return this
    }

    @Step("Assert expected parcel sizes are visible")
    fun assertExpectedParcelSizesAreVisible(transactionId: Long): ParcelSizeRobot {
        val parcelSizesList = loggedInUser.shipmentApi.getPackageSizesForTransaction(transactionId).map { it.title }

        parcelSizesList.dropLast(1).forEach { parcelSizeName ->
            VintedAssert.assertTrue(parcelSizeTitleElement(parcelSizeName).isVisible(), "Parcel size $parcelSizeName is not visible")
            VintedAssert.assertEquals(parcelSizeTitleElement(parcelSizeName).text, parcelSizeName, "Parcel size value should match expected $parcelSizeName")
        }
        VintedAssert.assertTrue(noShippingParcelSizeCellElement.isVisible(), "No shipping parcel size element is not visible")
        VintedAssert.assertEquals(parcelSizeCellElementList.count(), parcelSizesList.count(), "Parcel sizes count does not match expected")
        return this
    }

    @Step("Assert measurements and prices link opens package education screen")
    fun clickOnMeasurementsAndPricesLink(): ShippingOptionsEducationRobot {
        parcelSizeFirstElement.click()
        measurementsAndPricesLinkElement.click()
        return shippingOptionsEducationRobot
    }
}
