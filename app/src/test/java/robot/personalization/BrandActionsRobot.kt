package robot.personalization

import RobotFactory.catalogRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.browse.CatalogRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class BrandActionsRobot {
    private val followBrandButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("personalization_brands_follow_button_title")),
            iOSBy = VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("personalization_brands_follow_button_title"))
        )

    private val unfollowBrandButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.androidText(Android.getElementValue("personalization_brands_unfollow_button_title")),
            iOSBy = VintedBy.iOSNsPredicateStringNameOrLabel(IOS.getElementValue("personalization_brands_unfollow_button_title"))
        )

    @Step("Follow brand")
    fun followBrand(): CatalogRobot {
        followBrandButton.click()
        return catalogRobot
    }

    @Step("Assert unfollow button is visible")
    fun assertUnfollowButtonIsVisible() {
        VintedAssert.assertTrue(unfollowBrandButton.isVisible(), "Unfollow brand should be visible")
    }

    @Step("Unfollow brand")
    fun unfollowBrand(): CatalogRobot {
        unfollowBrandButton.click()
        return catalogRobot
    }

    @Step("Assert follow button is visible")
    fun assertFollowButtonIsVisible() {
        VintedAssert.assertTrue(followBrandButton.isVisible(), "Follow button should be visible")
    }
}
