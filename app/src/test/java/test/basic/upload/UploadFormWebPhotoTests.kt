package test.basic.upload

import RobotFactory.actionBarRobot
import RobotFactory.deepLink
import RobotFactory.galleryRobot
import RobotFactory.uploadItemRobot
import RobotFactory.webPhotoRobot
import commonUtil.testng.LoginToMainThreadUser
import commonUtil.testng.config.VintedPlatform
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import util.base.BaseTest
import util.values.Visibility

@RunMobile(platform = VintedPlatform.IOS) // TODO Only runs on IOS because tests on Android are unstable and need to be fixed.
@LoginToMainThreadUser
class UploadFormWebPhotoTests : BaseTest() {

    @BeforeMethod
    fun beforeTest() {
        deepLink.item.goToUploadForm()
    }

    @TmsLink("20918")
    @Test(description = "Upload a web photo and check if web photo warning banner appears")
    fun testWebPhotoWarning() {
        uploadItemRobot.clickAddFirstPhoto()
        galleryRobot.selectWebPhoto()
        webPhotoRobot
            .assertWebPhotoWarningVisibility(Visibility.Visible)
            .clickOnWebPhotoWarning()
            .assertWebPhotoWarningAcceptButtonVisibility(Visibility.Visible)
        actionBarRobot.closeBottomSheetComponentIfVisible()
        webPhotoRobot
            .clickOnWebPhotoWarning()
            .clickOnWebPhotoWarningAcceptButton()
            .assertWebPhotoWarningVisibility(Visibility.Invisible)
    }
}
