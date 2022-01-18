package util

import api.data.models.VintedUser
import commonUtil.extensions.isInitialized
import commonUtil.reporting.Report
import util.base.BaseTest.Companion.defaultUser
import util.base.BaseTest.Companion.loggedInUser

object AppTexts {
    val userByCountry: VintedUser
        get() {
            val user = if (loggedInUser.isInitialized()) loggedInUser else defaultUser
            Report.addMessage("User '${user.username}' using country language ${user.country.language.code}")
            return user
        }
}
