package test.basic

import RobotFactory.userProfileEditWorkflowRobot
import commonUtil.data.enums.VintedCountries
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile

@RunMobile(country = VintedCountry.INT)
@Feature("Localisation tests")
class LocalisationVisibleLanguageTests : BaseTest() {
    @Test(description = "Test that only expected languages are visible on app for FR user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_FR() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.FRANCE)
    }

    @Test(description = "Test that only expected languages are visible on app for DE user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_DE() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.GERMANY)
    }

    @Test(description = "Test that only expected languages are visible on app for AT user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_AT() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.AUSTRIA)
    }

    @Test(description = "Test that only expected languages are visible on app for LT user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_LT() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.LITHUANIA)
    }

    @Test(description = "Test that only expected languages are visible on app for NL user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_NL() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.NETHERLANDS)
    }

    @Test(description = "Test that only expected languages are visible on app for ES user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_ES() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.SPAIN)
    }

    @Test(description = "Test that only expected languages are visible on app for LU user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_LU() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.LUXEMBOURG)
    }

    @Test(description = "Test that only expected languages are visible on app for BE user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_BE() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.BELGIUM)
    }

    @Test(description = "Test that only expected languages are visible on app for IT user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_IT() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.ITALY)
    }

    @Test(description = "Test that only expected languages are visible on app for PT user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_PT() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.PORTUGAL)
    }

    @Test(description = "Test that only expected languages are visible on app for UK user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_UK() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.UNITED_KINGDOM)
    }

    @Test(description = "Test that only expected languages are visible on app for CZ user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_CZ() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.CZECH)
    }

    @Test(description = "Test that only expected languages are visible on app for US user")
    fun testOnlyExpectedLanguagesAreVisibleOnApp_US() {
        userProfileEditWorkflowRobot.createUserForCountryAndAssertOnlyExpectedLanguagesAreVisible(country = VintedCountries.USA)
    }
}
