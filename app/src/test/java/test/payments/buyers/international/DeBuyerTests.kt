package test.payments.buyers.international

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedShippingAddress
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.data.enums.VintedShippingRoutesCarriers
import commonUtil.testng.CreateFlexibleAddressUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import org.testng.annotations.BeforeMethod
import util.values.Visibility

@Feature("Shipping DE buyer tests")
@RunMobile(country = VintedCountry.DE)
@CreateFlexibleAddressUser
class DeBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.GERMANY)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test DE buyer from DE with small package size")
    fun testDeBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.DE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.DE_DE,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from AT with small package size")
    fun testDeBuyerFromAt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.AT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.DE_AT,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from FR with small package size")
    fun testDeBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.DE_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test DE buyer from NL with small package size")
    fun testDeBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.NL,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.DE_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test DE buyer from IT with small package size")
    fun testDeBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.IT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.DE_IT,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from PT with small package size")
    fun testDeBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.PT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from ES with small package size")
    fun testDeBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.ES,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.DE_ES,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test DE buyer from BE with small package size")
    fun testDeBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.BE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.DE_BE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test DE buyer from DE with custom shipping")
    fun testDeBuyerFromDe_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.DE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from DE with no shipping")
    fun testDeBuyerFromDe_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.DE,
            VintedShippingAddress.DE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test DE buyer from DE with add/remove bundle items with no shipping item")
    fun testDeBuyerFromDe_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.DE, VintedShippingAddress.DE, isBuyVisible = true)
    }

    @Test(description = "Test DE buyer from DE add/remove bundle items without no shipping item")
    fun testDeBuyerFromDe_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.DE,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }
}
