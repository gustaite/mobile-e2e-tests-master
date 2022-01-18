package robot.item

import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.Visibility
import util.*
import util.absfeatures.AbTestController

class ItemProRobot : BaseRobot() {
    private val proBadgeElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("pro_label"),
            iOSBy = VintedBy.accessibilityId("business_account_badge")
        )

    private val protectionFeeCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_header_service_fee_container"),
            iOSBy = VintedBy.accessibilityId("buyer_protection_fee")
        )

    private val shippingOptionsBlockElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_shipping_price_container"),
            iOSBy = VintedBy.accessibilityId("shipping_price")
        )

    private val sellerDetailsCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("view_accordion_header_cell"),
            iOSBy = VintedBy.accessibilityId("business_account_accordion_cell")
        )

    private val proSellerLegalInfoTopNoteElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_top_legal_note"),
            iOSBy = VintedBy.accessibilityId("business_account_legal_top_note")
        )

    private val proSellerLegalInfoBottomNoteElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_bottom_legal_note"),
            iOSBy = VintedBy.accessibilityId("business_account_legal_bottom_note")
        )

    private val proSellerLocationElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_location_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_location")}'")
        )

    private val proSellerEmailElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_email_text"),
            iOSBy = VintedBy.accessibilityId("email")
        )

    private val androidProSellerPhoneNumberElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("item_details_business_account_phone_text"))

    private val proSellerSiretNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_siret_text"),
            iOSBy = VintedBy.accessibilityId("siret_number")
        )

    private val proSellerLastSeenElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_last_logged_in_text"),
            iOSBy = VintedBy.accessibilityId("last_logged_on")
        )

    private val proSellerFollowingInfoElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_details_business_account_following_text"),
            iOSBy = VintedBy.iOSNsPredicateString("name BEGINSWITH '${IOS.getVoiceOverElementValue("voiceover_user_closet_followers")}'")
        )

    private val proSellerBuyerProtectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_header_actions_buyer_protection_info"),
            iOSBy = VintedBy.accessibilityId("buyer_protection_pro_money_back")
        )

    private val proSellerBuyerProtectionTransactionFlowTestElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("item_action_buyer_protection_info_body"),
            iOSBy = VintedBy.accessibilityId("buyer_protection_pro_money_back_transparent")
        )

    @Step("Assert pro badge is {visibility}")
    fun assertProBadgeVisibility(visibility: Visibility): ItemProRobot {
        VintedAssert.assertVisibilityEquals(proBadgeElement, visibility, "Pro badge should be $visibility")
        return this
    }

    @Step("Assert pro buyer protection is visible")
    fun assertProBuyerProtectionIsVisible(): ItemProRobot {
        VintedAssert.assertTrue(
            if (!AbTestController.isItemTransactionalFlowTransparencyOn()) {
                proSellerBuyerProtectionElement.withScrollIos().isVisible()
            } else {
                proSellerBuyerProtectionTransactionFlowTestElement.withScrollIos().isVisible()
            },
            "Pro buyer protection should be visible"
        )
        return this
    }

    @Step("Assert shipping options are displayed")
    fun assertShippingOptionsAreDisplayed(): ItemProRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(shippingOptionsBlockElement.isVisible(), "Shipping options should be displayed")
        }
        return this
    }

    @Step("Assert protection fee cell is {visibility}")
    fun assertProtectionFeeCellVisibility(visibility: Visibility): ItemProRobot {
        VintedDriver.scrollDownABit()
        if (!AbTestController.isItemTransactionalFlowTransparencyOn()) {
            IOS.doIfiOS { VintedDriver.scrollDown() }
        }
        VintedAssert.assertVisibilityEquals(protectionFeeCell, visibility, "Protection fee should be $visibility")
        return this
    }

    @Step("Click on seller details cell")
    fun clickOnSellerDetailsCell(): ItemProRobot {
        sellerDetailsCell.withScrollIos().click()
        return this
    }

    @Step("Check business seller info elements are visible")
    fun checkSellerInfoElements(): ItemProRobot {
        val softAssert = VintedSoftAssert()
        Android.scrollDownABit()
        softAssert.assertTrue(proSellerLocationElement.isVisible(), "Location should be displayed")
        softAssert.assertTrue(proSellerEmailElement.isVisible(), "Email should be displayed")
        Android.doIfAndroid { softAssert.assertTrue(!androidProSellerPhoneNumberElement.isVisible(), "Phone should not be displayed") }
        softAssert.assertTrue(proSellerSiretNumberElement.isVisible(), "Siret number text line should be displayed")
        softAssert.assertTrue(proSellerLastSeenElement.isVisible(), "Last seen info should be displayed")
        softAssert.assertTrue(proSellerFollowingInfoElement.isVisible(), "Following info should be displayed")
        softAssert.assertAll()
        return this
    }

    @Step("Check if pro seller legal text block is displayed")
    fun assertLegalInfoBlocksAreDisplayed(): ItemProRobot {
        IOS.doIfiOS { VintedDriver.scrollDownABit() }
        VintedAssert.assertTrue(proSellerLegalInfoTopNoteElement.isVisible(), "Pro seller legal info top note should be displayed")
        VintedAssert.assertTrue(proSellerLegalInfoBottomNoteElement.isVisible(), "Pro seller legal info bottom note should be displayed")
        return this
    }
}
