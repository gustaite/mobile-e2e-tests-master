package robot.payments

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.IOS
import util.VintedDriver
import util.absfeatures.AbTestController.isKycEducationOn
import util.driver.VintedBy
import util.driver.VintedElement

class PaymentsIdentityRobot : BaseRobot() {

    // https://admin.vinted.net/features/1147
    // androidBy2 is because panda_android_kyc_new_form feature changes id it seems
    private val idProofSectionElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = {
                Android.findAllElement(
                    androidBy1 = VintedBy.id("id_proof_section_container"),
                    androidBy2 = VintedBy.id("kyc_container")
                )
            },
            iosElement = { IOS.findElementByTranslationKey("id_proof_section_general") }
        )

    private val kycEducationConfirmButtonElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("kyc_education_submit"),
            iOSBy = VintedBy.accessibilityId("kyc_education_confirm")
        )

    @Step("Assert verification form layout is visible")
    fun assertVerificationFormLayoutIsVisible() {
        VintedAssert.assertTrue(idProofSectionElement.isVisible(10), "Verification form should be visible")
    }

    @Step("Click on KYC Education confirm button")
    fun clickKycEducationConfirmButton(): PaymentsIdentityRobot {
        if (isKycEducationOn()) kycEducationConfirmButtonElement.click()
        return this
    }
}
