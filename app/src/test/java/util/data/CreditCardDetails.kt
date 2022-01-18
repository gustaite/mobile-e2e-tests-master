package util.data

import util.base.BaseTest

data class CreditCardDetails(
    val credit_card: CreditCard
) {
    data class CreditCard(
        val name: String = BaseTest.loggedInUser.creditCardCredentials.info.fullName,
        val number: String = BaseTest.loggedInUser.creditCardCredentials.info.number,
        val date_month: String = BaseTest.loggedInUser.creditCardCredentials.info.date_month,
        val date_year: String = BaseTest.loggedInUser.creditCardCredentials.info.date_year,
        val cvv: String = BaseTest.loggedInUser.creditCardCredentials.info.cvv
    )
}
