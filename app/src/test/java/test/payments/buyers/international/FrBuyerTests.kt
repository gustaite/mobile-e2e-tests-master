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

@Feature("Shipping FR buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class FrBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.FRANCE)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test FR buyer from ES with small package size")
    fun testFrBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_ES,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from BE with small package size")
    fun testFrBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_BE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from LU with small package size")
    fun testFrBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_LU,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from NL with small package size")
    fun testFrBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from IT with small package size")
    fun testFrBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_IT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from PT with small package size")
    fun testFrBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_PT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from DE with small package size")
    fun testFrBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.DE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_DE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test FR buyer from FR with small package size")
    fun testFrBuyerFromFR() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_FR,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test FR buyer from FR with custom shipping")
    fun testFrBuyerFromFr_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test FR buyer from FR with heavy shipping")
    fun testFrBuyerFromFr_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.FR_FR_HEAVY_LARGE_PACKAGE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test FR buyer from FR with no shipping")
    fun testFrBuyerFromFr_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.FR,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test FR buyer from FR add/remove bundle items with no shipping item")
    fun testFrBuyerFromFr_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.FR, VintedShippingAddress.FR, isBuyVisible = true)
    }

    @Test(description = "Test FR buyer from FR add/remove bundle items without no shipping item")
    fun testFrBuyerFromFr_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.FR,
            VintedShippingAddress.FR,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test FR buyer from LU add/remove bundle items with no shipping item")
    fun testFrBuyerFromLu_addRemoveBundleItems() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.FR, VintedShippingAddress.LU, isBuyVisible = false)
    }
}
