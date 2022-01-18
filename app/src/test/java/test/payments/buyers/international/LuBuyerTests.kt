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

@Feature("Shipping LU buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class LuBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.LUXEMBOURG)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test LU buyer from FR with small package size")
    fun testLuBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from BE with small package size")
    fun testLuBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from LU with small package size")
    fun testLuBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from NL with small package size")
    fun testLuBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from ES with small package size")
    fun testLuBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from LU with custom shipping")
    fun testLuBuyerFromLu_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test LU buyer from LU with heavy shipping")
    fun testLuBuyerFromLu_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.LU_FR,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test LU buyer from LU with no shipping")
    fun testLuBuyerFromLu_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.LU,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test LU buyer from LU add/remove bundle items with no shipping item")
    fun testLuBuyerFromLu_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.LU, VintedShippingAddress.LU, isBuyVisible = true)
    }

    @Test(description = "Test LU buyer from LU add/remove bundle items without no shipping item")
    fun testLuBuyerFromLu_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.LU,
            VintedShippingAddress.LU,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test LU buyer from ES add/remove bundle items with no shipping item")
    fun testLuBuyerFromEs_addRemoveBundleItems_withNoShippingItems() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.LU, VintedShippingAddress.ES, isBuyVisible = false)
    }
}
