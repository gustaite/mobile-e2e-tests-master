package robot.profile

import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class SellerPoliciesRobot : BaseRobot() {

    private val businessNameBlockElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("seller_policies_business_name"),
            iOSBy = VintedBy.accessibilityId("display_name")
        )

    private val siretNumberBlockElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("seller_policies_siret_number"),
            iOSBy = VintedBy.accessibilityId("siret_number")
        )

    private val sellerPoliciesTermsAndConditionsTextElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("seller_policies_terms_and_conditions_text"),
            iOSBy = VintedBy.accessibilityId("terms_title")
        )

    private val sellerPoliciesReturnInformationCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("seller_policies_return_policy_text"),
            iOSBy = VintedBy.accessibilityId("return_policy_title")
        )

    private val sellerPoliciesAdditionalInformationCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("seller_policies_additional_information_text"),
            iOSBy = VintedBy.accessibilityId("additional_information_title")
        )

    @Step("Check if business name and siret number are displayed")
    fun checkIfBusinessNameAndSiretNumberAreDisplayed(businessName: String, siretNumber: String): SellerPoliciesRobot {
        VintedAssert.assertTrue(businessNameBlockElement.withScrollIos().isVisible(), "Business name element should be visible")
        VintedAssert.assertTrue(siretNumberBlockElement.withScrollIos().isVisible(), "Siret number element should be visible")
        VintedAssert.assertTrue(VintedDriver.findElementByText(businessName).isVisible(), "Business name should be visible")
        VintedAssert.assertTrue(VintedDriver.findElementByText(siretNumber).isVisible(), "Siret number should be visible")
        return this
    }

    @Step("Check if all seller policies cells are displayed")
    fun checkIfSellerPoliciesCellsAreDisplayed(): SellerPoliciesRobot {
        val softAssert = VintedSoftAssert()
        softAssert.assertTrue(sellerPoliciesTermsAndConditionsTextElement.isVisible(), "Terms and conditions cell should be visible")
        softAssert.assertTrue(sellerPoliciesReturnInformationCell.isVisible(), "Return info cell should be visible")
        softAssert.assertTrue(sellerPoliciesAdditionalInformationCell.isVisible(), "Additional info cell should be visible")
        softAssert.assertAll()
        return this
    }
}
