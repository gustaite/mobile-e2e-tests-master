package robot.workflow

import RobotFactory.deepLink
import RobotFactory.languageSelectionRobot
import RobotFactory.userProfileEditRobot
import RobotFactory.userProfileRobot
import RobotFactory.welcomeRobot
import commonUtil.testng.config.ConfigManager.portal
import api.factories.UserFactory
import commonUtil.asserts.VintedAssert
import commonUtil.asserts.VintedSoftAssert
import commonUtil.data.enums.VintedCountries
import io.qameta.allure.Step
import robot.BaseRobot
import util.IOS
import util.values.ToggleValue

class UserProfileEditWorkflowRobot : BaseRobot() {

    private val countryTitle = portal.country.title

    fun clickOnMyLocationAndSelectCountry(): UserProfileEditWorkflowRobot {
        userProfileEditRobot
            .clickOnMyLocation()
            .selectCountryInLocation(countryTitle)

        return this
    }

    fun selectCityInMyLocation(): UserProfileEditWorkflowRobot {
        userProfileEditRobot
            .sendLocationCity()
            .selectCityInLocation()

        return this
    }

    fun assertMyLocationToggleValueAndLocationCitySubtitleVisibility(toggleValue: ToggleValue): UserProfileEditWorkflowRobot {
        userProfileEditRobot
            .assertLocationSwitchValue(toggleValue)
            .assertLocationSubtitleContainsCity(countryTitle)
            .saveSection.clickSave()

        return this
    }

    fun openAboutTabAndAssertCityIsVisible(): UserProfileEditWorkflowRobot {
        deepLink.profile.goToMyProfile()
        userProfileRobot.openAboutTab().scrollDown().userInfo
            .assertCityIsVisible(countryTitle)

        return this
    }

    fun openAboutTabAndAssertCityIsNotVisible(): UserProfileEditWorkflowRobot {
        deepLink.profile.goToMyProfile()
        userProfileRobot.openAboutTab().scrollDown().userInfo
            .assertCityIsNotVisible(countryTitle)

        return this
    }

    @Step("Create user for country '{country.code}' and assert only expected languages are visible")
    fun createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country: VintedCountries) {
        val user = UserFactory.createRandomUser(country = country)
        IOS.doIfiOS { // Previous test logout on iOS sometimes happens on new test (if run tests on one device), this wait should prevent that
            VintedAssert.assertTrue(welcomeRobot.isShowLoginOptionsVisible(20), "Welcome screen was not visible")
        }
        deepLink.selectLanguageiOS(country)
        deepLink.loginToAccount(user)

        val softAssert = VintedSoftAssert()
        val languagesTitles = country.getExpectedLanguages()
        val languagesTitlesAsOneString = languagesTitles.joinToString(",")
        deepLink.goToSettings().openLanguageSelection()
        softAssert.assertEquals(
            languageSelectionRobot.getLanguagesCount(),
            languagesTitles.count(),
            "Language count does not match. Expected languages: $languagesTitlesAsOneString"
        )

        languagesTitles.forEach { language ->
            softAssert.assertTrue(
                languageSelectionRobot.isLanguageVisible(language = language),
                "Language '$language' was not available for selection"
            )
        }
        softAssert.assertAll()
    }
}
