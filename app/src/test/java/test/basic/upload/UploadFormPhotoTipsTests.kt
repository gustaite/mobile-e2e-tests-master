package test.basic.upload

import RobotFactory.deepLink
import RobotFactory.uploadItemRobot
import io.qameta.allure.Feature
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test
import robot.upload.PhotoTipsVisibility
import util.base.BaseTest
import util.driver.WebDriverFactory
import commonUtil.extensions.isInitialized
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.TmsLink
import util.image.Screenshot

@RunMobile
@LoginToDefaultUser
@Feature("Upload form photo tips tests")
class UploadFormPhotoTipsTests : BaseTest() {

    @Test(description = "Photo tips should be visible in upload form")
    @TmsLink("61")
    fun testPhotoTipsVisibleInUploadForm() {
        deepLink.item.goToUploadForm()
        uploadItemRobot
            .clickPhotoTips()
            .assertPhotoTipsVisibility(PhotoTipsVisibility.Visible)
            .closeBottomSheetComponentIfVisible()
    }

    @AfterMethod(description = "Close photo tips modal if visible")
    fun afterMethod(result: ITestResult) {
        if (!result.isSuccess) {
            if (!WebDriverFactory.driver.isInitialized()) return
            commonUtil.reporting.Report.addImage(Screenshot.takeScreenshot().screenshot)
        }
    }
}
