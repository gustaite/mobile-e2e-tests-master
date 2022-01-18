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
import util.absfeatures.AbTestController.isSeurHomeEsOn
import util.absfeatures.AbTestController.isSeurShopEsOn
import util.absfeatures.ShippingCarriersController
import util.values.Visibility

@Feature("Shipping ES buyer tests")
@RunMobile(country = VintedCountry.INT)
@CreateFlexibleAddressUser
class EsBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.SPAIN)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test ES buyer from FR with small package size")
    fun testEsBuyerFromFr() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.FR,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_FR,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from BE with small package size")
    fun testEsBuyerFromBe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.BE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_BE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from LU with small package size")
    fun testEsBuyerFromLu() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.LU,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_LU,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from NL with small package size")
    fun testEsBuyerFromNl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.NL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_NL,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from DE with small package size")
    fun testEsBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.DE,
            isBuyVisible = false,
            shippingCarriers = VintedShippingRoutesCarriers.ES_DE,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from IT with small package size")
    fun testEsBuyerFromIt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.IT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_IT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from PT with small package size")
    fun testEsBuyerFromPt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.PT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.ES_PT,
            bundleVisibility = Visibility.Visible,
            deliveryType = VintedShipmentDeliveryType.PICK_UP
        )
    }

    @Test(description = "Test ES buyer from ES with small package size")
    fun testEsBuyerFromEs() {
        val carriersListBasedOnFeatures = listOf(
            Pair(VintedShippingRoutesCarriers.ES_ES_SEUR_HOME_FEATURE, isSeurHomeEsOn()),
            Pair(VintedShippingRoutesCarriers.ES_ES_SEUR_SHOP_FEATURE, isSeurShopEsOn())
        )

        val esToEsShippingCarriers =
            ShippingCarriersController.getShippingRoutesCarriersBasedOnFeatureFlag(carriersListBasedOnFeatures, VintedShippingRoutesCarriers.ES_ES) as VintedShippingRoutesCarriers

        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = esToEsShippingCarriers,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test ES buyer from ES with custom shipping")
    fun testEsBuyerFromEs_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test ES buyer from ES with heavy shipping")
    fun testEsBuyerFromEs_HeavyShipping() {
        val carriersListBasedOnFeatures = listOf(
            Pair(VintedShippingRoutesCarriers.ES_ES_HEAVY_LARGE_PACKAGE_SEUR_HOME_FEATURE, isSeurHomeEsOn()),
            Pair(VintedShippingRoutesCarriers.ES_ES_HEAVY_LARGE_PACKAGE_SEUR_SHOP_FEATURE, isSeurShopEsOn())
        )

        val esToEsHeavyLargeShippingCarriers =
            ShippingCarriersController.getShippingRoutesCarriersBasedOnFeatureFlag(carriersListBasedOnFeatures, VintedShippingRoutesCarriers.ES_ES_HEAVY_LARGE_PACKAGE) as VintedShippingRoutesCarriers

        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = esToEsHeavyLargeShippingCarriers,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test ES buyer from ES with no shipping")
    fun testEsBuyerFromEs_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.ES,
            VintedShippingAddress.ES,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test ES buyer from ES add/remove bundle items with no shipping item")
    fun testEsBuyerFromEs_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.ES, VintedShippingAddress.ES, isBuyVisible = true)
    }

    @Test(description = "Test ES buyer from ES add/remove bundle items without no shipping item")
    fun testEsBuyerFromEs_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.ES,
            VintedShippingAddress.ES,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }

    @Test(description = "Test ES buyer from FR add/remove bundle items with no shipping item")
    fun testEsBuyerFromFr_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.ES, VintedShippingAddress.FR, isBuyVisible = false)
    }
}
