package robot.workflow

import RobotFactory.checkoutRobot
import RobotFactory.checkoutWorkflowRobot
import RobotFactory.contactDetailsRobot
import RobotFactory.deepLink
import RobotFactory.dropOffPointSelectionRobot
import RobotFactory.homeDeliverySelectionRobot
import RobotFactory.labelDeliveryRobot
import RobotFactory.paymentAccountDetailsRobot
import RobotFactory.securityWebViewRobot
import api.controllers.item.ItemRequestBuilder
import api.controllers.item.getShipmentOptions
import api.controllers.user.shipmentApi
import api.controllers.user.transactionApi
import api.data.models.VintedItem
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.data.enums.VintedPortal
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.BaseRobot
import robot.CheckoutRobot
import robot.DropOffPointSelectionRobot
import util.absfeatures.AbTestController.isAddressFormUnificationIsOn
import util.absfeatures.AbTestController.isCheckoutPhoneNumberOn
import util.absfeatures.AbTestController.isShippingLabelPhoneNumberOn
import util.absfeatures.AbTestController.isSinglePudoCarrierV1BCVariants
import util.base.BaseTest.Companion.loggedInUser
import util.data.CreditCardDetails
import util.values.Visibility

class CheckoutWorkflowRobot : BaseRobot() {

    @Step("Go to item checkout and fill address and credit card details")
    fun goToItemCheckoutAndFillAddressAndCreditCardDetails(item: VintedItem): CheckoutWorkflowRobot {
        deepLink.item.goToItem(item)
            .clickBuyButton()
            .assertAllPricesAreDisplayed()
        checkoutRobot
            .openPaymentMethodsSection()
            .selectCreditCardPaymentMethod()
            .insertNewCreditCardInfo(CreditCardDetails.CreditCard())
            .saveCreditCardAndHandle3dsIfNeeded()
        checkoutRobot
            .assertCreditCardExpirationDateIsDisplayed()
        openFillAndAssertNewAddressDetailsInCheckout()
        return this
    }

    @Step("Open, fill and assert new address details in checkout")
    fun openFillAndAssertNewAddressDetailsInCheckout(): CheckoutWorkflowRobot {
        checkoutRobot.openAddAddressSection()
        fillAndAssertAddressDetailsInCheckout()
        return this
    }

    @Step("Open, edit and assert previously filled in address details in checkout")
    fun openEditAndAssertPreviouslyFilledAddressDetailsInCheckout(): CheckoutWorkflowRobot {
        checkoutRobot.openEditAddressSection()
        fillAndAssertAddressDetailsInCheckout()
        return this
    }

    @Step("Fill and assert address details in checkout")
    private fun fillAndAssertAddressDetailsInCheckout(): CheckoutWorkflowRobot {
        checkoutRobot
            .fillAddressOrPersonalDetails(loggedInUser.billingAddress)
            .clickSave()
        paymentAccountDetailsRobot
            .assertBillingName(loggedInUser.realName!!)
        checkoutRobot
            .assertAddressOrPhoneNumberIsVisible()
        return this
    }

    @Step("Click buy and assert message input is visible")
    fun clickBuyAndAssertMessageInputIsVisible(item: VintedItem) {
        checkoutRobot.clickBuy()
        securityWebViewRobot
            .simulateSuccessful3dsResponseAfterClickingBuy(item)
            .assertMessageInputVisibility(Visibility.Visible)
    }

    @Step("Buy an item with {deliveryType} delivery method")
    fun buyAnItemWithPickUpOrHomeDeliveryMethod(otherUserItem: VintedItem, deliveryType: VintedShipmentDeliveryType) {
        goToItemCheckoutAndFillAddressAndCreditCardDetails(otherUserItem)
        selectPickUpOrHomeDeliveryTypeByItemFillDeliveryDetailsAndSelectPickUpPointIfNeeded(otherUserItem, deliveryType)
        setShippingContactInformationForBuyerIfRequired(otherUserItem).addAddressIfPhoneWasDeletedWhenChangingDeliveryTypeInPL()
        clickBuyAndAssertMessageInputIsVisible(otherUserItem)
    }

    @Step("Buy {itemType} item with custom or no shipping method")
    fun buyAnItemWithCustomOrNoShippingMethod(item: VintedItem, itemType: ItemRequestBuilder.VintedType) {
        goToItemCheckoutAndFillAddressAndCreditCardDetails(item)
        assertCustomOrNoShippingOptionDetailsAreVisible(itemType)
        clickBuyAndAssertMessageInputIsVisible(item)
    }

    @Step("Assert custom or no shipping option details are visible for {itemType}")
    fun assertCustomOrNoShippingOptionDetailsAreVisible(itemType: ItemRequestBuilder.VintedType): CheckoutWorkflowRobot {
        checkoutRobot.assertShippingOptionDetailsVisibility(itemType, null)
        return this
    }

