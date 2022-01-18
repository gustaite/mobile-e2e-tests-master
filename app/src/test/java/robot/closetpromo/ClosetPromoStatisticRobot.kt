package robot.closetpromo

import RobotFactory.followingRobot
import RobotFactory.uploadItemRobot
import RobotFactory.userProfileEditRobot
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.EnvironmentManager.isAndroid
import util.IOS
import util.VintedDriver
import util.absfeatures.AbTestController
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.closetPromoStatisticsPerformanceParagraphText

class ClosetPromoStatisticRobot : BaseRobot() {

    private val closetPromoStatisticsHeaderElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_header"),
            // ToDO iosBy2 is for CP_INSIGHTS_REWORK
            iosElement = {
                IOS.findAllElement(
                    iosBy1 = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_header_title")),
                    iosBy2 = VintedBy.accessibilityId("discovery_cell")
                )
            }
        )

    private val closetPromoPerformanceLineChartElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_line_chart"),
            iOSBy = VintedBy.accessibilityId(". 1 dataset. ")
        )

    private val closetPromoPerformanceStatisticsParagraphElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_statistics"),
            iOSBy = VintedBy.iOSNsPredicateString("name CONTAINS '$closetPromoStatisticsPerformanceParagraphText'")
        )

    private val closetPromoPerformanceItemClicksElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_items_since_promotion"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_stats_item_clicks"))
        )

    private val closetPromoPerformanceFavoritesElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_favorites_since_promotion"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_stats_favorites"))
        )

    private val closetPromoPerformanceFollowersElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("closet_promo_performance_followers_since_promotion"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_stats_followers"))
        )

    private val closetPromoPerformanceUploadItemElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("closet_promo_performance_upload_item"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_hints_upload_item"))
        )

    private val closetPromoPerformanceEditProfileElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("closet_promo_performance_edit_profile"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_hints_edit_profile"))
        )

    private val closetPromoPerformanceSeeFollowersElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.scrollableId("closet_promo_performance_see_followers"),
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("closet_promo_performance_hints_see_followers"))
        )

    private val cancelButtonIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId(IOS.getElementValue("cancel"))
        )

    // ToDo Element for CP_INSIGHTS_REWORK
    private val newClosetPromoDiscoveryCellElementIos: VintedElement
        get() = VintedDriver.findElement(
            iOSBy = VintedBy.accessibilityId("discovery_cell")
        )

    @Step("Check if statistics header is visible")
    fun assertStatisticsHeaderIsVisible(): ClosetPromoStatisticRobot {
        VintedAssert.assertTrue(closetPromoStatisticsHeaderElement.isVisible(), "Statistics header should be visible")
        return RobotFactory.closetPromoStatisticRobot
    }

    @Step("Check if all elements are visible in Closet Promo statistics screen Visibility part")
    fun assertVisibilityClosetPromoStatisticsElementsAreVisible(): ClosetPromoStatisticRobot {
        if (!AbTestController.isCpInsightsReworkOnIos()) {
            VintedAssert.assertTrue(
                closetPromoPerformanceLineChartElement.isVisible(), "Closet promo performance chart should be visible"
            )
            VintedAssert.assertTrue(
                closetPromoPerformanceStatisticsParagraphElement.isVisible(),
                "Closet promo performance statistics paragraph should be visible"
            )
        }
        return this
    }

    @Step("Temp: Check discovery cell is visible for new CP")
    fun tempAssertDiscoveryCellIsVisibleForNewCp(): ClosetPromoStatisticRobot {
        VintedAssert.assertTrue(
            newClosetPromoDiscoveryCellElementIos.isVisible(), "Closet promo discovery cell should be visible"
        )
        return this
    }

    @Step("Check if all elements are visible in Closet Promo statistics screen Interactions part")
    fun assertInteractionsClosetPromoStatisticsElementsAreVisible(): ClosetPromoStatisticRobot {
        repeat(2) { Android.scrollDownABit() }
        closetPromoPerformanceItemClicksElement.withScrollIos()
        VintedAssert.assertTrue(
            closetPromoPerformanceItemClicksElement.isVisible(),
            "Closet promo performance item clicks element should be visible"
        )
        VintedAssert.assertTrue(
            closetPromoPerformanceFavoritesElement.isVisible(),
            "Closet promo performance favourites element should be visible"
        )
        VintedAssert.assertTrue(
            closetPromoPerformanceFollowersElement.isVisible(),
            "Closet promo performance followers element should be visible"
        )
        return this
    }

    @Step("Check if all elements are visible in Closet Promo statistics screen Tips part")
    fun assertTipsClosetPromoStatisticsElementsAreVisible(): ClosetPromoStatisticRobot {
        Android.scrollDown()
        VintedAssert.assertTrue(
            closetPromoPerformanceUploadItemElement.withScrollIos().isVisible(),
            "Closet Promo performance upload item tip element should be visible"
        )
        VintedAssert.assertTrue(
            closetPromoPerformanceEditProfileElement.isVisible(),
            "Closet Promo performance edit profile tip element should be visible"
        )
        VintedAssert.assertTrue(
            closetPromoPerformanceSeeFollowersElement.isVisible(),
            "Closet Promo performance see followers tip element should be visible"
        )
        return this
    }

    @Step("Click on Performance Upload Item element and assert Upload Form is opened")
    fun openUploadFormFromPerformanceUploadItemButton(): ClosetPromoStatisticRobot {
        closetPromoPerformanceUploadItemElement.click()
        uploadItemRobot.assertTitleInputFieldIsVisible()
        clickBack()
        return this
    }

    @Step("Click on Performance See Profile Edit element and assert Profile Edit opened")
    fun openEditProfileFromPerformanceSeeProfileButton(): ClosetPromoStatisticRobot {
        closetPromoPerformanceEditProfileElement.click()
        userProfileEditRobot.assertEditProfileScreenIsVisible()
        if (isAndroid) {
            clickBack()
        } else {
            cancelButtonIos.click()
        }
        return this
    }

    @Step("Click on Performance See Followers element and assert Followers Screen is opened")
    fun openFollowersScreenFromPerformanceSeeFollowersButton(): ClosetPromoStatisticRobot {
        closetPromoPerformanceSeeFollowersElement.click()
        followingRobot.assertFollowersScreenIsOpened()
        clickBack()
        assertVisibilityClosetPromoStatisticsElementsAreVisible()
        return this
    }
}
