package robot.workflow

import RobotFactory.checkoutRobot
import RobotFactory.conversationRobot
import RobotFactory.deepLink
import RobotFactory.homeDeliverySelectionRobot
import RobotFactory.itemRobot
import commonUtil.testng.config.ConfigManager.portal
import api.controllers.PortalConfigurationAPI
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import api.data.models.VintedItem
import api.data.responses.VintedShipmentDeliveryType
import api.factories.UserFactory
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedPortal
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.extensions.adaptPrice
import commonUtil.data.enums.*
import commonUtil.extensions.isInitialized
import commonUtil.thread
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.PriceFactory
import util.VintedDriver
import util.base.BaseTest.Companion.flexibleAddressUser
import util.base.BaseTest.Companion.loggedInUser
import util.values.Visibility
import java.text.DecimalFormat

open class ShipmentWorkflowRobot : BaseRobot() {

    private var itemSimple: VintedItem by thread.lateinit()
    private var itemHeavy: VintedItem by thread.lateinit()
    private var itemCustom: VintedItem by thread.lateinit()
    private var itemNoShipping: VintedItem by thread.lateinit()

    fun buyerAddRemoveBundleItems(
        buyerAddress: VintedShippingAddress, sellerAddress: VintedShippingAddress, isBuyVisible: Boolean,
        itemType: ItemRequestBuilder.VintedType = ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING
    ) {
        updateSellerShippingAddresses(sellerAddress)
        createRequiredItems(itemType == ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING)
        if (itemType != ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING) {
            ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        }
        val item = decideItem(itemType)
        assertBuyerSellerAndItemCountryIdAreExpected(buyerAddress, sellerAddress, item)
        deepLink
            .item.goToItem(item = item)
            .assertBundleButtonVisibilityInUserItemsTab(Visibility.Visible, scrollUp = false)
            .clickBundleButton()
            .addBundleElementsAndContinue(1)
            .assertConversationScreenIsVisible()
            .assertBundleItemsCount(2)
            .assertBuyAndMakeOfferButtonVisibility(isBuyVisible)
            .clickOnBundledImage(2)
            .clickAddMoreItemsButton()
            .addBundleElementsAndContinue(2)
        IOS.doIfiOS { conversationRobot.conversationDetailsRobot.clickBack() }
        conversationRobot
            .assertBundleItemsCount(4)
            .assertBuyAndMakeOfferButtonVisibility(isBuyVisible)
            .clickOnBundledImage(4)
            .clickUpdateOrderButton()
            .removeBundleElementsAndContinue(1)
        IOS.doIfiOS { conversationRobot.conversationDetailsRobot.clickBack() }
        conversationRobot.assertBundleItemsCount(3)
            .assertBuyAndMakeOfferButtonVisibility(isBuyVisible)
    }

    fun buyerFromOneCountrySellerFromAnother(
        buyerAddress: VintedShippingAddress, sellerAddress: VintedShippingAddress, isBuyVisible: Boolean, shippingCarriers: VintedShippingRoutesCarriers,
        itemType: ItemRequestBuilder.VintedType = ItemRequestBuilder.VintedType.SIMPLE_ITEM, bundleVisibility: Visibility = Visibility.Invisible,
        deliveryType: VintedShipmentDeliveryType = VintedShipmentDeliveryType.HOME
    ) {
        updateSellerShippingAddresses(sellerAddress)
        val userCountry = PortalConfigurationAPI.getConfiguration(loggedInUser).systemConfiguration.userCountry
        commonUtil.reporting.Report.addMessage("Logged in user country is: $userCountry")
        createRequiredItems()
        val item = decideItem(itemType)
        assertBuyerSellerAndItemCountryIdAreExpected(buyerAddress, sellerAddress, item)

        deepLink
            .item.goToItem(item = item)
            .assertBundleButtonVisibilityInUserItemsTab(bundleVisibility)
            .assertBuyButtonVisibility(isBuyVisible)
            .assertMessageButtonIsVisibleInItemScreen()
        Android.doIfAndroid { VintedDriver.scrollUpABit() }
        if (isBuyVisible) {
            itemRobot.clickBuyButton()
            assertCheckoutPricesCarriersAndManualDeliveryOptions(shippingCarriers, itemType, item, deliveryType)
        }
        itemRobot
            .clickItemMessageButton()
            .assertConversationScreenIsVisible()
            .assertBuyAndMakeOfferButtonVisibility(isBuyVisible)
        if (isBuyVisible) {
            conversationRobot.clickItemBuyButton()
            assertCheckoutPricesCarriersAndManualDeliveryOptions(shippingCarriers, itemType, item, deliveryType)

            val priceNumeric = DecimalFormat("0.00").format(item.priceNumeric.toDouble() - 1).adaptPrice()

            val priceFormatted = PriceFactory.getFormattedPriceWithCurrencySymbol(priceNumeric, replaceSpaceCharToSpec = true)
            conversationRobot
                .clickItemMakeOfferButton()
                .makeAnOfferRequest(priceNumeric)
                .offerActionsRobot.assertOfferRequestPrice(priceFormatted)
        }
    }