    @Step("Select Pick-up shipping")
    fun selectPickUpShipping(): DropOffPointSelectionRobot {
        checkoutRobot.selectPickUpOrHomeDeliveryTypeAndDetails(VintedShipmentDeliveryType.PICK_UP)
        return dropOffPointSelectionRobot
    }

    @Step("Select {deliveryType} delivery by item, fill delivery details and select Pick-up point if needed")
    fun selectPickUpOrHomeDeliveryTypeByItemFillDeliveryDetailsAndSelectPickUpPointIfNeeded(item: VintedItem, deliveryType: VintedShipmentDeliveryType): CheckoutWorkflowRobot {
        selectDeliveryTypeByItemOrTransaction(item, null, deliveryType)
        checkoutRobot.selectPickUpPointIfNeeded(deliveryType)
        return checkoutWorkflowRobot
    }

    @Step("Select {deliveryType} type by transaction and fill delivery details")
    fun selectPickUpOrHomeDeliveryTypeByTransactionAndFillDeliveryDetails(transactionId: Long, deliveryType: VintedShipmentDeliveryType): CheckoutRobot {
        selectDeliveryTypeByItemOrTransaction(null, transactionId, deliveryType)
        return checkoutRobot
    }

    private fun selectDeliveryTypeByItemOrTransaction(item: VintedItem?, transactionId: Long?, deliveryType: VintedShipmentDeliveryType) {
        val expectedHomeDeliveryCarriersCount = getExpectedHomeDeliveryCountByItemOrTransactionId(item, transactionId)
        checkoutRobot.selectPickUpOrHomeDeliveryTypeAndDetails(deliveryType)
        if (deliveryType == VintedShipmentDeliveryType.HOME) {
            if (isSinglePudoCarrierV1BCVariants() || expectedHomeDeliveryCarriersCount == 1) {
                checkoutRobot
                    .clickHomeDeliveryCell()
                    .assertHomeDeliverySelectionScreenVisibility(Visibility.Invisible)
            } else {
                homeDeliverySelectionRobot.selectHomeDeliveryCarrierAndAssertCarriersCount(
                    expectedHomeDeliveryCarriersCount
                )
            }
        }
    }

    @Step("Get expected home delivery carriers count by item or transaction")
    private fun getExpectedHomeDeliveryCountByItemOrTransactionId(item: VintedItem?, transactionId: Long?): Int {
        return if (item != null) {
            loggedInUser.getShipmentOptions(item).filter { it.deliveryType == VintedShipmentDeliveryType.HOME }.count()
        } else {
            loggedInUser.shipmentApi.getTransactionShipmentOptions(transactionId!!)
                .filter { it.deliveryType == VintedShipmentDeliveryType.HOME }.count()
        }
    }

    @Step("Add and assert phone number in checkout")
    private fun addPhoneNumberInCheckout() {
        checkoutRobot.clickAddPhoneNumber()
        insertAndSaveNumberInContactDetails()
            .assertPhoneNumberIsAdded()
    }

    @Step("Add and assert phone number in label delivery")
    private fun addPhoneNumberInLabelDelivery() {
        labelDeliveryRobot.clickAddPhoneNumberInLabelDelivery()
        insertAndSaveNumberInContactDetails()
        labelDeliveryRobot.assertAddedPhoneNumberIsVisibleInLabelDeliveryScreen()
    }

    @Step("Insert and save phone number in contact details")
    fun insertAndSaveNumberInContactDetails(): CheckoutRobot {
        val phoneNumber = VintedShippingAddress.getByPortal().phoneNumber
        contactDetailsRobot
            .addPhoneNumber(phoneNumber)
            .confirmPhoneNumber()
        return checkoutRobot
    }

    @Step("Set shipping contact information for buyer if required")
    fun setShippingContactInformationForBuyerIfRequired(item: VintedItem): CheckoutRobot {
        if (isPhoneNumberInCheckout() && loggedInUser.transactionApi.checkIfShippingContactIsRequired(item)) addPhoneNumberInCheckout()
        return checkoutRobot
    }

    @Step("Set shipping contact information for seller if required")
    fun setShippingContactInformationForSellerIfRequired(item: VintedItem): CheckoutRobot {
        if (isPhoneNumberInShippingLabel() && loggedInUser.transactionApi.checkIfShippingContactIsRequired(item)) addPhoneNumberInLabelDelivery()
        return checkoutRobot
    }

    @Step("Is phone number in checkout")
    private fun isPhoneNumberInCheckout(): Boolean {
        return if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL)) (isAddressFormUnificationIsOn())
        else (isCheckoutPhoneNumberOn())
    }

    @Step("Is phone number in shipping label")
    private fun isPhoneNumberInShippingLabel(): Boolean {
        return if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL)) (isAddressFormUnificationIsOn())
        else (isShippingLabelPhoneNumberOn())
    }
}
