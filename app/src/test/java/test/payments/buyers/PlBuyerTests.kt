package test.payments.buyers

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
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

@Feature("Shipping PL buyer tests")
@RunMobile(country = VintedCountry.PL)
@CreateFlexibleAddressUser
class PlBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.POLAND)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test PL buyer from PL with small package size")
    fun testPlBuyerFromPl() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PL,
            VintedShippingAddress.PL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.PL_PL,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PL buyer from PL with custom shipping")
    fun testPlBuyerFromPl_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PL,
            VintedShippingAddress.PL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PL buyer from PL with heavy shipping")
    fun testPlBuyerFromPl_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PL,
            VintedShippingAddress.PL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.PL_PL_HEAVY_LARGE_PACKAGE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PL buyer from PL with no shipping")
    fun testPlBuyerFromPl_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.PL,
            VintedShippingAddress.PL,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test PL buyer from PL with add/remove bundle items with no shipping item")
    fun testPlBuyerFromPl_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.PL, VintedShippingAddress.PL, isBuyVisible = true)
    }

    @Test(description = "Test PL buyer from PL add/remove bundle items without no shipping item")
    fun testPlBuyerFromPl_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.PL,
            VintedShippingAddress.PL,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }
}
