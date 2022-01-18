package robot

import RobotFactory
import RobotFactory.billingAddressRobot
import RobotFactory.buyerProtectionProRobot
import RobotFactory.checkoutWorkflowRobot
import RobotFactory.contactDetailsRobot
import RobotFactory.dropOffPointSelectionRobot
import RobotFactory.homeDeliverySelectionRobot
import RobotFactory.inAppNotificationRobot
import RobotFactory.itemRobot
import RobotFactory.paymentAccountDetailsRobot
import api.controllers.GlobalAPI.Companion.getCarriers
import api.controllers.item.ItemRequestBuilder
import api.controllers.item.ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_CUSTOM_SHIPPING
import api.controllers.item.ItemRequestBuilder.VintedType.SIMPLE_ITEM_WITH_NO_SHIPPING
import api.controllers.item.getShipmentOptions
import api.data.models.VintedItem
import api.data.responses.VintedShipmentDeliveryType
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.data.enums.VintedBillingAddress
import commonUtil.data.enums.VintedPortal
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import robot.inbox.conversation.ConversationRobot
import robot.item.ItemRobot
import robot.payments.PaymentMethodsRobot
import robot.profile.balance.BillingAddressRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.base.BaseTest.Companion.otherUser
import util.driver.VintedBy
import util.driver.VintedElement
import util.image.ImageFactory
import util.image.ImageRecognition
import robot.workflow.CheckoutWorkflowRobot
import util.Android.Companion.CELL_TITLE_FIELD_ID
import util.absfeatures.AbTestController
import util.values.ElementByLanguage.Companion.addPersonalDataText
import util.values.ElementByLanguage.Companion.addShippingAddressText
import util.values.Visibility

class CheckoutRobot : BaseRobot() {
    private val itemPriceElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("checkout_header_item_price_value", "item")

    private val shippingPriceElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("checkout_header_shipping_price_value", "checkout_shipping_price_title")

