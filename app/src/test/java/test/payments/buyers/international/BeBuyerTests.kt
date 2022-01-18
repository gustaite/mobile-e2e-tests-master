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
import util.absfeatures.AbTestController.isDhlNlToHome
import util.values.Visibility

@Feature("Shipping BE buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class BeBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.BELGIUM)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test BE buyer from FR with small package size")
    fun testBeBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test BE buyer from BE with small package size")
    fun testBeBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_BE,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from BE with custom shipping")
    fun testBeBuyerFromBe_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from BE with heavy shipping")
    fun testBeBuyerFromBe_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_BE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from BE with no shipping")
    fun testBeBuyerFromBe_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from LU with small package size")
    fun testBeBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_LU,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    // TODO update when dhl_nl_to_home is scaled
    @Test(description = "Test BE buyer from NL with small package size")
    fun testBeBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = if (isDhlNlToHome()) VintedShippingRoutesCarriers.BE_NL_DHL_TO_HOME_AB_TEST else VintedShippingRoutesCarriers.BE_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = if (isDhlNlToHome()) VintedShipmentDeliveryType.HOME else VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test BE buyer from ES with small package size")
    fun testBeBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_ES,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test BE buyer from DE with small package size")
    fun testBeBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.BE_DE,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from IT with small package size")
    fun testBeBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.BE_IT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test BE buyer from PT with small package size")
    fun testBeBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.BE,
            VintedShippingAddress.PT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test BE buyer from BE add/remove bundle items with no shipping item")
    fun testBeBuyerFromBe_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.BE, VintedShippingAddress.BE, isBuyVisible = true)
    }

    @Test(description = "Test BE buyer from BE add/remove bundle items without no shipping item")
    fun testBeBuyerFromBe_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.BE,
            VintedShippingAddress.BE,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test BE buyer from NL add/remove bundle items with no shipping item")
    fun testBeBuyerFromNL_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.BE, VintedShippingAddress.NL, isBuyVisible = false)
    }
}
