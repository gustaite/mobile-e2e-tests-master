package test.basic.upload

import RobotFactory.deepLink
import RobotFactory.luxuryItemRobot
import RobotFactory.luxuryItemUploadWorkflowRobot
import RobotFactory.navigationRobot
import RobotFactory.uploadItemRobot
import commonUtil.testng.LoginToMainThreadUser
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.mobile.RunMobile
import util.values.Visibility
import kotlin.random.Random

@RunMobile(country = VintedCountry.INT)
@Feature("Luxury items tests in upload form")
@LoginToMainThreadUser
@Test(description = "Luxury items tests. Luxury item is an item with a price more than 100 eur and luxury brand")
class UploadLuxuryItemTests : BaseTest() {

    private val luxuryPrice = Random.nextInt(300, 999).toString()
    private val nonLuxuryPrice = Random.nextInt(1, 99).toString()

    @BeforeMethod(description = "Open upload form")
    fun beforeTest() {
        deepLink.item.goToUploadForm()
    }

    @TmsLink("20919")
    @Test(description = "Test if luxury item warning is visible with one photo and disappears when uploading at least 5 photos")
    fun testLuxuryItemUploadFlow() {

        luxuryItemUploadWorkflowRobot
            .uploadLuxuryItemWithOnePhoto(luxuryPrice)
            .closeLuxuryModalAndCheckIfWarningTextIsVisible()

        uploadItemRobot.clickSaveAndWait()

        luxuryItemRobot
            .clickOnAddMorePhotosInLuxuryItemWarningModal()
            .clickBack()

        luxuryItemRobot.assertLuxuryItemWarningTextVisibility(Visibility.Visible)

        uploadItemRobot.clickSaveAndWait()

        luxuryItemRobot
            .clickOnAddMorePhotosInLuxuryItemWarningModal()
            .selectPhotosFromGallery(4)
            .assertPhotoIsVisible()

        luxuryItemRobot
            .assertLuxuryItemWarningTextVisibility(Visibility.Invisible)
            .clickSaveAndWait()

        navigationRobot
            .openProfileTab()
            .clickOnUserProfile().closetScreen
            .assertItemsIsVisibleInCloset(1, luxuryPrice)
    }

    @Test(description = "Test if luxury item warning is visible with one photo and can be saved as a draft, also edit that draft to have non luxury price and test if luxury item warning is not visible")
    fun testLuxuryItemSaveToDraftFlow() {
        luxuryItemUploadWorkflowRobot.uploadLuxuryItemWithOnePhoto(luxuryPrice)
        luxuryItemRobot
            .assertLuxuryItemsModalTitleVisibility(Visibility.Visible)
            .clickOnSaveAsDraftInLuxuryItemWarningModal()
            .assertLuxuryItemsModalTitleVisibility(Visibility.Invisible)

        deepLink.profile.goToMyProfile().closetScreen
            .editDraft()
            .openSellingPriceSection()
            .enterPriceAndSubmit(nonLuxuryPrice)
            .clickSaveAndWait()

        luxuryItemRobot
            .assertLuxuryItemsModalTitleVisibility(Visibility.Invisible)
    }
}
