package robot.profile

import RobotFactory.addressRobot
import RobotFactory.workflowRobot
import api.AssertApi
import api.controllers.GlobalAPI.Companion.getCarriers
import api.controllers.user.userApi
import io.qameta.allure.Step
import robot.BaseRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement
import api.values.preferredChoiceShippingCarriers
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.data.enums.*
import commonUtil.testng.config.PortalFactory
import util.absfeatures.AbTestController.isAddressFormUnificationIsOn

class ShippingScreenRobot : BaseRobot() {

    private val shippingCarrierTitleElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.scrollableSetWithParentAndChild("carrier_settings_cell", "view_cell_title"),
            iOSBy = VintedBy.iOSNsPredicateString("name == 'shipping_carrier'")
        )

    private val addShippingAddressButton: VintedElement
        get() = VintedDriver.findElement(VintedBy.scrollableId("address_block_new_address"), VintedBy.accessibilityId("add_address_block"))

    private val shippingCarrierToggleElementAndroid: List<VintedElement>
        get() = VintedDriver.findElementList(androidBy = VintedBy.scrollableId("view_toggle_switch"))

    private fun shippingCarrierToggleElementIos(carrierName: String): VintedElement =
        VintedDriver.findElement(iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeOther' AND (name == 'shipping_carrier_toggle_$carrierName')"))

    private val disableButtonElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("modal_primary_button", "shipping_options_disable_carrier_disable")

    private fun shippingCarrierTitleElement(name: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = name.trim(), scroll = true),
            iOSBy = VintedBy.accessibilityId(name)
        )

    private val userFullNameElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "address_block_existing_address",
                Android.CELL_TITLE_FIELD_ID
            ),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name == 'add_address_block'`]/XCUIElementTypeStaticText[`name CONTAINS '${loggedInUser.billingAddress.fullName}'`]")
        )

    private val addressElementAndroid: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild(
                "address_block_existing_address",
                Android.CELL_BODY_FIELD_ID
            )
        )

    private val postalCityElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { addressElementAndroid },
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name == 'add_address_block'`]/XCUIElementTypeStaticText[`name CONTAINS '${loggedInUser.billingAddress.postalCode}'`]")
        )

    private val addressLinesElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { addressElementAndroid },
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name == 'add_address_block'`]/XCUIElementTypeStaticText[`name CONTAINS '${loggedInUser.billingAddress.address1}'`]")
        )

    private val phoneNumberElement: VintedElement
        get() = VintedDriver.findElement(
            androidElement = { addressElementAndroid },
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCell[`name == 'add_address_block'`]/XCUIElementTypeStaticText[`name CONTAINS '${loggedInUser.billingAddress.getPhoneNumber()}'`]")
        )

    @Step("Assert {country} expected carriers value: {shippingCarriers}")
    fun assertShippingCarriersName(
        shippingCarriers: List<String> = workflowRobot.getVisibleCarriersList(),
        country: String = loggedInUser.preferredChoiceShippingCarriers().country
    ): ShippingScreenRobot {
        val softAssert: VintedSoftAssert
        val expectedCarriersList = getExpectedCarriersNamesList()
        IOS.scrollDown()
        val visibleCarriersList: List<String>
        assertCarrierWithNameIsVisible(shippingCarriers).let {
            visibleCarriersList = it.first
            softAssert = it.second
        }
        val visibleCarriersCount = if (isAndroid) visibleCarriersList.size else shippingCarrierTitleElementList.size

        softAssert.assertEquals(
            visibleCarriersList.sorted(), shippingCarriers.sorted(),
            "Shipment carrier provider list given from enum class: $shippingCarriers but is visible in UI calculated by using enum: $visibleCarriersList"
        )
        softAssert.assertEquals(
            shippingCarriers.sorted(), expectedCarriersList.sorted(),
            "Given carriers list to compare from enum class: $shippingCarriers and expected from api: $expectedCarriersList. " +
                "Carriers values might differ in VintedPreferredChoiceShippingCarriers.kt or VintedMandatoryShippingCarriers.kt $country carriers List."
        )
        softAssert.assertEquals(
            visibleCarriersCount, expectedCarriersList.size,
            "Expected carriers count from api: ${expectedCarriersList.size} actual count from UI: $visibleCarriersCount. " +
                "Shipping carriers might be missing in VintedPreferredChoiceShippingCarriers.kt or VintedMandatoryShippingCarriers.kt $country carriers List."
        )
        softAssert.assertAll()
        return this
    }

    private fun getExpectedCarriersNamesList(): List<String> {
        val expectedCarriersList = mutableListOf<String>()
        val carrierPreferencesList = loggedInUser.userApi.getCarrierPreferences(true)
        val carriersList = getCarriers(user = loggedInUser)
        carrierPreferencesList.forEach { carrierPreference ->
            val carrierName = carriersList.first { it.id == carrierPreference.carrierId }.name!!
            expectedCarriersList.add(carrierName)
        }
        return expectedCarriersList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))
    }

    @Step("Assert carriers and titles are visible")
    private fun assertCarrierWithNameIsVisible(preferredChoiceShippingCarriers: List<String>): Pair<List<String>, VintedSoftAssert> {
        val softAssert = VintedSoftAssert()
        val visibleCarriersList = mutableListOf<String>()
        preferredChoiceShippingCarriers.forEach { name ->
            val carrier = shippingCarrierTitleElement(name)
            softAssert.assertTrue(carrier.isVisible(), "Expected carrier '$name' should be visible")
            visibleCarriersList.add(carrier.text)
        }
        return Pair(visibleCarriersList.toList(), softAssert)
    }

    @Step("Open shipping address screen for the first time")
    fun openMyAddressScreen(): AddressRobot {
        addShippingAddressButton.click()
        return addressRobot
    }

    @Step("Check if all preferred carriers are turned ON")
    fun assertAllPreferredCarriersAreEnabled(expected: Boolean = true): ShippingScreenRobot {
        val carrierPreferencesList = loggedInUser.userApi.getCarrierPreferences()
        carrierPreferencesList.forEach { carrierPreference ->
            val carriersList = getCarriers(user = loggedInUser).filter { it.id == carrierPreference.carrierId }.map { it.name }
            AssertApi.assertApiResponseWithWait(
                actual = { carrierPreference.enabled },
                expected = expected,
                errorMessage = "$carriersList carrier preference toggle switch should be enabled = $expected but found enabled = ${carrierPreference.enabled}"
            )
            commonUtil.reporting.Report.addMessage("$carriersList toggle switch is enabled =  $expected}")
        }
        return this
    }

    @Step("Turn OFF one preferred carrier")
    fun turnOFFPreferredCarrier(carrierName: String): ShippingScreenRobot {
        Android.doIfAndroid {
            Android.scrollDownABit()
            val preferredChoiceCarrierToggle = shippingCarrierToggleElementAndroid.first { it.isElementEnabled() }
            preferredChoiceCarrierToggle.tap()
        }
        IOS.doIfiOS { shippingCarrierToggleElementIos(carrierName).withScrollIos().click() }
        disableButtonElement.click()
        return this
    }

    @Step("Assert shipping address or personal details are visible")
    fun assertShippingAddressOrPersonalDetailsInfo(address: VintedBillingAddress): ShippingScreenRobot {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.PL) && !isAddressFormUnificationIsOn()) {
            assertAddressFullName(address.fullName)
            assertPhoneNumber(address.getPhoneNumber())
        } else {
            assertAddressFullName(address.fullName)
            assertAddressLines(address)
            assertPostalCity(address)
        }
        return this
    }

    @Step("Assert phone number is visible")
    private fun assertPhoneNumber(phoneNumber: String) {
        phoneNumberElement.let {
            VintedAssert.assertTrue(it.isVisible(), "Phone number element should be visible")
            val text = it.text
            VintedAssert.assertTrue(text.contains(phoneNumber), "Address block should contains Phone number: $phoneNumber. Actual text: $text")
        }
    }

    @Step("Assert address full name is {fullName}")
    private fun assertAddressFullName(fullName: String) {
        userFullNameElement.let {
            VintedAssert.assertTrue(it.withWait().isVisible(), "Address user full name element should be visible")
            val text = it.text
            VintedAssert.assertTrue(text.contains(fullName), "Address user full name does not match. Expected: $fullName. Actual: $text")
        }
    }

    @Step("Assert postal and city values are visible")
    private fun assertPostalCity(address: VintedBillingAddress) {
        postalCityElement.let {
            VintedAssert.assertTrue(it.isVisible(), "Postal city element should be visible")
            val text = it.text
            VintedAssert.assertTrue(text.contains(address.postalCode), "Address block should contains Postal code. Actual text: $text")
            VintedAssert.assertTrue(text.contains(address.city.replace(", NY", ""), ignoreCase = true), "Address block should contains City. Actual text: $text")
        }
    }

    @Step("Assert address lines are visible")
    private fun assertAddressLines(address: VintedBillingAddress) {
        addressLinesElement.let {
            VintedAssert.assertTrue(it.isVisible(), "Address lines element should be visible")
            val text = it.text
            VintedAssert.assertTrue(text.contains(address.address1), "Address block should contains Address 1. Actual text: $text")
            VintedAssert.assertTrue(text.contains(address.address2), "Address block should contains Address 2. Actual text: $text")
        }
    }
}
