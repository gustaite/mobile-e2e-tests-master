package robot

import api.data.models.faq.VintedFaqEntryValue
import api.data.models.getTextByUserCountry
import commonUtil.asserts.VintedAssert
import commonUtil.data.VintedCountriesTextValue
import io.qameta.allure.Step
import util.*
import util.driver.VintedBy
import util.driver.VintedElement
import commonUtil.extensions.escapeApostrophe
import util.AppTexts.userByCountry
import robot.webview.WebViewRobot

class HelpCenterRobot : BaseRobot() {

    private val helpCenterTitleElement: VintedElement
        get() = VintedDriver.findElement(VintedBy.id("help_center_label_title"), VintedBy.accessibilityId("faq_questions_section_title"))

    private val sectionElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            VintedBy.id("faq_cell"),
            VintedBy.className("XCUIElementTypeCell")
        )

    private val transactionItemTitleElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableSetWithParentAndChild(
                "hc_transaction",
                "view_cell_title"
            )
        )

    private fun helpSectionElement(name: String) = VintedDriver.findElement(
        androidBy = VintedBy.scrollableIdWithText("view_cell_title", name),
        iOSBy = VintedBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name CONTAINS '${name.escapeApostrophe()}'")
    )

    @Step("Check if help center screen is visible")
    fun assertHelpCenterIsDisplayed(): HelpCenterRobot {
        VintedAssert.assertTrue(helpCenterTitleElement.isVisible(15), "Help center title should be visible")
        return this
    }

    @Step("Check if Help center screen title text is '{text}'")
    fun assertHelpCenterScreenText(text: String): HelpCenterRobot {
        helpCenterTitleElement.text.let {
            VintedAssert.assertEquals(it, text, "Help center screen title element actual: $it, expected: $text")
        }
        return this
    }

    @Step("Open random help center section")
    fun openHelpCenterSection(): WebViewRobot {
        while (VintedElement.isListVisible({ sectionElementList })) {
            sectionElementList.take(8).random().click()
        }
        return WebViewRobot()
    }

    @Step("Assert transaction item title visible (only Android)")
    fun assertTransactionItemTitleVisible(): HelpCenterRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(transactionItemTitleElement.withWait().isVisible(), "Transaction item title should be visible on help center")
        }
        return this
    }

    @Step("Click help section with name '{helpSectionText.title}'")
    fun clickHelpSection(helpSectionText: HelpSectionTexts): HelpCenterRobot {
        helpSectionElement(helpSectionText.title).let {
            VintedAssert.assertTrue(it.isVisible(), "Help section with name should be visible")
            it.click()
        }
        return this
    }
}

enum class HelpSectionTexts(val title: String, val faqEntry: VintedFaqEntryValue) {
    RECEIVED_NOT_EXPECTED(
        userByCountry.getTextByUserCountry(
            VintedCountriesTextValue(
                fr = "L'article n'est pas conforme",
                de = "Ich habe etwas anderes erhalten als erwartet",
                pl = "Otrzymany przedmiot nie spełnia twoich oczekiwań",
                uk = "I didn't receive what I expected",
                us = "I didn't receive what I expected",
                cz = "Nedorazilo mi to, co jsem čekal/a",
                lt = "Gavau ne tai, ko tikėjausi"
            )
        ),
        faqEntry = VintedFaqEntryValue.NOT_EXISTING_FAQ_ENTRY
    ),
    IT_IS_DAMAGED(
        userByCountry.getTextByUserCountry(
            VintedCountriesTextValue(
                fr = "L’article est endommagé (tâches/trous)",
                de = "Der Artikel ist beschädigt",
                pl = "Przedmiot jest uszkodzony (plamy/dziury)",
                uk = "It's damaged (stains/holes)",
                us = "It's damaged (stains/holes)",
                cz = "Je poškozený: má skvrny/díry, nesprávnou barvu/velikost",
                lt = "Prekė pažeista (dėmėta / skylėta), ne ta spalva / dydis"
            )
        ),
        faqEntry = VintedFaqEntryValue.IT_IS_DAMAGED
    )
}
