package util.values

import api.data.models.getTextByUserCountry
import commonUtil.data.VintedCountriesTextValue
import util.AppTexts.userByCountry

class UploadFormElementTexts {
    companion object {
        private val suggestedElementTextValues = VintedCountriesTextValue(
            fr = "Suggestions",
            de = "Vorgeschlagen",
            pl = "Sugerowane",
            uk = "Suggested",
            us = "Suggested",
            lt = "Siūlomi",
            cz = "Návrhy"
        )

        val suggestedElementText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = suggestedElementTextValues)

        private val suggestedCategoryElementTextValues = VintedCountriesTextValue(
            fr = "Blazers",
            de = "Blazer",
            pl = "Żakiety",
            uk = "Blazers",
            us = "Blazers",
            lt = "Švarkeliai",
            cz = "Saka"
        )

        val suggestedCategoryElementText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = suggestedCategoryElementTextValues)

        private val suggestedCategoryPathElementTextValues = VintedCountriesTextValue(
            fr = "Femmes > Vêtements > Blazers & tailleurs",
            de = "Damen > Kleidung > Blazer & Anzüge",
            pl = "Kobiety > Ubrania > Żakiety i garnitury",
            uk = "Women > Clothes > Suits & blazers",
            us = "Women > Clothes > Suits & blazers",
            lt = "Moterims > Drabužiai > Švarkeliai ir kostiumėliai",
            cz = "Ženy > Oblečení > Kostýmy a saka"
        )

        val suggestedCategoryPathElementText: String
            get() = userByCountry.getTextByUserCountry(countriesTextValue = suggestedCategoryPathElementTextValues)
    }
}
