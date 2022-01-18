package test.basic.navigation.deepLinkNavigation

import RobotFactory.deepLink
import RobotFactory.modalRobot
import RobotFactory.navigationRobot
import api.controllers.user.helpCenterApi
import api.data.models.faq.VintedFaqEntryType
import commonUtil.testng.LoginToMainThreadUser
import io.qameta.allure.Feature
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.IOS
import util.base.BaseTest
import util.data.NavigationDataProviders
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import java.lang.reflect.Method

@RunMobile
@LoginToMainThreadUser
@Feature("DeepLink navigation tests")
class DeepLinkNavigationMiscellaneousTests : BaseTest() {

    @Test(description = "Test if deepLink navigation to 'Contact support' screen is working")
    fun testDeepLinkNavigationToContactSupportScreen() {
        val faqEntry = loggedInUser.helpCenterApi.getFaqEntryForType(VintedFaqEntryType.members_feedback).faqEntry
        deepLink.miscellaneous.goToContactSupport(faqEntryId = faqEntry?.id!!)
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.contactSupportPageTitle)
    }

    @Test(description = "Test if deepLink navigation to 'Donations Overview' screen is working")
    fun testDeepLinkNavigationToDonationsOverviewScreen() {
        deepLink.miscellaneous.goToDonationsOverview()
        navigationRobot.assertNavigationBarNameText(NavigationDataProviders.donationsOverviewPageTitle)
    }

    @RunMobile(platform = VintedPlatform.IOS, message = "Test only for IOS")
    @Test(description = "Test if app alert modal is shown with texts")
    fun testDeepLinkShowingAlertModalWithTexts() {
        val title = "Laba diena!"
        val subtitle = "Kaip gyvenimas?"
        val closeTitle = "Iki!"
        deepLink.miscellaneous.showAppAlertUsingTexts(title = title, subtitle = subtitle, closeTitle = closeTitle)
        modalRobot
            .assertElementWithTextIsVisibleInModal(title)
            .assertElementWithTextIsVisibleInModal(subtitle)
            .assertElementWithTextIsVisibleInModal(closeTitle)
    }

    @RunMobile(platform = VintedPlatform.IOS, message = "Test only for IOS")
    @Test(description = "Test if app alert modal is shown with keys")
    fun testDeepLinkShowingAlertModalWithKeys() {
        val title = IOS.getRandomKeyValuePair()
        val subtitle = IOS.getRandomKeyValuePair()
        val closeTitle = IOS.getRandomKeyValuePair()
        deepLink.miscellaneous.showAppAlertUsingKeys(titleKey = title.first, subtitleKey = subtitle.first, closeTitleKey = closeTitle.first)
        modalRobot
            .assertElementWithTextIsVisibleInModal(title.second)
            .assertElementWithTextIsVisibleInModal(subtitle.second)
            .assertElementWithTextIsVisibleInModal(closeTitle.second)
    }

    @AfterMethod(description = "Close modal if it is visible")
    fun afterMethod_a_closeModalIfVisible(method: Method) {
        if (method.name in listOf("testDeepLinkShowingAlertModalWithTexts", "testDeepLinkShowingAlertModalWithKeys") &&
            !navigationRobot.isModalInvisible()
        ) navigationRobot.closeModal()
    }
}
