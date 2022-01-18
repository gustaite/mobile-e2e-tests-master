package test.basic.b2c

import RobotFactory.deepLink
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import api.controllers.item.ItemAPI
import api.controllers.item.ItemRequestBuilder
import api.controllers.item.deleteAllDrafts
import api.data.models.isNotNull
import commonUtil.data.Price
import commonUtil.testng.LoginToBusinessUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.ElementByLanguage
import util.values.Visibility

@Feature("B2C upload tests")
@RunMobile(country = VintedCountry.INT)
@LoginToBusinessUser
class BusinessSellerUploadTests : BaseTest() {

    @Test(description = "Test B2C user cannot upload to beauty category")
    @TmsLink("25073")
    fun testB2cUserCannotUploadToBeautyCategory() {
        val price = Price.getRandomPriceInRange()
        deepLink.item.goToUploadForm()
            .selectDefaultPhotoTitleDescriptionValuesOnItemUpload()
        uploadFormWorkflowRobot
            .selectAndAssertBeautyCategoryInUploadForm(ElementByLanguage.CategoryWomenBeautyItem)
            .selectAndAssertFirstConditionInUploadForm()
            .selectAndAssertNoBrandInUploadForm()
            .selectAndAssertPriceInUploadForm(price, true)
        uploadItemRobot
            .selectRandomShippingOption()
            .clickSave()
            .assertB2cUploadToBeautyCategoryErrorIsVisible()
            .closeB2cUploadToBeautyCategoryErrorIos()
            .selectAndAssertBeautyCategoryInUploadForm(ElementByLanguage.CategoryMenBeautyItem)
            .selectAndAssertFirstConditionInUploadForm()
        uploadItemRobot
            .clickSave()
            .assertB2cUploadToBeautyCategoryErrorIsVisible()
    }

    @Test(description = "Test B2C user cannot upload with custom shipping")
    @TmsLink("25074")
    fun testB2cUserCannotUploadWithCustomShipping() {
        val item = ItemAPI.createDraft(loggedInUser, ItemRequestBuilder.VintedType.SIMPLE_ITEM)
        deepLink.item.goToItemEditing(item)
        uploadItemRobot
            .openShippingOptionsSection()
            .assertCustomOptionVisibility(Visibility.Invisible)
    }

    @AfterMethod(description = "Delete drafts")
    fun deleteDrafts() {
        loggedInUser.isNotNull().deleteAllDrafts()
    }
}
