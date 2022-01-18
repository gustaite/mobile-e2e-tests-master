package test.basic

import RobotFactory.deepLink
import RobotFactory.navigationRobot
import RobotFactory.personalizationWorkflowRobot
import RobotFactory.problemWorkflowRobot
import RobotFactory.webViewRobot
import io.qameta.allure.Feature
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink

@RunMobile
@Feature("Webview tests")
@LoginToMainThreadUser
class WebViewTests : BaseTest() {
    @Test(description = "Check if personalisation help button opens web view")
    @TmsLink("24717")
    fun testIfPersonalisationHelpButtonOpensWebView() {
        personalizationWorkflowRobot
            .openPersonalizationSettings()
            .openPersonalisationInfoScreen()
            .assertWebViewIsVisible()
    }

    @Test(description = "Check if about vinted sections leads to web view")
    @TmsLink("79")
    fun testIfAboutVintedOpensWebView() {
        navigationRobot
            .openProfileTab()
            .openAboutVinted()
            .openRandomCell()
            .assertWebViewIsVisible()
    }

    @Test(description = "Check if help center sections are clickable and if web view is opened")
    @TmsLink("68")
    fun testIfHelpCenterOpensWebView() {
        deepLink.setting
            .goToHelpCenter()
            .assertHelpCenterIsDisplayed()
            .openHelpCenterSection()
            .assertWebViewIsVisible()
    }

    @Test(description = "Test if random FAQ link opens web view")
    @TmsLink("25650")
    fun testIfRandomFaqLinkOpens() {
        val url = problemWorkflowRobot.getRandomFaqAppLink()
        deepLink.openLinkWithFullUrl(url)
        webViewRobot.assertWebViewIsVisible()
    }

    @Test(description = "Check if Vinted guide sections are clickable and if web view is opened")
    @TmsLink("5274")
    fun testIfVintedGuideOpensWebView() {
        deepLink.setting
            .goToVintedGuide()
            .assertVintedGuideIsDisplayed()
            .openVintedGuideSection()
            .assertWebViewIsVisible()
    }
}
