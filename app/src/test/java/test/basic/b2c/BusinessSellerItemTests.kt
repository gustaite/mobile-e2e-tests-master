package test.basic.b2c

import RobotFactory.deepLink
import RobotFactory.itemProRobot
import RobotFactory.itemRobot
import RobotFactory.userProfileClosetRobot
import RobotFactory.userProfileRobot
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@Feature("B2C tests")
@LoginToNewUser
@RunMobile(country = VintedCountry.INT)
class BusinessSellerItemTests : BaseTest() {

    @Test(description = "Check if all elements are displayed in business account item screen")
    @TmsLinks(TmsLink("22303"), TmsLink("22308"))
    fun testElementsInBusinessAccountItemScreen() {
        val name = "Auto Shop"
        deepLink.profile.goToUserProfile(businessUser!!.id)
        userProfileRobot
            .assertItemIsVisible()
            .openFirstItem()
            .assertMessageButtonIsVisibleInItemScreen()
            .assertBuyButtonVisibility(shouldBeVisible = true)
        userProfileClosetRobot
            .assertProfileInfoElementContainsName(name)
            .assertProBadgeVisibility(visibility = Visibility.Visible)
        itemProRobot
            .assertProBuyerProtectionIsVisible()
        // TODO add click clickOnProBuyerProtectionLink (missing ids), assertScreenName() and closeRefundSafetyPolicy()
        itemProRobot
            .assertShippingOptionsAreDisplayed()
            .assertProtectionFeeCellVisibility(visibility = Visibility.Visible)
            .clickOnSellerDetailsCell()
            .checkSellerInfoElements()
            .assertLegalInfoBlocksAreDisplayed()
        // TODO missing clicks on links in legal info blocks (missing ids)
        itemRobot
            .assertBundleButtonVisibilityInUserItemsTab(visibility = Visibility.Invisible)
    }

    @Test(description = "Check elements in business account item checkout screen")
    @TmsLink("22306") // TODO update accordingly to "22306"
    fun testBusinessAccountItemCheckoutScreen() {
        deepLink.profile.goToUserProfile(businessUser!!.id)
        userProfileRobot
            .openFirstItem()
            .clickBuyButton()
            .assertBuyerProtectionFeeIsDisplayed()
            .clickOnBuyerProtectionInfoAndroid()
            .checkBuyerProtectionProElements()
            .closeBuyerProtectionScreenAndroid()
            .assertProTermsAgreementIsDisplayed()
    }
}
