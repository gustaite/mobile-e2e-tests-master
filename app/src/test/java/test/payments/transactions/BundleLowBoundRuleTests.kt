package test.payments.transactions

import RobotFactory.conversationRobot
import RobotFactory.deepLink
import api.controllers.item.*
import api.controllers.user.bundleApi
import api.data.models.VintedItem
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import api.controllers.user.conversationApi
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.LoginToNewUser
import commonUtil.thread
import commonUtil.testng.CreateOneTestUser
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility

@LoginToNewUser
@CreateOneTestUser
@RunMobile(country = VintedCountry.INT)
@Feature("Bundle low bound rule tests") // (FS: https://admin.vinted.net/features/846)
class BundleLowBoundRuleTests : BaseTest() {
    private var smallSizeItem: VintedItem? by thread.lateinit()
    private var mediumSizeItem: VintedItem? by thread.lateinit()
    private var largeSizeItem: VintedItem? by thread.lateinit()

    @BeforeMethod(description = "Create S,M,L size items for logged in users")
    fun createItemsForLoggedInUser() {
        smallSizeItem = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM,
            price = "5"
        )

        mediumSizeItem = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM_MEDIUM,
            price = "6"
        )

        largeSizeItem = ItemAPI.uploadItem(
            itemOwner = loggedInUser,
            type = ItemRequestBuilder.VintedType.SIMPLE_ITEM_LARGE,
            price = "7"
        )
    }

    @Test(description = "Check bundle low bound rule")
    fun testBundleLowBoundRule() {
        val transaction = oneTestUser.bundleApi.createBundleWithItems(smallSizeItem!!, mediumSizeItem!!)
        loggedInUser.conversationApi.getFirstConversation()

        deepLink.conversation
            .goToConversation(transaction.conversationId)
            .assertMarkAsReservedButtonVisibility(visibility = Visibility.Visible)
            .openParcelSizeSelectionForm()
            .assertParcelSizeIsNotVisible(SMALL_PACKAGE_ID)
            .assertExpectedParcelSizesAreVisible(transaction.id)
            .clickOnMeasurementsAndPricesLink()
            .assertCarriersAndPricesInfoForPackageSizeIsVisible(MEDIUM_PACKAGE_ID)
            .goBackToParcelSizeSelectionForm()
            .selectParcelSizeAndSubmit()

        oneTestUser.bundleApi.updateBundle(transaction.id, listOf(smallSizeItem!!.id, mediumSizeItem!!.id, largeSizeItem!!.id))

        conversationRobot
            .openParcelSizeSelectionForm()
            .assertParcelSizeIsNotVisible(SMALL_PACKAGE_ID)
            .assertParcelSizeIsNotVisible(MEDIUM_PACKAGE_ID)
            .assertExpectedParcelSizesAreVisible(transaction.id)
            .clickOnMeasurementsAndPricesLink()
            .assertCarriersAndPricesInfoForPackageSizeIsVisible(LARGE_PACKAGE_ID)
    }
}
