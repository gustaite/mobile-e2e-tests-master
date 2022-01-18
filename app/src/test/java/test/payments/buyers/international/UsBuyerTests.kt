package test.payments.buyers.international

import RobotFactory.deepLink
import RobotFactory.shipmentWorkflowRobot
import api.controllers.item.ItemRequestBuilder
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedShippingRoutesCarriers
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.CreateFlexibleAddressUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@Feature("Shipping US buyer tests")
@RunMobile(country = VintedCountry.US)
@CreateFlexibleAddressUser
class UsBuyerTests : BaseTest() {

    @BeforeMethod(description = "Create user and update shipping address")
    fun createUserAndUpdateShippingAddress() {
        shipmentWorkflowRobot.createUserUpdateShippingAddressAndSetLanguage(country = VintedCountries.USA)
    }

    @BeforeMethod(description = "Login into user")
    fun loginToUserOnApp() {
        deepLink.loginToAccount(loggedInUser)
    }

    @Test(description = "Test US buyer from US with small package size")
    fun testUsBuyerFromUs() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.US,
            VintedShippingAddress.US,
            isBuyVisible = true,
            shippingCarriers = VintedShippingRoutesCarriers.US_US,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test US buyer from US with no shipping")
    fun testUsBuyerFromUs_NoShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.US,
            VintedShippingAddress.US,
            isBuyVisible = false, // Buy button was restricted on US for CUSTOM and NO_SHIPPING https://github.com/vinted/core/pull/57060
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test US buyer from US with custom shipping")
    fun testUsBuyerFromUs_CustomShipping() {
        shipmentWorkflowRobot.buyerFromOneCountrySellerFromAnother(
            VintedShippingAddress.US,
            VintedShippingAddress.US,
            isBuyVisible = false, // Buy button was restricted on US for CUSTOM and NO_SHIPPING https://github.com/vinted/core/pull/57060
            shippingCarriers = VintedShippingRoutesCarriers.EMPTY,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING,
            bundleVisibility = Visibility.Visible
        )
    }

    @Test(description = "Test US buyer from US with add/remove bundle items with no shipping item")
    fun tesUsBuyerFromUs_addRemoveBundleItems_withNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(VintedShippingAddress.US, VintedShippingAddress.US, isBuyVisible = false)
        // Buy button was restricted on US for CUSTOM and NO_SHIPPING https://github.com/vinted/core/pull/57060
    }

    @Test(description = "Test US buyer from US add/remove bundle items without no shipping item")
    fun testUsBuyerFromUs_addRemoveBundleItems_withoutNoShippingItem() {
        shipmentWorkflowRobot.buyerAddRemoveBundleItems(
            VintedShippingAddress.US,
            VintedShippingAddress.US,
            isBuyVisible = false,
            itemType = ItemRequestBuilder.VintedType.SIMPLE_ITEM
        )
    }
}