    @Step("[API] Assert buyer, seller and item country id match expected")
    private fun assertBuyerSellerAndItemCountryIdAreExpected(buyerAddress: VintedShippingAddress, sellerAddress: VintedShippingAddress, item: VintedItem) {
        VintedAssert.assertEquals(flexibleAddressUser.userApi.getInfo().countryId, sellerAddress.countryId, "Seller country id does not match")
        VintedAssert.assertEquals(loggedInUser.userApi.getInfo().countryId, buyerAddress.countryId, "Buyer country id does not match")
        VintedAssert.assertEquals(item.countryId, sellerAddress.countryId, "Item country Id does not match")
        VintedAssert.assertEquals(item.user?.countryId, sellerAddress.countryId, "User country Id does not match on item")
    }

    private fun assertCheckoutPricesCarriersAndManualDeliveryOptions(
        shippingCarriers: VintedShippingRoutesCarriers,
        itemType: ItemRequestBuilder.VintedType,
        item: VintedItem,
        deliveryType: VintedShipmentDeliveryType,
    ) {
        flexibleAddressUser.userApi.assertShipmentOptionsIsNotEmpty(item)
        checkoutRobot.assertAllPricesAreDisplayed()
        if (itemType == ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING || itemType == ItemRequestBuilder.VintedType.SIMPLE_ITEM) {
            checkoutRobot.selectPickUpOrHomeDeliveryTypeAndDetails(deliveryType)
            when (deliveryType) {
                VintedShipmentDeliveryType.HOME -> homeDeliverySelectionRobot.assertHomeDeliveryCarriersAreVisible(item, shippingCarriers)
                VintedShipmentDeliveryType.PICK_UP -> {
                    clickBack()
                    checkoutRobot.assertHomeDeliveryOptionInvisible()
                }
                else -> {}
            }
        }
        checkoutRobot
            .assertShippingOptionDetailsVisibility(itemType, deliveryType)
            .leaveCheckout()
    }

    private fun decideItem(itemType: ItemRequestBuilder.VintedType): VintedItem {
        return when (itemType) {
            ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING -> itemCustom
            ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING -> itemHeavy
            ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING -> itemNoShipping
            else -> itemSimple
        }
    }

    private fun updateSellerShippingAddresses(sellerAddress: VintedShippingAddress) {
        flexibleAddressUser.userApi.updateShippingAddress(sellerAddress)
    }

    private val portalsWithoutHeavyShipping
        get() = listOf(VintedPortal.US, VintedPortal.SB_US)

    private fun createHeavyShippingItem() {
        if (!itemHeavy.isInitialized()) {
            if (portal !in portalsWithoutHeavyShipping) {
                itemHeavy = ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_HEAVY_LARGE_SHIPPING, price = "6.00")
                commonUtil.reporting.Report.addMessage("New Heavy item created")
            } else {
                itemHeavy = createItem("6.00") // Needed for Bundle check
            }
        }
    }

    private fun createCustomShippingItem() {
        if (!itemCustom.isInitialized()) {
            itemCustom = ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING, price = "7.00")
            commonUtil.reporting.Report.addMessage("New Custom item created")
        }
    }

    private fun createNoShippingItem(alsoCreateNoShippingItem: Boolean) {
        if (alsoCreateNoShippingItem && !itemNoShipping.isInitialized()) {
            itemNoShipping = ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING, price = "8.00")
            commonUtil.reporting.Report.addMessage("New NoShopping item created")
        }
    }

    private fun createItem(price: String): VintedItem {
        val item = ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, price = price)
        commonUtil.reporting.Report.addMessage("Another Simple item created")
        return item
    }

    private fun createRequiredItems(alsoCreateNoShippingItem: Boolean = true) {
        if (!itemSimple.isInitialized()) {
            itemSimple = ItemAPI.uploadItem(flexibleAddressUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM, price = "5.00")
            commonUtil.reporting.Report.addMessage("New Simple item created")
        }

        createHeavyShippingItem()
        createCustomShippingItem()
        createNoShippingItem(alsoCreateNoShippingItem)
    }

    fun changeShippingAddressAndCheckPreferredChoiceShippingCarriers(shippingAddress: VintedShippingAddress, preferredChoiceShippingCarriers: List<String>) {
        loggedInUser.userApi.updateShippingAddress(shippingAddress)
        deepLink
            .goToSettings()
            .openShippingSettings()
            .assertShippingCarriersName(preferredChoiceShippingCarriers)
            .assertAllPreferredCarriersAreEnabled()
    }

    fun createUserUpdateShippingAddressAndSetLanguage(country: VintedCountries) {
        loggedInUser = UserFactory.createRandomUser(country = country)
        loggedInUser.userApi.updateShippingAddress(country.getShippingAddress())
        deepLink.selectLanguageiOS(country)
    }
}
