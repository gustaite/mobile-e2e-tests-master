package util

import commonUtil.asserts.VintedAssert
import commonUtil.data.enums.VintedCountries
import commonUtil.data.enums.VintedLocales
import commonUtil.extensions.changeCommaToDot
import commonUtil.extensions.changeSimpleSpaceToSpecial
import commonUtil.extensions.numeric
import commonUtil.extensions.removeSpecialSpaceAndMinus
import util.AppTexts.userByCountry
import util.EnvironmentManager.isAndroid
import util.base.BaseTest.Companion.systemConfiguration
import java.text.NumberFormat
import java.util.*

class PriceFactory {
    companion object {
        fun getFormattedPriceWithCurrencySymbol(price: Double, replaceSpaceCharToSpec: Boolean = false): String {
            return getFormattedPriceWithCurrencySymbol(price.numeric(), replaceSpaceCharToSpec)
        }

        private fun getAmountFormatAndLocale(): Pair<String, Locale> {
            val amountFormat = when (userByCountry.country) {
                VintedCountries.NETHERLANDS -> "\u20ac %.2f"
                VintedCountries.UNITED_KINGDOM -> "£%.2f"
                VintedCountries.USA -> "US$%.2f"
                else -> systemConfiguration.localeConfiguration.currency.amountFormat
            }
            return Pair(amountFormat, userByCountry.country.locale)
        }

        fun getFormattedPriceWithCurrencySymbol(price: String, replaceSpaceCharToSpec: Boolean = false): String {
            val response = getAmountFormatAndLocale()
            val amountFormat = response.first
            val locale = response.second
            val priceAsDouble = price.changeCommaToDot().toDouble()
            val numberFormat = NumberFormat.getCurrencyInstance(locale)
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2

            return when (isAndroid) {
                true -> when (locale) {
                    VintedLocales.CZECH.value -> formatWithCurrencyInstance(numberFormat, priceAsDouble, replaceSpaceCharToSpec)
                    else -> formatWithAmountFormat(amountFormat, locale, priceAsDouble)
                }
                false -> when (locale) {
                    Locale.ITALY, VintedLocales.AUSTRIA.value, Locale.US -> formatWithAmountFormat(amountFormat, locale, priceAsDouble)
                    else -> formatWithCurrencyInstance(numberFormat, priceAsDouble, replaceSpaceCharToSpec)
                }
            }
        }

        private fun formatWithCurrencyInstance(numberFormat: NumberFormat, price: Double, replaceSpaceCharToSpec: Boolean): String {
            val formattedPrice = numberFormat.format(price)
            return if (replaceSpaceCharToSpec) {
                formattedPrice.removeSpecialSpaceAndMinus()
            } else {
                formattedPrice.changeSimpleSpaceToSpecial()
            }
        }

        private fun formatWithAmountFormat(amountFormat: String, locale: Locale, price: Double): String {
            return amountFormat.format(locale, price)
        }

        fun assertEquals(actual: String, expected: String, message: String) {
            val priceActual = actual.removeSpecialSpaceAndMinus()
            val priceExpected = expected.removeSpecialSpaceAndMinus()
            VintedAssert.assertTrue(expected.isNotEmpty(), "Expected price was empty String")
            VintedAssert.assertEquals(priceActual, priceExpected, message)
        }

        fun assertStartsWith(actual: String, expected: String, message: String) {
            val priceActual = actual.removeSpecialSpaceAndMinus()
            val priceExpected = expected.removeSpecialSpaceAndMinus()
            VintedAssert.assertTrue(expected.isNotEmpty(), "Expected price was empty String")
            VintedAssert.assertTrue(priceActual.startsWith(priceExpected), message)
        }

        fun assertContains(actual: String, expected: String, message: String) {
            val priceActual = actual.removeSpecialSpaceAndMinus()
            val priceExpected = expected.removeSpecialSpaceAndMinus()
            VintedAssert.assertTrue(expected.isNotEmpty(), "Expected price was empty String")
            VintedAssert.assertTrue(priceActual.contains(priceExpected), message)
        }
    }
}
