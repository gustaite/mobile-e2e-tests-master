package test.basic.item

import RobotFactory.deepLink
import RobotFactory.itemRobot
import RobotFactory.shippingOptionRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileRobot
import api.controllers.GlobalAPI
import api.controllers.item.*
import api.data.models.VintedItem
import commonUtil.data.Price
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.thread
import io.qameta.allure.Feature
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import robot.item.ItemActions
import robot.upload.ShippingOptionTypes
import commonUtil.testng.config.VintedCountry.INT
import commonUtil.testng.config.VintedCountry.PAYMENTS
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.IOS
import util.base.BaseTest

@RunMobile
@Feature("Edit item tests")
@LoginToMainThreadUser
class EditItemTests : BaseTest() {
    private var item: VintedItem? by thread.lateinit()

    @BeforeMethod(description = "Delete user items")
    fun deleteUserItems() {
        loggedInUser.deleteAllItems()
    }

    @Test(description = "Edit item price")
    @TmsLink("25671")
    fun testEditItemPrice() {
        val price = Price.getRandomPriceInRange()
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        deepLink.item.goToItem(item!!)

        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.EDIT)

        uploadItemRobot
            .openSellingPriceSection()
            .enterPriceAndSubmit(price)
            .assertPrice(price)
            .clickSaveAndWait()

        itemRobot.assertPrice(price)

        deepLink.profile.goToMyProfile()
        userProfileRobot.closetScreen
            .assertItemsIsVisibleInCloset(1, price)
    }

    @RunMobile(country = PAYMENTS, message = "Test only for payment countries")
    @Test(description = "Edit shipping options. Only for payments countries")
    @TmsLink("25669")
    fun testEditShippingOptions() {
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        deepLink.item.goToItem(item!!)
        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.EDIT)
        val title = uploadItemRobot
            .openShippingOptionsSection()
            .selectShippingOptionAndReturnTitle(2, ShippingOptionTypes.STANDARD)
        shippingOptionRobot.clickSubmitInParcelSizeScreen()

        uploadItemRobot
            .assertShippingOption(title)
            .clickSaveAndWait()

        deepLink.item.goToItem(item!!)
        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.EDIT)

        IOS.scrollDown()
        uploadItemRobot.assertShippingOption(title)
    }

    @RunMobile(country = INT, message = "Test for international platform. Others do not have heavy shipping")
    @Test(description = "Edit item, select heavy shipping (5, 10 or 20 kg), save it, check if it is saved")
    @TmsLink("25669")
    fun testHeavyPackageSizeSelection() {
        item = ItemAPI.uploadItem(loggedInUser, ItemRequestBuilder.VintedType.WITH_CATEGORY_FOR_HEAVY_SHIPPING)
        val randomHeavyPackageSize = GlobalAPI.getCatalogPackageSizes(user = loggedInUser, catalogId = item!!.catalogId)
            .filter { it.code.startsWith("HEAVY") }.random()

        deepLink.item.goToItem(item!!)
        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.EDIT)
        uploadItemRobot
            .openShippingOptionsSection()
            .selectPackageSize(randomHeavyPackageSize.title)
        shippingOptionRobot.clickSubmitInParcelSizeScreen()

        uploadItemRobot
            .assertShippingOption(randomHeavyPackageSize.title)
            .clickSaveAndWait()

        deepLink.item.goToItem(item!!)
        itemRobot
            .openItemActions()
            .clickOnAction(ItemActions.EDIT)

        IOS.scrollDown()
        uploadItemRobot.assertShippingOption(randomHeavyPackageSize.title)
    }
}
