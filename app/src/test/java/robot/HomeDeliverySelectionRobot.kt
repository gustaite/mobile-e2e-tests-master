package robot

import RobotFactory.checkoutRobot
import api.controllers.item.getShipmentOptions
import api.data.models.VintedItem
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.data.enums.VintedShippingRoutesCarriers
import io.qameta.allure.Step
import util.*
import util.absfeatures.AbTestController.isSinglePudoCarrierIsOn
import util.base.BaseTest
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility

class HomeDeliverySelectionRobot : BaseRobot() {

    private val homeDeliveryCarrierElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.scrollableId("item_home_delivery_selection_cell"),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeImage' && (name == 'radioEmpty' || name == 'radioChecked')")
        )

    private val saveSelectedCarrierButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("home_delivery_selection_save"),
            iOSBy = VintedBy.accessibilityId("save_button")
        )

    private fun shipmentCarrierElement(name: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(Android.CELL_BODY_FIELD_ID, name.trim()),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' && name CONTAINS[c] '$name'")
        )
    private val homeDeliverySelectionScreenTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText(
                "actionbar_label",
                Android.getElementValue("home_delivery_selection_screen_title")
            ),
            iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && name == '${IOS.getElementValue("home_delivery_selection_screen_title")}'")
        )

    private val closeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_button"),
            iOSBy = VintedBy.accessibilityId("close")
        )

    @Step("Select home delivery carrier and assert carriers count")
    fun selectHomeDeliveryCarrierAndAssertCarriersCount(expectedHomeDeliveryCarriersCount: Int): CheckoutRobot {
        val actualHomeDeliveryCarriersCount = homeDeliveryCarrierElementList.count()
        actualHomeDeliveryCarriersCount.let { carriersCount ->
            VintedAssert.assertEquals(
                carriersCount, expectedHomeDeliveryCarriersCount,
                "Carriers count should be $expectedHomeDeliveryCarriersCount but is $carriersCount"
            )
        }
        selectRandomHomeDeliveryCarrier()
        return checkoutRobot
    }

    @Step("Assert home delivery selection screen is {visibility}")
    fun assertHomeDeliverySelectionScreenVisibility(visibility: Visibility): CheckoutRobot {
        VintedAssert.assertVisibilityEquals(homeDeliverySelectionScreenTitleElement, visibility, "Home Delivery Selection screen should be $visibility")
        return checkoutRobot
    }

    @Step("Select random home delivery carrier")
    private fun selectRandomHomeDeliveryCarrier() {
        homeDeliveryCarrierElementList.random().click()
        saveSelectedCarrierButton.click()
    }

    @Step("Assert multiple home delivery carriers are visible")
    private fun assertMultipleHomeDeliveryCarriersAreVisible(homeDeliveryCarriers: List<String>, softAssert: VintedSoftAssert): VintedSoftAssert {
        val actualHomeDeliveryCarriersCount = homeDeliveryCarrierElementList.count()
        val visibleCarriersList = mutableListOf<String>()
        homeDeliveryCarriers.forEach { name ->
            val carrier = shipmentCarrierElement(name)
            VintedAssert.assertTrue(carrier.isVisible(), "Expected carrier '$name' should be visible")
            visibleCarriersList.add(carrier.text.lowercase())
        }
        val expectedCarriers = homeDeliveryCarriers.toMutableList()
        expectedCarriers.replaceAll(String::lowercase)
        softAssert.assertEquals(
            visibleCarriersList, expectedCarriers,
            "Shipment carrier provider list should be $expectedCarriers but is $visibleCarriersList"
        )
        softAssert.assertEquals(
            actualHomeDeliveryCarriersCount, expectedCarriers.count(),
            "Home delivery carriers count does not match"
        )
        closeElement.click()
        return softAssert
    }

    @Step("Assert home delivery carriers are visible")
    fun assertHomeDeliveryCarriersAreVisible(item: VintedItem, shippingCarriers: VintedShippingRoutesCarriers): CheckoutRobot {
        // ToDo remove carriers counting logic and check if at least one carrier is visible
        if (!isSinglePudoCarrierIsOn()) {
            val homeDeliveryCarriersFromApi = BaseTest.loggedInUser.getShipmentOptions(item)
                .filter { it.deliveryType == VintedShipmentDeliveryType.HOME }.map { it.title }
            val expectedHomeDeliveryCarriers = shippingCarriers.carriers.filter { it in homeDeliveryCarriersFromApi }
            commonUtil.reporting.Report.addMessage("Filtered home delivery carriers: $expectedHomeDeliveryCarriers")
            val softAssert = VintedSoftAssert()

            softAssert.assertEquals(
                expectedHomeDeliveryCarriers.count(), homeDeliveryCarriersFromApi.count(),
                "Home delivery carriers count does not match. Expected carriers: $expectedHomeDeliveryCarriers and actual from " +
                    "API: $homeDeliveryCarriersFromApi. Values might differ in VintedShippingRoutesCarriers.kt carriers List"
            )
            if (expectedHomeDeliveryCarriers.count() > 1) {
                assertMultipleHomeDeliveryCarriersAreVisible(expectedHomeDeliveryCarriers, softAssert)
            } else {
                checkoutRobot.assertHomeDeliveryShippingCarrierProviderIsVisible(
                    expectedHomeDeliveryCarriers.first(),
                    softAssert
                )
            }
            softAssert.assertAll()
        } else {
            if (homeDeliverySelectionScreenTitleElement.withWait().isVisible()) closeElement.click()
        }
        return checkoutRobot
    }
}