    private val serviceFeePriceElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("checkout_header_service_fee_value"),
            VintedBy.accessibilityId("service_fee_info_title")
        )

    private val walletAmountElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "checkout_header_wallet_amount_value",
            "checkout_vinted_account_cell"
        )

    private val totalPriceElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("checkout_header_total_price_value", "checkout_shipping_price_total")

    private val addressSectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("checkout_details_shipping_address"),
            iOSBy = VintedBy.accessibilityId("addAddressCell")
        )

    private val filledAddressSectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("checkout_details_shipping_address"),
            iOSBy = VintedBy.accessibilityId("address_1_0")
        )

    private val addressSectionTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.androidTextByBuilder(text = Android.getElementValue("checkout_add_personal_details_row"), scroll = true),
                    androidBy2 = VintedBy.androidTextByBuilder(text = Android.getElementValue("checkout_add_address_row"), scroll = true)
                )
            },
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId(IOS.getElementValue("checkout_add_personal_details_row")),
                    iosBy2 = VintedBy.accessibilityId(IOS.getElementValue("checkout_add_address_row"))
                )
            }
        )

    private val personalDetailsTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("checkout_personal_details")),
            iOSBy = VintedBy.iOSTextByBuilder(IOS.getElementValue("checkout_personal_details"))
        )

    private val paymentMethodSectionElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidElement = { VintedDriver.findElement(androidBy = VintedBy.scrollableId("checkout_details_pay_in_method")) },
            iosTranslationKey = "checkout_add_payment_method_row"
        )

    private val buyButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_details_button"),
            iOSBy = VintedBy.accessibilityId("submit")
        )

    private fun paymentMethodExpirationDateElement(textWithExpirationDate: String) =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(
                text = textWithExpirationDate, scroll = true,
                searchType = Util.SearchTextOperator.CONTAINS
            ),
            iOSBy = VintedBy.iOSTextByBuilder(text = textWithExpirationDate, onlyVisibleInScreen = true, searchType = Util.SearchTextOperator.CONTAINS)
        )

    private val pickUpDeliveryOptionsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("shipping_delivery_options_pick_up_title"), scroll = true),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("shipping_delivery_options_pick_up_title"))
        )

    private val homeDeliveryOptionsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("shipping_delivery_options_home_title"), scroll = true),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("shipping_delivery_options_home_title"))
        )

    private val homeDeliveryCellElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("delivery_options_details_body_cell"),
            // TODO iosBy2 is for checkout_shipping_decoupling FS
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId("home_delivery_cell"),
                    iosBy2 = VintedBy.accessibilityId("home_delivery_carrier_name_text")
                )
            }
        )

    private val pickUpDeliveryDetailsElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.androidTextByBuilder(text = Android.getElementValue("checkout_choose_pick_up_point"), scroll = true),
                    androidBy2 = VintedBy.scrollableId("delivery_options_details_shipping_point")
                )
            },
            iOSBy = VintedBy.iOSNsPredicateString("name == 'selected_pickup_point_carrier_label' || name == '${(IOS.getElementValue("checkout_choose_pick_up_point"))}'")
        )

    private val homeDeliveryDetailsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("delivery_options_details_carrier"),
            // TODO iosBy2 is for checkout_shipping_decoupling FS
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId("home_delivery_cell"),
                    iosBy2 = VintedBy.accessibilityId("home_delivery_carrier_name_text")
                )
            }
        )

    private val customShippingDetailsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("shipping_delivery_options_custom_title")),
            // TODO iosBy2 is for checkout_shipping_decoupling FS
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId("custom_shipping_cell"),
                    iosBy2 = VintedBy.accessibilityId(IOS.getElementValue("shipping_delivery_options_custom_title"))
                )
            }
        )

    private val noShippingDetailsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("shipping_delivery_options_meet_up_title")),
            // TODO iosBy2 is for checkout_shipping_decoupling FS
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId("meetup_shipping_cell"),
                    iosBy2 = VintedBy.accessibilityId(IOS.getElementValue("shipping_delivery_options_meet_up_title"))
                )
            }
        )

    private val closeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("actionbar_button"),
            iOSBy = VintedBy.accessibilityId("close")
        )

    private fun shipmentCarrierElement(name: String): VintedElement =
        VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.scrollableIdWithText("carrier_item_title", name.trim()),
                    androidBy2 = VintedBy.scrollableIdWithText(Android.CELL_BODY_FIELD_ID, name.trim())
                )
            },
            iOSBy = VintedBy.iOSNsPredicateString("label ==[c] '$name'")
        )

    private val buyerProtectionFeeCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_header_service_fee_cell"),
            iOSBy = VintedBy.accessibilityId("service_fee_info_title")
        )

    // TODO iOS is missing id
    private val buyerProtectionFeeInfoElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("checkout_header_service_fee_info")
        )

    private val proTermsAgreementTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("checkout_header_business_seller_agreement"),
            iOSBy = VintedBy.accessibilityId("business_account_terms_of_sale")
        )

    private val checkoutAddPhoneNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("checkout_details_contact_details_cell"),
            iOSBy = VintedBy.accessibilityId("checkout_add_phone_number")
        )

    private val addedPhoneNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("checkout_details_contact_details_cell", CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeCell[`name == 'checkout_edit_phone_number'`]/XCUIElementTypeStaticText"
            )
        )

    private fun addressOrPersonalDetailsElement(element: String, searchType: Util.SearchTextOperator) =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = element, scroll = true, searchType = searchType),
            iOSBy = VintedBy.iOSTextByBuilder(text = element, onlyVisibleInScreen = false, searchType = searchType)
        )

    @Step("Assert item photo is visible in the checkout")
    fun assertItemPhotoIsVisibleInCheckout() {
        VintedAssert.assertTrue(
            ImageRecognition.isImageInScreen(ImageFactory.ITEM_1_PHOTO_CHECKOUT),
            "Image occurrence was not found"
        )
    }

    @Step("Open payment method selection screen")
    fun openPaymentMethodsSection(): PaymentMethodsRobot {
        paymentMethodSectionElement.click()
        return RobotFactory.paymentMethodsRobot
    }

    @Step("Assert address is empty")
    fun assertAddressSectionIsEmpty(): CheckoutWorkflowRobot {
        val addShippingAddressOrDetailsText = if (loggedInUser.billingAddress.needsPersonalDetails && personalDetailsTextElement.isVisible())
            addPersonalDataText else addShippingAddressText
        addressSectionTextElement.text.let { addressSectionText ->
            VintedAssert.assertEquals(
                addressSectionText, addShippingAddressOrDetailsText,
                "Text had to be $addShippingAddressText, but was $addressSectionText"
            )
            return checkoutWorkflowRobot
        }
    }

    @Step("Open address screen")
    fun openAddAddressSection(): CheckoutRobot {
        addressSectionElement.click()
        return this
    }

    @Step("Open filled in address screen to edit")
    fun openEditAddressSection(): CheckoutRobot {
        filledAddressSectionElement.click()
        return this
    }

    @Step("Assert item, shipping, service and total prices are displayed in checkout screen")
    fun assertAllPricesAreDisplayed(): CheckoutRobot {
        VintedAssert.assertTrue(itemPriceElement.isVisible(10), "Item price should be visible")
        VintedAssert.assertTrue(shippingPriceElement.isVisible(10), "Shipping price should be visible")
        VintedAssert.assertTrue(serviceFeePriceElement.isVisible(10), "Service fee should be visible")
        VintedAssert.assertTrue(totalPriceElement.isVisible(10), "Total price should be visible")
        return this
    }

    @Step("Android only: Click on buyer protection info element")
    fun clickOnBuyerProtectionInfoAndroid(): BuyerProtectionProRobot {
        // TODO update steps to run on iOS when id is added
        Android.doIfAndroid {
            buyerProtectionFeeInfoElementAndroid.click()
        }
        return buyerProtectionProRobot
    }

    @Step("Assert wallet amount is visible")
    fun assertWalletAmountIsDisplayed(): CheckoutRobot {
        VintedAssert.assertTrue(walletAmountElement.isVisible(), "Wallet amount should be visible")
        return this
    }

    @Step("Assert added credit card expiration date is visible in checkout")
    fun assertCreditCardExpirationDateIsDisplayed(
        dateMonth: String = loggedInUser.creditCardCredentials.info.date_month,
        dateYear: String = loggedInUser.creditCardCredentials.info.date_year
    ): CheckoutRobot {
        val expirationDateText = "$dateMonth/20$dateYear"
        Android.scrollDownABit()
        VintedAssert.assertTrue(
            paymentMethodExpirationDateElement(expirationDateText).withScrollIos().withWait(seconds = 20).isVisible(),
            "Credit card expiration date should be visible"
        )
        return this
    }

    @Step("Click buy button")
    fun clickBuy(): ConversationRobot {
        buyButtonElement.withWait()
        inAppNotificationRobot.closeInAppNotificationIfExists()
        buyButtonElement.click()
        return RobotFactory.conversationRobot
    }

    @Step("Click on home delivery cell")
    fun clickHomeDeliveryCell(): HomeDeliverySelectionRobot {
        homeDeliveryCellElement.withScrollIos().click()
        return homeDeliverySelectionRobot
    }

    @Step("Select {deliveryType} delivery type and details")
    fun selectPickUpOrHomeDeliveryTypeAndDetails(deliveryType: VintedShipmentDeliveryType): CheckoutRobot {
        VintedDriver.scrollDown()
        when (deliveryType) {
            VintedShipmentDeliveryType.HOME -> {
                homeDeliveryOptionsElement.withScrollIos().click()
                IOS.scrollDown()
                homeDeliveryDetailsElement.withScrollIos().click()
            }
            VintedShipmentDeliveryType.PICK_UP -> {
                pickUpDeliveryOptionsElement.withScrollIos().click()
                pickUpDeliveryDetailsElement.withScrollIos().click()
            }
            else -> {}
        }
        return this
    }

    @Step("Click on Pick-up point button if visible")
    fun selectPickUpPointIfNeeded(deliveryType: VintedShipmentDeliveryType): CheckoutWorkflowRobot {
        if (deliveryType == VintedShipmentDeliveryType.PICK_UP) dropOffPointSelectionRobot.selectDropOffPoint()
        return checkoutWorkflowRobot
    }

    @Step("Assert selected Drop Off Point is visible in checkout screen")
    fun assertSelectedDropOffPointIsVisibleInCheckout(): CheckoutRobot {
        val selectedPickUpPointElement = pickUpDeliveryDetailsElement
        VintedAssert.assertTrue(selectedPickUpPointElement.isVisible(), "Selected Drop Off Point should be visible in checkout")
        return this
    }

    @Step("Assert Home delivery option is not visible")
    fun assertHomeDeliveryOptionInvisible(): CheckoutRobot {
        VintedAssert.assertTrue(homeDeliveryOptionsElement.isInvisible(), "Home delivery option should be invisible")
        return this
    }

    @Step("Fill address or personal details")
    fun fillAddressOrPersonalDetails(billingAddress: VintedBillingAddress): BillingAddressRobot {
        return if (billingAddress.needsPersonalDetails && personalDetailsTextElement.isVisible()) {
            billingAddressRobot.fillPhoneNumber(billingAddress.getPhoneNumber())
        } else {
            billingAddressRobot.fillBillingAddress(billingAddress)
        }
    }
    @Step("Assert address or personal details {element} is {visibility}")
    fun assertAddressOrPersonalDetails(element: String, searchType: Util.SearchTextOperator, visibility: Visibility): CheckoutRobot {
        VintedAssert.assertVisibilityEquals(
            addressOrPersonalDetailsElement(element, searchType), visibility,
            "$element should be $visibility"
        )
        return this
    }

    @Step("Assert address or phone number is visible")
    fun assertAddressOrPhoneNumberIsVisible(
        billingAddress: VintedBillingAddress = loggedInUser.billingAddress
    ): CheckoutRobot {
        VintedDriver.scrollUpABit()
        if (personalDetailsTextElement.isInvisible()) {
            assertAddressOrPersonalDetails(billingAddress.address1, Util.SearchTextOperator.STARTS_WITH, visibility = Visibility.Visible)
            assertAddressOrPersonalDetails(billingAddress.postalCode, Util.SearchTextOperator.CONTAINS, visibility = Visibility.Visible)
            assertAddressOrPersonalDetails(billingAddress.city, Util.SearchTextOperator.CONTAINS, Visibility.Visible)
        } else {
            assertAddressOrPersonalDetails(billingAddress.getPhoneNumber(), Util.SearchTextOperator.CONTAINS, Visibility.Visible)
        }
        return this
    }

    @Step("PL only: Add address if phone number was deleted because of changing delivery type from PICK-UP to HOME")
    fun addAddressIfPhoneWasDeletedWhenChangingDeliveryTypeInPL(): CheckoutRobot {
        if (!AbTestController.isAddressFormUnificationIsOn() && PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL)) {
            IOS.scrollUp()
            Android.scrollUp()
            if (personalDetailsTextElement.isInvisible()) {
                val addressLine1 = loggedInUser.billingAddress.address1
                val phoneNumber = loggedInUser.billingAddress.getPhoneNumber()

                paymentAccountDetailsRobot.assertBillingName(loggedInUser.realName!!)
                assertAddressOrPersonalDetails(addressLine1, Util.SearchTextOperator.STARTS_WITH, visibility = Visibility.Invisible)
                assertAddressOrPersonalDetails(phoneNumber, Util.SearchTextOperator.CONTAINS, visibility = Visibility.Invisible)
                checkoutWorkflowRobot.openEditAndAssertPreviouslyFilledAddressDetailsInCheckout()
            }
        }
        return this
    }

    @Step("Leave checkout screen")
    fun leaveCheckout(): ItemRobot {
        closeElement.click()
        return itemRobot
    }

    @Step("Assert home delivery shipping carrier {expectedHomeDeliveryCarrier} is visible")
    fun assertHomeDeliveryShippingCarrierProviderIsVisible(expectedHomeDeliveryCarrier: String?, softAssert: VintedSoftAssert): VintedSoftAssert {
        softAssert.assertTrue(
            shipmentCarrierElement(expectedHomeDeliveryCarrier!!).withScrollIos().isVisible(),
            "Expected home delivery carrier '$expectedHomeDeliveryCarrier' should be visible"
        )
        return softAssert
    }

    @Step("Assert {itemType} shipping details are visible")
    fun assertShippingOptionDetailsVisibility(itemType: ItemRequestBuilder.VintedType, deliveryType: VintedShipmentDeliveryType?): CheckoutRobot {
        VintedDriver.scrollDown()
        when (itemType) {
            SIMPLE_ITEM_WITH_CUSTOM_SHIPPING ->
                VintedAssert.assertTrue(customShippingDetailsElement.isVisible(), "Custom shipping details should be visible")
            SIMPLE_ITEM_WITH_NO_SHIPPING ->
                VintedAssert.assertTrue(noShippingDetailsElement.isVisible(), "No shipping details should be visible")
            else ->
                when (deliveryType) {
                    VintedShipmentDeliveryType.PICK_UP -> VintedAssert.assertTrue(
                        pickUpDeliveryDetailsElement.isVisible(),
                        "Pick-up delivery details element should be visible"
                    )
                    VintedShipmentDeliveryType.HOME -> VintedAssert.assertTrue(homeDeliveryDetailsElement.isVisible(), "Home delivery details element should be visible")
                    else -> {}
                }
        }
        return this
    }

    @Step("Assert turned off carrier is not visible in checkout")
    fun assertTurnedOffCarrierIsNotVisible(turnedOffCarrier: Long, item: VintedItem): CheckoutRobot {
        val turnedOffCarrierName = getCarriers(user = loggedInUser).first { it.id == turnedOffCarrier }.name
        val availableCarriers = otherUser.getShipmentOptions(item).map { it.title }
        val visibleCarriersTitles = mutableListOf<String>()

        Android.scrollDown()
        commonUtil.reporting.Report.addMessage("Turned off carrier is $turnedOffCarrierName. Carriers visible in checkout for buyer: $availableCarriers")
        availableCarriers.forEach { name ->
            val carrier = shipmentCarrierElement(name).withScrollIos()
            VintedAssert.assertNotEquals(carrier.text, turnedOffCarrierName, "Turned off carrier $turnedOffCarrierName should not be visible in checkout")
            visibleCarriersTitles.add(carrier.text)
        }
        VintedAssert.assertEquals(visibleCarriersTitles.count(), availableCarriers.count(), "Shipment carrier provider list count does not match")
        return this
    }

    @Step("Assert buyer protection fee is displayed")
    fun assertBuyerProtectionFeeIsDisplayed(): CheckoutRobot {
        VintedAssert.assertTrue(buyerProtectionFeeCell.isVisible(10), "Buyer protection fee should be displayed")
        return this
    }

    @Step("Assert pro terms agreement text is displayed")
    fun assertProTermsAgreementIsDisplayed(): CheckoutRobot {
        VintedAssert.assertTrue(proTermsAgreementTextElement.withScrollIos().isVisible(), "Pro terms agreement text should be displayed")
        return this
    }

    @Step("Click on add phone number")
    fun clickAddPhoneNumber(): ContactDetailsRobot {
        checkoutAddPhoneNumberElement.withScrollIos().click()
        return contactDetailsRobot
    }

    @Step("Assert phone number is added")
    fun assertPhoneNumberIsAdded(): ContactDetailsRobot {
        val phoneNumber = VintedShippingAddress.getByPortal().phoneNumber
        VintedAssert.assertEquals(addedPhoneNumberElement.text, phoneNumber, "Phone number $phoneNumber should be visible")
        return contactDetailsRobot
    }
}
