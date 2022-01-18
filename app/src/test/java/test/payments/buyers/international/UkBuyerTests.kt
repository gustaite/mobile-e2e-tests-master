package test.payments.buyers.international

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
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
import util.absfeatures.AbTestController.isHermesUkPostableOn
import util.absfeatures.AbTestController.isYodelUkOn
import util.absfeatures.ShippingCarriersController.getShippingRoutesCarriersBasedOnFeatureFlag
import util.values.Visibility

@Feature("Shipping UK buyer tests")
@RunMobile(country = VintedCountry.UK)
@CreateFlexibleAddressUser
class UkBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.UNITED_KINGDOM)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test UK buyer from UK with small package size")
    fun testUkBuyerFromUk() {
        val carriersListBasedOnAbTests = listOf(
            Pair(VintedShippingRoutesCarriers.UK_UK_HERMES_POSTABLE_AB_TEST, isHermesUkPostableOn()),
            Pair(VintedShippingRoutesCarriers.UK_UK_YODEL_AB_TEST, isYodelUkOn())
        )

        val ukToUKShippingCarriers =
            getShippingRoutesCarriersBasedOnFeatureFlag(carriersListBasedOnAbTests, VintedShippingRoutesCarriers.UK_UK) as VintedShippingRoutesCarriers

        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.UK,
            VintedShippingAddress.UK,
            isBuyVisible = true,
            shippingCarriers = ukToUKShippingCarriers,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test UK buyer from UK with heavy shipping")
    fun testUKBuyerFromUK_HeavyShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.UK,
            VintedShippingAddress.UK,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.UK_UK_HEAVY_LARGE_PACKAGE,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test UK buyer from UK with custom shipping")
    fun testUkBuyerFromUk_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.UK,
            VintedShippingAddress.UK,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test UK buyer from UK with no shipping")
    fun testUkBuyerFromUk_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.UK,
            VintedShippingAddress.UK,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test UK buyer from UK with add/remove bundle items with no shipping item")
    fun testUkBuyerFromUk_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.UK, VintedShippingAddress.UK, isBuyVisible = true)
    }

    @Test(description = "Test UK buyer from UK add/remove bundle items without no shipping item")
    fun testUkBuyerFromUk_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.UK,
            VintedShippingAddress.UK,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }
}
