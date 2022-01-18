package util.values

import api.data.models.getTextByUserCountry
import commonUtil.data.VintedCountriesTextValue
import commonUtil.data.enums.VintedUserBankAccountDetails
import util.AppTexts.userByCountry

class UserBankAccountDetailsTexts {
    companion object {
        val ibanNumberText: String
            get() = userByCountry.getTextByUserCountry(ibanNumberTextValues)

        private val ibanNumberTextValues = VintedCountriesTextValue(
            fr = VintedUserBankAccountDetails.INT.iban,
            de = VintedUserBankAccountDetails.DE.iban,
            pl = VintedUserBankAccountDetails.PL.iban,
            cz = VintedUserBankAccountDetails.CZ.iban,
            lt = VintedUserBankAccountDetails.LT.iban
        )

        val accountNumberText: String
            get() = userByCountry.getTextByUserCountry(accountNumberTextValues)

        private val accountNumberTextValues = VintedCountriesTextValue(
            uk = VintedUserBankAccountDetails.UK.accountNumber,
            us = VintedUserBankAccountDetails.US.accountNumber
        )

        val sortCodeOrRoutingNumberText: String
            get() = userByCountry.getTextByUserCountry(sortCodeOrRoutingNumberTextValues)

        private val sortCodeOrRoutingNumberTextValues = VintedCountriesTextValue(
            uk = VintedUserBankAccountDetails.UK.sortCode,
            us = VintedUserBankAccountDetails.US.routingNumber
        )
    }
}
