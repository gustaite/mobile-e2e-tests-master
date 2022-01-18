package test.payments

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.settingsRobot
import RobotFactory.shipmentWorkflowRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.GlobalAPI
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.user.userApi
import commonUtil.data.enums.*
import commonUtil.testng.config.PortalFactory
import commonUtil.data.enums.VintedCarriers.Companion.restrictedCarriersIds
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry.*
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Issue
import io.qameta.allure.Issues
import io.qameta.allure.TmsLink
import util.absfeatures.AbTestController
import util.absfeatures.ShippingCarriersController

@LoginToNewUser
@Feature("Shipping settings tests")
class ShippingSettingsTests : BaseTest() {

    @Issues(Issue("MARIOS-540"), Issue("BUGS-84"))
    @RunMobile(country = PAYMENTS, message = "Test for payments countries")
    @Test(description = "Check preferred choice shipping carriers value")
    fun testPreferredChoiceCarriersInShippingSettings() {
        navigationRobot.openProfileTab()
        deepLink
            .goToSettings()
            .openShippingSettings()
            .assertShippingCarriersName()
            .assertAllPreferredCarriersAreEnabled()
            .openMyAddressScreen()
            .insertFullAddressOrPersonalDetailsInfo(loggedInUser.billingAddress)
            .saveFullAddress()
            .assertShippingCarriersName()
            .assertAllPreferredCarriersAreEnabled()

        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.DE)) workflowRobot.changeAddressToATAndCheckCarriers()
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for BE country in INT market")
    fun testPreferredChoiceCarriersInINT_BE() {
        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.BE,
            workflowRobot.getVisibleCarriersList(
                VintedPreferredChoiceShippingCarriers.BE, VintedMandatoryShippingCarriers.BE
            )
        )
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for ES country in INT market")
    fun testPreferredChoiceCarriersInINT_ES() {
        val carriersListBasedOnFeatures = listOf(
            Pair(VintedPreferredChoiceShippingCarriers.ES_SEUR_HOME_FEATURE, AbTestController.isSeurHomeEsOn()),
            Pair(VintedPreferredChoiceShippingCarriers.ES_SEUR_SHOP_FEATURE, AbTestController.isSeurShopEsOn())
        )

        val preferredChoiceShippingCarriers =
            ShippingCarriersController.getShippingRoutesCarriersBasedOnFeatureFlag(
                carriersListBasedOnFeatures,
                VintedPreferredChoiceShippingCarriers.ES
            ) as VintedPreferredChoiceShippingCarriers

        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.ES,
            workflowRobot.getVisibleCarriersList(
                preferredChoiceShippingCarriers, VintedMandatoryShippingCarriers.ES
            )
        )
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for LU country in INT market")
    fun testPreferredChoiceCarriersInINT_LU() {
        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.LU,
            workflowRobot.getVisibleCarriersList(
                VintedPreferredChoiceShippingCarriers.LU, VintedMandatoryShippingCarriers.LU
            )
        )
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for NL country in INT market")
    fun testPreferredChoiceCarriersInINT_NL() {
        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.NL,
            workflowRobot.getVisibleCarriersList(
                VintedPreferredChoiceShippingCarriers.NL, VintedMandatoryShippingCarriers.NL
            )
        )
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for IT country in INT market")
    fun testPreferredChoiceCarriersInINT_IT() {
        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.IT,
            workflowRobot.getVisibleCarriersList(
                VintedPreferredChoiceShippingCarriers.IT, VintedMandatoryShippingCarriers.IT
            )
        )
    }

    @Issue("BUGS-84")
    @RunMobile(country = INT, message = "Test for INT only")
    @Test(description = "Check preferred choice shipping carriers value for PT country in INT market")
    fun testPreferredChoiceCarriersInINT_PT() {
        shipmentWorkflowRobot.changeShippingAddressAndCheckPreferredChoiceShippingCarriers(
            VintedShippingAddress.PT,
            workflowRobot.getVisibleCarriersList(
                VintedPreferredChoiceShippingCarriers.PT, VintedMandatoryShippingCarriers.PT
            )
        )
    }

    @RunMobile(country = PAYMENTS_EXCEPT_US_CZ, message = "Test for payments countries which have at least one optional shipping carrier")
    @Test(description = "Check if switch toggle for preferred carriers works as expected")
    fun testCarrierPreferenceSwitch() {
        val globalCarriers = GlobalAPI.getCarriers(user = loggedInUser)
        val notMandatoryCarriers = loggedInUser.userApi.getCarrierPreferences().filter { it.carrierId in globalCarriers.filter { carrier -> !carrier.mandatory }.map { globalCarrier -> globalCarrier.id } }
        val preferredChoiceCarrierId = notMandatoryCarriers.first().carrierId
        val turnedOffCarrierName = globalCarriers.first { it.id == preferredChoiceCarrierId }.name!!
        navigationRobot
            .openProfileTab()
        deepLink
            .goToSettings()
            .openShippingSettings()
            .assertAllPreferredCarriersAreEnabled()
            .turnOFFPreferredCarrier(turnedOffCarrierName)
            .clickBack()
        settingsRobot
            .openShippingSettings()
        AssertApi.assertApiResponseWithWait(
            actual = { workflowRobot.getExpectedCarrierPreferencesForToggle(preferredChoiceCarrierId) },
            expected = false,
            errorMessage = "Expected carrier should be disabled but is enabled"
        )
    }

    @RunMobile(country = PAYMENTS_EXCEPT_US, message = "Test for payments countries which have at least one optional shipping carrier")
    @Test(description = "Check if seller's turned off carrier is not visible in checkout for buyer")
    fun testTurnedOffCarrierIsNotVisibleInCheckout() {
        val item = ItemAPI.uploadItem(otherUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        val turnedOffCarrierId = otherUser.userApi.getCarrierPreferences().map { it.carrierId }.filterNot { it in restrictedCarriersIds }.random()
        otherUser.userApi.updateCarrierPreference(turnedOffCarrierId, false)

        deepLink
            .item.goToItem(item)
            .clickBuyButton()
            .assertAllPricesAreDisplayed()
            .assertTurnedOffCarrierIsNotVisible(turnedOffCarrierId, item)
    }

    @RunMobile(country = PAYMENTS, message = "Test for payment countries only")
    @Test(description = "Add new address in settings and check if change is saved in checkout")
    @TmsLink("93")
    fun testAddNewAddressInSettingsAndAssertItIsVisibleInCheckout() {
        deepLink
            .goToSettings()
            .openShippingSettings()
            .openMyAddressScreen()
            .insertFullAddressOrPersonalDetailsInfo(loggedInUser.billingAddress)
            .saveFullAddress()
            .assertShippingAddressOrPersonalDetailsInfo(loggedInUser.billingAddress)
        deepLink
            .item.goToItem(withItemsUserItem)
            .clickBuyButton()
            .assertAddressOrPhoneNumberIsVisible()
    }
}
