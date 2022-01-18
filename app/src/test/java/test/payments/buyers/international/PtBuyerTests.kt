package test.payments.buyers.international

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedShippingRoutesCarriers
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.CreateFlexibleAddressUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@Feature("Shipping PT buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class PtBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.PORTUGAL)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test PT buyer from FR with small package size")
    fun testPtBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.PT_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test PT buyer from BE with small package size")
    fun testPtBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.BE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from LU with small package size")
    fun testPtBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.LU,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from NL with small package size")
    fun testPtBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.NL,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from DE with small package size")
    fun testPtBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from IT with small package size")
    fun testPtBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.IT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Issue("ZEBRA-976")
    @Test(description = "Test PT buyer from ES with small package size")
    fun testPtBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.ES,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from PT with small package size")
    fun testPtBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.PT_PT,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from PT with heavy shipping")
    fun testPtBuyerFromPt_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.PT_PT,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from PT with custom shipping")
    fun testPtBuyerFromPt_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from PT with no shipping")
    fun testPtBuyerFromPt_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PT,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PT buyer from PT add/remove bundle items with no shipping item")
    fun testPtBuyerFromPt_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.PT, VintedShippingAddress.PT, isBuyVisible = true)
    }

    @Test(description = "Test PT buyer from PT add/remove bundle items without no shipping item")
    fun testPtBuyerFromPt_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.PT,
            VintedShippingAddress.PT,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test PT buyer from FR add/remove bundle items with no shipping item")
    fun testPtBuyerFromFr_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.PT, VintedShippingAddress.FR, isBuyVisible = false)
    }
}
