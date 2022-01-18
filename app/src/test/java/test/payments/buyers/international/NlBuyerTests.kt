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

@Feature("Shipping NL buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class NlBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.NETHERLANDS)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test NL buyer from FR with small package size")
    fun testNlBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.NL_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test NL buyer from BE with small package size")
    fun testNlBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.NL_BE,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from LU with small package size")
    fun testNlBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.LU,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from ES with small package size")
    fun testNlBuyerFromEs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.NL_ES,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from DE with small package size")
    fun testNlBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.NL_DE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test NL buyer from IT with small package size")
    fun testNlBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.NL_IT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test NL buyer from PT with small package size")
    fun testNlBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.PT,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from NL with small package size")
    fun testNlBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = if (isDhlNlToHome()) VintedShippingRoutesCarriers.NL_NL_DHL_TO_HOME_AB_TEST else VintedShippingRoutesCarriers.NL_NL,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from NL with custom shipping")
    fun testNlBuyerFromNl_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from NL with heavy shipping")
    fun testNlBuyerFromNl_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = if (isDhlNlToHome()) VintedShippingRoutesCarriers.NL_NL_HEAVY_LARGE_PACKAGE_DHL_TO_HOME_AB_TEST else VintedShippingRoutesCarriers.NL_NL_HEAVY_LARGE_PACKAGE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from NL with no shipping")
    fun testNlBuyerFromNl_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.NL,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test NL buyer from NL with add/remove bundle items with no shipping item")
    fun testNlBuyerFromNl_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.NL, VintedShippingAddress.NL, isBuyVisible = true)
    }

    @Test(description = "Test NL buyer from NL add/remove bundle items without no shipping item")
    fun testNlBuyerFromNl_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.NL,
            VintedShippingAddress.NL,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test NL buyer from BE add/remove bundle items with no shipping item")
    fun testNlBuyerFromBe_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.NL, VintedShippingAddress.BE, isBuyVisible = false)
    }
}
