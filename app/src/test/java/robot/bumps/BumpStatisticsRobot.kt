package robot.bumps

import RobotFactory.userProfileRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import robot.profile.tabs.UserProfileClosetRobot
import util.*
import util.driver.VintedBy
import util.driver.VintedElement

class BumpStatisticsRobot : BaseRobot() {

    private val bumpStatisticsHeaderElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey("push_up_performance_item_header", "item_performance_screen_name")

    private val bumpedItemImageElementAndroid: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("push_up_performance_item_image"))

    private val bumpPerformanceChartElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("push_up_performance_line_chart"),
            iOSBy = VintedBy.accessibilityId(". 1 dataset. ")
        )

    private val bumpPerformancePeriodElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "push_up_performance_visibility_period",
            "item_performance_impressions_title"
        )

    private val bumpStatisticsTextElementAndroid: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("push_up_performance_statistics"))

    private val bumpEngagementsCellElement: VintedElement
        get() = VintedDriver.elementByIdAndTranslationKey(
            "push_up_performance_engagements",
            "item_performance_engagements_title"
        )

    @Step("Check if bump statistics header is visible")
    fun assertBumpStatisticsHeaderIsVisible(): BumpStatisticsRobot {
        VintedAssert.assertTrue(bumpStatisticsHeaderElement.isVisible(), "Statistics header should be visible")
        return this
    }

    @Step("Check if all elements are visible in bump statistics screen")
    fun assertAllBumpStatisticsElementsAreVisible(): BumpStatisticsRobot {
        Android.doIfAndroid {
            VintedAssert.assertTrue(bumpedItemImageElementAndroid.isVisible(), "Bumped item image should be visible")
            VintedAssert.assertTrue(bumpStatisticsTextElementAndroid.isVisible(), "Bump statistics text should be visible")
        }
        VintedAssert.assertTrue(bumpPerformancePeriodElement.isVisible(), "Bump duration should be visible")
        VintedAssert.assertTrue(bumpPerformanceChartElement.isVisible(), "Bump performance chart should be visible")
        VintedAssert.assertTrue(bumpEngagementsCellElement.isVisible(), "Bumped item engagement chart should be visible")
        return this
    }

    @Step("Go back to user closet screen by clicking back 2 times")
    fun goBackToUserClosetScreen(): UserProfileClosetRobot {
        repeat(2) { clickBack() }
        return userProfileRobot.closetScreen
    }
}
