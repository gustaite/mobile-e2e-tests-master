package robot.workflow

import RobotFactory.luxuryItemRobot
import RobotFactory.uploadFormWorkflowRobot
import RobotFactory.uploadItemRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.upload.UploadItemRobot
import util.values.Visibility

class LuxuryItemUploadWorkflowRobot : BaseRobot() {

    @Step("Try to upload an item with 1 photo, price {price} and luxury brand ")
    fun uploadLuxuryItemWithOnePhoto(price: String): LuxuryItemUploadWorkflowRobot {
        uploadItemRobot.selectDefaultValuesOnItemUploadWithPhoto(price)
        uploadFormWorkflowRobot
            .selectLuxuryBrandAndCloseAuthenticityModal()
            .clickSaveAndWait()
        return this
    }

    @Step("Check if luxury warning modal is visible, close it and check if warning text is visible")
    fun closeLuxuryModalAndCheckIfWarningTextIsVisible(): UploadItemRobot {
        luxuryItemRobot
            .assertLuxuryItemsModalTitleVisibility(Visibility.Visible)
            .closeLuxuryModal()
            .assertLuxuryItemWarningTextVisibility(Visibility.Visible)
        return uploadItemRobot
    }
}
