package test.basic.b2c

import RobotFactory.deepLink
import RobotFactory.profileAboutTabRobot
import RobotFactory.userProfileClosetRobot
import RobotFactory.userProfileRobot
import api.values.UserBusinessAccountInfo
import commonUtil.testng.*
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import io.qameta.allure.TmsLinks
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@Feature("B2C tests")
@RunMobile(country = VintedCountry.INT)
class BusinessSellerProfileTests : BaseTest() {
    private val businessAccountInfo get() = UserBusinessAccountInfo.current

    @LoginToNewUser
    @Test(description = "Check if all elements are displayed in business profile closet tab")
    @TmsLinks(TmsLink("21457"), TmsLink("25089"))
    fun testElementsInBusinessAccountProfileTab() {
        val name = "Auto Shop"
        val username = "@" + businessUser!!.username

        deepLink.profile.goToUserProfile(businessUser!!.id)
        userProfileClosetRobot
            .assertProfileInfoElementContainsName(name)
            .assertProfileInfoElementContainsUsername(username)
            .assertProBadgeVisibility(visibility = Visibility.Visible)
            .checkBusinessAccountInfoElements(businessAccountInfo.email)
        userProfileRobot
            .checkShopBundleVisibility(visibility = Visibility.Invisible)
            .clickOnUserEmailAddressAndAssertOpenWithOptionsAreVisibleAndroid()
    }

    @LoginToNewUser
    @Test(description = "Check if all elements are displayed in business profile about tab")
    @TmsLink("25087")
    fun testElementsInBusinessAccountAboutTab() {
        val username = businessUser!!.username

        deepLink.profile.goToUserProfile(businessUser!!.id)
        userProfileRobot
            .openAboutTab()
            .assertUsernameInUserAboutProfile(username)
            .assertNavigationBarNameText(username)
        profileAboutTabRobot
            .assertBusinessIdentityVerifiedAndSiretNumberAreVisible(businessAccountInfo.siretNumber)
            .clickOnSellerPoliciesCell()
            .checkIfBusinessNameAndSiretNumberAreDisplayed(
                businessName = businessAccountInfo.registeredName,
                siretNumber = businessAccountInfo.siretNumber
            )
            .checkIfSellerPoliciesCellsAreDisplayed()
            .clickBack()
        userProfileClosetRobot
            .clickOnUserEmailAddressAndAssertOpenWithOptionsAreVisibleAndroid()
    }

    @LoginToBusinessUser
    @Test(description = "Test profile details when logged in to business account")
    @TmsLink("25352")

    fun testProfileDetailsWhenLoggedInToBusinessAccount() {
        deepLink.profile.goToMyProfile()
        userProfileClosetRobot
            .assertProBadgeVisibility(visibility = Visibility.Visible)
            .checkBusinessAccountInfoElements(businessAccountInfo.email)
        userProfileRobot
            .openAboutTab()
            .assertBusinessIdentityVerifiedAndSiretNumberAreVisible(businessAccountInfo.siretNumber)
            .clickOnSellerPoliciesCell()
            .checkIfBusinessNameAndSiretNumberAreDisplayed(
                businessName = businessAccountInfo.registeredName,
                siretNumber = businessAccountInfo.siretNumber
            )
            .checkIfSellerPoliciesCellsAreDisplayed()
    }
}
