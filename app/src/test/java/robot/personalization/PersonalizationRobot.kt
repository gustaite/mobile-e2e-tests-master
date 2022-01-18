package robot.personalization

import io.qameta.allure.Step
import robot.BaseRobot
import robot.webview.WebViewRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class PersonalizationRobot : BaseRobot() {

    private val infoButton: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("menu_user_personalisation_settings_help"),
            iOSBy = VintedBy.accessibilityId("info")
        )

    private val categoriesAndSizesElement: VintedElement get() =
        VintedDriver.elementByIdAndTranslationKey("user_personalisation_sizes", "personalize_feed_segment_button_categories")

    private val brandSectionElement: VintedElement get() =
        VintedDriver.elementByIdAndTranslationKey("user_personalisation_brands", "personalize_feed_segment_button_brands")

    private val followedMembersElement: VintedElement get() =
        VintedDriver.elementByIdAndTranslationKey("user_personalisation_followed_users", "personalize_feed_segment_button_members")

    @Step("Click on brand section")
    fun clickBrandSection(): PersonalizationBrandRobot {
        brandSectionElement.click()
        return PersonalizationBrandRobot()
    }

    @Step("Open categories and sizes")
    fun openCategoriesAndSizes(): CategoriesAndSizesRobot {
        categoriesAndSizesElement.click()
        return CategoriesAndSizesRobot()
    }

    @Step("Click on info button in personalisation")
    fun openPersonalisationInfoScreen(): WebViewRobot {
        infoButton.click()
        return WebViewRobot()
    }

    @Step("Open followed members")
    fun openFollowedMembers(): FollowedMembersRobot {
        followedMembersElement.click()
        return FollowedMembersRobot()
    }
}
