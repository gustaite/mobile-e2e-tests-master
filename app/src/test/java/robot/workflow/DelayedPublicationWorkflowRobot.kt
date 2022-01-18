package robot.workflow

import RobotFactory.delayedPublicationRobot
import RobotFactory.itemRobot
import io.qameta.allure.Step
import robot.BaseRobot
import robot.item.ItemActions
import util.values.Visibility

class DelayedPublicationWorkflowRobot : BaseRobot() {

    @Step("Check if delayed item information modal works")
    fun checkIfDelayedItemInformationModalWorks(): DelayedPublicationWorkflowRobot {
        delayedPublicationRobot
            .clickOnInformationLink()
            .assertDelayedItemModalVisibility(Visibility.Visible)
            .clickOnOkInDelayedInformationModal()
            .assertDelayedItemModalVisibility(Visibility.Invisible)
        return this
    }

    @Step("Assert bump and share buttons are not visible in item view")
    fun assertBumpAndShareButtonsAreNotVisible(): DelayedPublicationWorkflowRobot {
        itemRobot
            .assertShareButtonVisibility(Visibility.Invisible)
            .assertBumpButtonVisibility(Visibility.Invisible)
        return this
    }

    @Step("Assert actions (sell, reserve, hide, bump) are not available for delayed item")
    fun assertItemActionsAreNotAvailable(): DelayedPublicationWorkflowRobot {
        itemRobot
            .openItemActions()
            .assertActionButtonVisibility(ItemActions.HIDE, Visibility.Invisible)
            .assertActionButtonVisibility(ItemActions.SOLD, Visibility.Invisible)
            .assertActionButtonVisibility(ItemActions.PROMOTED, Visibility.Invisible)
            .assertActionButtonVisibility(ItemActions.SWAPPED, Visibility.Invisible)
            .assertActionButtonVisibility(ItemActions.RESERVE, Visibility.Invisible)
        return this
    }
}
