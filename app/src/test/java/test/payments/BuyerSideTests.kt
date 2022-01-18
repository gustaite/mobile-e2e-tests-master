package test.payments

import RobotFactory.deepLink
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.data.models.VintedItem
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import io.qameta.allure.TmsLink

@RunMobile(
    env = VintedEnvironment.PRODUCTION, country = VintedCountry.PAYMENTS_EXCEPT_US,
    message = "Test for payments countries with Drop Off Points"
)
@LoginToNewUser
@CreateOneTestUser
@Feature("Buyer side tests")
class BuyerSideTests : BaseTest() {
    private var oneTestUserItem: VintedItem? by thread.lateinit()

    @BeforeMethod
    fun createItemForOneTestUser() {
        oneTestUserItem = ItemAPI.uploadItem(
            itemOwner = oneTestUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "25"
        )
    }

    @Issue("SPELL-1027")
    @Test(description = "Test if Pick-up Points search by 'Search This Area' button returns results when address was not added")
    @TmsLink("27901")
    fun testPickUpPointsSearchBySearchThisAreaButtonWhenAddressWasNotAdded() {
        deepLink
            .item.goToItem(oneTestUserItem!!)
            .clickBuyButton()
            .assertAddressSectionIsEmpty()
            .selectPickUpShipping()
            .assertDropOffPointsAreFoundBySearchThisAreaButton()
            .selectDropOffPointFromList()
            .confirmSelectedParcelShop()
            .assertSelectedDropOffPointIsVisibleInCheckout()
    }
}
