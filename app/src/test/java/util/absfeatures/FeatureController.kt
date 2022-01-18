package util.absfeatures

import api.controllers.absfeatures.isFeatureOn
import io.qameta.allure.Step
import org.testng.SkipException
import util.base.BaseTest.Companion.userForAbTestOrFeatureCheck

object FeatureController {

    @Step("Check if {featureName} feature is on or off")
    fun isOn(featureName: String): Boolean {
        return userForAbTestOrFeatureCheck.isFeatureOn(featureName = featureName, isLoggedIn = true)
    }

    @Step("Skip test when external link modal is off")
    fun skipTestExternalLinkModalIsOff(): Throwable? {
        return if (!isOn(FeatureNames.EXTERNAL_LINK_MODAL)) {
            SkipException("External link modal feature is off: https://admin.vinted.net/features/1136")
        } else null
    }
}
