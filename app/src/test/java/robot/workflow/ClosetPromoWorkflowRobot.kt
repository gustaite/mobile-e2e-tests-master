package robot.workflow

import RobotFactory.closetPromoCheckoutRobot
import RobotFactory.closetPromoStatisticRobot
import RobotFactory.deepLink
import RobotFactory.paymentMethodsRobot
import RobotFactory.userProfileRobot
import RobotFactory.userShortInfoSectionRobot
import RobotFactory.workflowRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.payments.PaymentMethods
import util.absfeatures.AbTestController

class ClosetPromoWorkflowRobot : BaseRobot() {

    @Step("Select to order a closet promo and check if how it works screen, price and duration are displayed")
    fun selectToOrderClosetPromoAndCheckItsElements(): ClosetPromoWorkflowRobot {
        deepLink.profile
            .goToMyProfile()
            .clickOnClosetPromoBanner()
        workflowRobot
            .checkClosetPromoHowItWorksScreenAndLeaveIt()
        workflowRobot
            .checkClosetPromoHelpModalAndCloseIt()
            .assertPriceIsDisplayed()
            .assertDurationIsDisplayed()
            .clickConfirm()
        return this
    }

    @Step("Click on Closet Promo Statistics Banner and assert that Statistics Header is visible")
    fun clickOnPromoStatisticsBannerAndAssertStatisticsHeader(): ClosetPromoWorkflowRobot {
        userShortInfoSectionRobot
            .assertShortUserSectionIsVisible()
        userProfileRobot
            .clickOnClosetPromoStatisticsBanner()
        closetPromoStatisticRobot
            .assertStatisticsHeaderIsVisible()
        return this
    }

    @Step("Order closet Promo using {paymentMethod} payment method")
    fun orderClosetPromoUsingThirdPartyPaymentMethod(paymentMethod: PaymentMethods) {
        selectToOrderClosetPromoAndCheckItsElements()
        paymentMethodsRobot.selectThirdPartyPaymentMethod(paymentMethod)
        closetPromoCheckoutRobot
            .assertOrderSummaryIsDisplayed()
            .confirmOrder()
        paymentMethodsRobot.payUsingSelectedThirdPartyPaymentMethod(paymentMethod)
        clickOnPromoStatisticsBannerAndAssertStatisticsHeader()
    }

    @Step("Check if all elements are visible in Closet Promo statistics screen ")
    fun assertAllClosetPromoStatisticsElementsAreVisible(): ClosetPromoWorkflowRobot {
        if (AbTestController.isCpInsightsReworkOnIos()) {
            closetPromoStatisticRobot.tempAssertDiscoveryCellIsVisibleForNewCp()
        } else {
            closetPromoStatisticRobot
                .assertVisibilityClosetPromoStatisticsElementsAreVisible()
                .assertInteractionsClosetPromoStatisticsElementsAreVisible()
                .assertTipsClosetPromoStatisticsElementsAreVisible()
        }
        return this
    }

    @Step("Assert all Tips elements open appropriate screens - upload form, profile edit, followers")
    fun assertClosetPromoStatisticsTipsElementsOpenAppropriateScreens(): ClosetPromoWorkflowRobot {
        if (!AbTestController.isCpInsightsReworkOnIos()) {
            closetPromoStatisticRobot
                .openUploadFormFromPerformanceUploadItemButton()
                .openEditProfileFromPerformanceSeeProfileButton()
                .openFollowersScreenFromPerformanceSeeFollowersButton()
        }
        return this
    }

    @Step("Review order and buy Closet Promo when buying not for the first time")
    fun reviewOrderAndBuyClosetPromo(): ClosetPromoWorkflowRobot {
        closetPromoCheckoutRobot
            .clickReviewOrder()
            .clickOnSavedCreditCard()
            .confirmOrder()
        return this
    }
}
