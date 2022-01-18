package test.payments.buyers.international

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedShippingRoutesCarriers
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.CreateFlexibleAddressUser
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@Feature("Shipping IT buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class ItBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.ITALY)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test IT buyer from FR with small package size")
    fun testItBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from BE with small package size")
    fun testItBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_BE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from DE with small package size")
    fun testItBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.IT_DE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from ES with small package size")
    fun testItBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_ES,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from NL with small package size")
    fun testItBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from PT with small package size")
    fun testItBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.PT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test IT buyer from IT with small package size")
    fun testItBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_IT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from IT with custom shipping")
    fun testItBuyerFromIt_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test IT buyer from IT with heavy shipping")
    fun testItBuyerFromIt_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.IT_IT_HEAVY_LARGE_PACKAGE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test IT buyer from IT with no shipping")
    fun testItBuyerFromIt_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.IT,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test IT buyer from IT add/remove bundle items with no shipping item")
    fun testItBuyerFromIt_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.IT, VintedShippingAddress.IT, isBuyVisible = true)
    }

    @Test(description = "Test IT buyer from IT add/remove bundle items without no shipping item")
    fun testItBuyerFromIt_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.IT,
            VintedShippingAddress.IT,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test IT buyer from FR add/remove bundle items with no shipping item")
    fun testItBuyerFromFr_addRemoveBundleItems_withNoShippingItems() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.IT, VintedShippingAddress.FR, isBuyVisible = false)
    }
}
