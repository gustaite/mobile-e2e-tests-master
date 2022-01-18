package robot

import commonUtil.asserts.VintedSoftAssert
import io.qameta.allure.Step
import util.Android
import util.Android.Companion.CELL_TITLE_FIELD_ID
import util.IOS
import util.VintedDriver
import util.absfeatures.AbTestController
import util.driver.VintedBy
import util.driver.VintedElement

class BuyerProtectionProRobot : BaseRobot() {

    private val buyerProtectionProIconElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_fee_education_icon"),
            iOSBy = VintedBy.accessibilityId("buyer-protection-pro-shield-64")
        )

    private val buyerProtectionFeeBottomSheetIconElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("bpf_bottom_sheet_image"),
            iOSBy = VintedBy.accessibilityId("buyer-protection-pro-shield-48")
        )

    private val buyerProtectionProScreenTitleElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_fee_education_title"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("checkout_service_fee_pro_label"))
        )

    private val buyerProtectionFeeBottomSheetTitleElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("bpf_bottom_sheet_title"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("checkout_service_fee_pro_label"))
        )

    private val buyerProtectionProGotItElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.id("checkout_fee_education_button"),
            iOSBy = VintedBy.accessibilityId("dialog_service_fee_explanation_close_title")
        )

    private val moneyBackGuaranteedProElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                CELL_TITLE_FIELD_ID,
                Android.getElementValue("money_back_guaranteed_pro")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("money_back_guaranteed_pro"))
        )

    private val securedPersonalDataProElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                CELL_TITLE_FIELD_ID,
                Android.getElementValue("secured_personal_data")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("secured_personal_data"))
        )

    private val ourFullSupportProElement: VintedElement get() =
        VintedDriver.findElement(
            androidBy = VintedBy.scrollableIdWithText(
                CELL_TITLE_FIELD_ID,
                Android.getElementValue("our_full_support")
            ),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("our_full_support"))
        )

    @Step("Android only: Check buyer protection pro screen elements are visible")
    fun checkBuyerProtectionProElements(): BuyerProtectionProRobot {
        Android.doIfAndroid {
            val softAssert = VintedSoftAssert()
            if (AbTestController.isBuyerProtectionFeeBottomSheetOn()) {
                softAssert.assertTrue(
                    buyerProtectionFeeBottomSheetIconElement.isVisible(),
                    "Buyer protection fee bottom sheet icon should be displayed"
                )
                softAssert.assertTrue(
                    buyerProtectionFeeBottomSheetTitleElement.isVisible(),
                    "Buyer protection fee bottom sheet screen title should be displayed"
                )
            } else {
                softAssert.assertTrue(
                    buyerProtectionProIconElement.isVisible(),
                    "Buyer protection pro icon should be displayed"
                )
                softAssert.assertTrue(
                    buyerProtectionProScreenTitleElement.isVisible(),
                    "Buyer protection pro screen title should be displayed"
                )
            }
            softAssert.assertTrue(
                moneyBackGuaranteedProElement.isVisible(),
                "Refund Policy Pro should be displayed"
            )
            softAssert.assertTrue(
                securedPersonalDataProElement.isVisible(),
                "Secure in-app payments should be displayed"
            )
            softAssert.assertTrue(ourFullSupportProElement.isVisible(), "Our support should be displayed")
            softAssert.assertAll()
        }
        return this
    }

    @Step("Android only: Close buyer protection screen")
    fun closeBuyerProtectionScreenAndroid(): CheckoutRobot {
        Android.doIfAndroid {
            if (!AbTestController.isBuyerProtectionFeeBottomSheetOn()) {
                buyerProtectionProGotItElement.click()
            } else {
                commonUtil.Util.retryUntil(
                    block = {
                        VintedDriver.pullDownToRefresh(relativeBeginYOffset = 0.4)
                        moneyBackGuaranteedProElement.isInvisible(2)
                    },
                    tryForSeconds = 10
                )
            }
        }
        return CheckoutRobot()
    }
}
