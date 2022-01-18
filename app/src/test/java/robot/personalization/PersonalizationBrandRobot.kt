package robot.personalization

import RobotFactory.brandActionsRobot
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.EnvironmentManager.isAndroid
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class PersonalizationBrandRobot : BaseRobot() {

    private val searchElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id(Android.INPUT_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("search")
        )

    @Step("{action.value} first brand")
    fun setFirstBrandAs(action: PersonalizationBrandAction): PersonalizationBrandRobot {
        when (action) {
            PersonalizationBrandAction.FOLLOW -> brandActionsRobot.followBrand()
            PersonalizationBrandAction.UNFOLLOW -> brandActionsRobot.unfollowBrand()
        }
        return this
    }

    @Step("Search for brand: {brand}")
    fun searchForBrand(brand: String): PersonalizationBrandRobot {
        if (isAndroid) {
            searchElement.click()
            Android.sendKeysUsingKeyboard(brand)
        } else {
            searchElement.sendKeys(brand)
        }
        return this
    }
}

enum class PersonalizationBrandAction(val value: String) {
    FOLLOW("Follow"),
    UNFOLLOW("Unfollow")
}
