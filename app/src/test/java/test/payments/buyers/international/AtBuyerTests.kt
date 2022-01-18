package test.payments.buyers.international

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

@Feature("Shipping AT buyer tests")
@RunMobile(country = VintedCountry.DE)
@CreateFlexibleAddressUser
class AtBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.AUSTRIA)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test AT buyer from AT with small package size")
    fun testAtBuyerFromAt() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.AT,
            VintedShippingAddress.AT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.AT_AT,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test AT buyer from DE with small package size")
    fun testAtBuyerFromDe() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.AT,
            VintedShippingAddress.DE,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.AT_DE,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test AT buyer from AT with custom shipping")
    fun testAtBuyerFromAt_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.AT,
            VintedShippingAddress.AT,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test AT buyer from AT add/remove bundle items without no shipping item")
    fun testAtBuyerFromAt_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.AT,
            VintedShippingAddress.AT,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }
}
