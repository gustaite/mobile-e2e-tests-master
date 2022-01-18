package robot.inbox.conversation

import RobotFactory.billingAddressRobot
import RobotFactory.checkoutWorkflowRobot
import RobotFactory.conversationRobot
import RobotFactory.inAppNotificationRobot
import RobotFactory.paymentAccountDetailsRobot
import RobotFactory.addressRobot
import api.data.models.VintedItem
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedPortal
import commonUtil.data.enums.VintedShippingAddress
import commonUtil.testng.config.ConfigManager.portal
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.balance.BillingAddressRobot
import util.*
import util.base.BaseTest
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ConversationElementTexts.parcelShipmentFromOptionsText

class LabelDeliveryRobot : BaseRobot() {

    private val addAddressElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.setWithParentAndChild(
                        "shipping_label_address_cell",
                        Android.CELL_TITLE_FIELD_ID
                    ),
                    androidBy2 = VintedBy.setWithParentAndChild(
                        "shipping_label_delivery_address",
                        Android.CELL_TITLE_FIELD_ID
                    )
                )
            },
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("get_shipping_label_add_your_address_title"))
        )

    private val itemMakeOfferButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidIdAndText("message_header_action_secondary", Android.getElementValue("transaction_btn_make_offer")),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'message_action_request_offer' || name == 'message_action_offer'")
        )

    private val generateLabelButton: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.scrollableId("shipping_label_confirm"),
                    androidBy2 = VintedBy.scrollableId("checkout_shipping_label_submit")
                )
            },
            iOSBy = VintedBy.iOSNsPredicateString("name == 'shipping_label_submit_title' || name == 'shipping_label_submit'")
        )

    private val howSendPackageContinueButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            androidId = "drop_off_selection_continue_button",
            iosTranslationKey = "drop_off_selection_continue_button_title"
        )

    private val parcelShipmentFromOptionsElement: VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(parcelShipmentFromOptionsText.random()),
            iOSBy = VintedBy.iOSTextByBuilder(parcelShipmentFromOptionsText.random())
        )

    private val labelDeliveryPhoneNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("shipping_label_contact_details_cell"),
            iOSBy = VintedBy.accessibilityId("phoneNumberCell")
        )

    private val labelDeliveryAddedPhoneNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "shipping_label_contact_details_cell", Android.CELL_TITLE_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain(
                "**/XCUIElementTypeOther[`name == 'phoneNumberCell'`]/**/XCUIElementTypeStaticText"
            )
        )

    private val confirmParcelShipmentFromOptionElement: VintedElement
        get() = VintedDriver.findElement(
            VintedBy.id("modal_primary_button"),
            iOSBy = VintedBy.accessibilityId("confirm")
        )

    @Step("Confirm Balance information")
    fun confirmBalanceInformation() {
        paymentAccountDetailsRobot
            .enterBirthday()
            .enterSecurityNumber()
        billingAddressRobot
            .openFillAndSaveBillingAddress()
            .clickSave()
        inAppNotificationRobot.closeInAppNotificationIfExists()
    }

    @Step("Open Billing address screen and fill in Billing address")
    fun openAndFillInBillingAddress() {
        addAddressElement.tap()
        addressRobot.insertFullAddressOrPersonalDetailsInfo(BaseTest.loggedInUser.billingAddress)
        billingAddressRobot.clickSave()
    }

    @Step("Assert added phone number is visible in label delivery screen")
    fun assertAddedPhoneNumberIsVisibleInLabelDeliveryScreen() {
        val phoneNumber = VintedShippingAddress.getByPortal().phoneNumber
        VintedAssert.assertEquals(
            labelDeliveryAddedPhoneNumberElement.text, phoneNumber,
            "Phone number $phoneNumber should be visible"
        )
    }

    @Step("Click add phone in number in label delivery")
    fun clickAddPhoneNumberInLabelDelivery(): BillingAddressRobot {
        labelDeliveryPhoneNumberElement.click()
        return billingAddressRobot
    }

    @Step("Click generate label")
    fun clickGenerateLabel() {
        generateLabelButton.withScrollIos().click()
    }

    @Step("Choose to send package from mailbox or post office")
    fun chooseToSendPackageFromMailboxOrPostOfficeIfVisible() {
        if (portal == VintedPortal.SB_INT && parcelShipmentFromOptionsElement.isVisible()) {
            parcelShipmentFromOptionsElement.click()
            howSendPackageContinueButtonElement.click()
        }
    }

    @Step("Confirm Parcel Shipment From Selected Option if visible")
    fun confirmParcelShipmentFromSelectedOption() {
        if (portal == VintedPortal.SB_INT && confirmParcelShipmentFromOptionElement.isVisible()) {
            confirmParcelShipmentFromOptionElement.click()
        }
    }

    @Step("Fill in recovery address and generate label")
    fun fillInRecoveryAddressAndGenerateLabel(item: VintedItem): ConversationRobot {
        confirmBalanceInformation()
        chooseToSendPackageFromMailboxOrPostOfficeIfVisible()
        openAndFillInBillingAddress()
        checkoutWorkflowRobot.setShippingContactInformationForSellerIfRequired(item)
        clickGenerateLabel()
        confirmParcelShipmentFromSelectedOption()
        return conversationRobot
    }
}
