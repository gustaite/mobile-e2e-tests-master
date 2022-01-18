package robot.profile.settings

import RobotFactory.editablePoliciesSettingsRobot
import RobotFactory.languageSelectionRobot
import api.controllers.GlobalAPI
import api.data.responses.VintedLanguage
import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedCountries
import io.qameta.allure.Step
import robot.BaseRobot
import robot.notificationsettings.NotificationSettingsRobot
import robot.profile.PaymentsScreenRobot
import robot.profile.ShippingScreenRobot
import robot.profile.UserProfileEditRobot
import util.*
import util.base.BaseTest.Companion.loggedInUser
import util.EnvironmentManager.isAndroid
import util.driver.VintedBy
import util.driver.VintedElement
import util.driver.WaitFor
import util.reporting.AllureReport

class SettingsRobot : BaseRobot() {
    companion object {
        private const val iOSLogOutAccessibilityId = "logout"
    }

    private val logoutElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_settings_logout"),
            iOSBy = VintedBy.accessibilityId(
                iOSLogOutAccessibilityId
            )
        )

    private val modalYesButtonIos: VintedElement get() = VintedDriver.findElement(iOSBy = VintedBy.accessibilityId("logout_modal_yes"))

    private fun languageCellElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
        )

    private val languageSettingsElement: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_preferences_language"), iOSBy = VintedBy.accessibilityId("settings_language"))

    private fun selectedLanguageTextElement(text: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = text, scroll = false),
            iOSBy = VintedBy.iOSTextByBuilder(text = text, onlyVisibleInScreen = true)
        )

    private val pushNotificationSettingsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_preferences_notifications_push"),
            iOSBy = VintedBy.accessibilityId("settings_push_notifications_button")
        )

    private val emailNotificationSettingsElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_preferences_notifications_email"),
            iOSBy = VintedBy.accessibilityId("settings_email_notifications_button")
        )

    private val profileDetailsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_settings_profile_details"),
            iOSBy = VintedBy.accessibilityId("profile_details")
        )

    private val paymentSettingsButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_settings_payments"), iOSBy = VintedBy.accessibilityId("settings_payments_cell_title"))

    private val shippingSettingsButton: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_settings_shipping"), iOSBy = VintedBy.accessibilityId("settings_shipping_options"))

    private val dataSettingsButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("settings_data_policy"),
            iOSBy = VintedBy.accessibilityId("user_settings_data_settings_button")
        )

    private val accountSettingsButton: VintedElement get() = VintedDriver.findElement(androidBy = VintedBy.scrollableId("user_settings_account"), iOSBy = VintedBy.accessibilityId("account_settings"))

    private val securityButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("user_settings_security"),
            iOSBy = VintedBy.accessibilityId("security")
        )

    private val businessAccountPoliciesCell: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("user_settings_business_terms"),
            iOSBy = VintedBy.accessibilityId("business_account_policies")
        )

    private val settingsActionBarElement: VintedElement get() = VintedDriver.findElement(
        androidBy = VintedBy.androidTextByBuilder(text = Android.getElementValue("user_settings_page_title"), scroll = false),
        iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeNavigationBar' && (name == '${IOS.getElementValue("settings")}')")
    )

    @Step("Click logout")
    fun clickLogout() {
        AllureReport.addScreenshot()
        logoutElement.withWait(WaitFor.Visible, 2).withScrollIos().click()

        if (isAndroid) closeModal() else modalYesButtonIos.click()
    }

    @Step("Open push notification settings screen")
    fun openPushNotificationSettings() {
        pushNotificationSettingsElement.tap()
    }

    @Step("Open email notification settings screen")
    fun openEmailNotificationSettings(): NotificationSettingsRobot {
        emailNotificationSettingsElement.tap()
        return NotificationSettingsRobot()
    }

    @Step("Open payments settings")
    fun openPaymentsSettings(): PaymentsScreenRobot {
        paymentSettingsButton.click()
        return PaymentsScreenRobot()
    }

    @Step("Open shipping settings")
    fun openShippingSettings(): ShippingScreenRobot {
        shippingSettingsButton.click()
        return RobotFactory.shippingScreenRobot
    }

    @Step("Open language selection")
    fun openLanguageSelection(): LanguageSelectionRobot {
        languageSettingsElement.withScrollIos().click()
        return languageSelectionRobot
    }

    @Step("Open profile details")
    fun openProfileDetails(): UserProfileEditRobot {
        profileDetailsButton.click()
        return UserProfileEditRobot()
    }

    @Step("Open data settings")
    fun openDataSettings(): DataSettingsRobot {
        dataSettingsButton.withScrollIos().click()

        return DataSettingsRobot()
    }

    @Step("Open security screen")
    fun openSecurityScreen() {
        securityButton.click()
    }

    @Step("Open account settings")
    fun openAccountSettings(): UserAccountSettingsRobot {
        accountSettingsButton.click()
        return UserAccountSettingsRobot()
    }

    @Step("Assert language has changed to {language.title}")
    fun assertLanguageHasChangedTo(language: VintedLanguage) {
        val country = VintedCountries.getCountryFromLanguage(languageCode = language.code)!!
        languageSettingsElement.withScrollIos()
        val settingsLanguageKey = "settings_language" // same key for both iOS and Android
        val expectedSelectedLanguage = if (isAndroid) language.titleShort else "${language.title} (${language.titleShort})"

        commonUtil.reporting.Report.addMessage(
            """
            Assertion is made by checking that language cell in the settings has different translation
            and '$expectedSelectedLanguage' title on the right
            """.trimIndent()
        )

        /**
         Texts in [Android.getElementValue] are immutable and saved once
         So it returns french translation here even though app language and portal locale could be different now
         */
        val oldTitle = Android.getElementValue(settingsLanguageKey)
        VintedAssert.assertFalse(languageCellElement(oldTitle).isVisible(1), "Element with $oldTitle text should not be visible")

        /**
         Getting new translation by calling API, new translation is ensured by different accept-language header.
         See [api.util.BaseHeadersInterceptor.intercept] there new locale is set
         */
        val newText = getTexts(country).entries.find { it.key == settingsLanguageKey }!!.value
        VintedAssert.assertTrue(languageCellElement(newText).isVisible(1), "Element with $newText should be visible")

        VintedAssert.assertTrue(selectedLanguageTextElement(expectedSelectedLanguage).isVisible(1), "Selected language should be $expectedSelectedLanguage")
    }

    @Step("[API] Get {language.code} translation texts")
    private fun getTexts(country: VintedCountries): Map<String, String> =
        if (isAndroid) GlobalAPI.getAndroidTexts(user = loggedInUser, country = country) else GlobalAPI.getIosTexts(
            user = loggedInUser,
            country = country
        )

    @Step("Open account policies settings")
    fun openPoliciesSettings(): EditablePoliciesSettingsRobot {
        businessAccountPoliciesCell.click()
        return editablePoliciesSettingsRobot
    }

    @Step("Is settings action bar element visible")
    fun isSettingsActionBarVisible(): Boolean {
        return settingsActionBarElement.isVisible(2)
    }
}
