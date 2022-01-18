package test.basic.upload

import RobotFactory.deepLink
import RobotFactory.uploadItemRobot
import commonUtil.testng.LoginToNewUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.ElementByLanguage

@RunMobile
@Feature("Brand authentication tests in upload form")
@LoginToNewUser
@Test(description = "Test Brand authentication flow. It should close authenticity modal and assert that brand notice is visible")
class UploadFormBrandAuthenticationTests : BaseTest() {

    @BeforeMethod(description = "Open upload form")
    fun beforeTest() {
        deepLink.item.goToUploadForm()
    }

    @Test(description = "Test brand authentication flow")
    @TmsLink("65")
    fun testBrandAuthenticationFlow() {

        uploadItemRobot
            .openCategoriesSection()
            .selectCategoryAndSubcategory(ElementByLanguage.getCategoriesAndSubcategories(0))
            .openBrandsSection()
            .selectLuxuryBrand()
            .checkBrandAuthenticityModalCloseItAndCheckNotice()

        uploadItemRobot
            .clickProofOfAuthenticity()
            .checkBrandAuthenticityModalCloseItAndCheckNotice()
    }
}
