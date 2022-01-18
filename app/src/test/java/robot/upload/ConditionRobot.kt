package robot.upload

import api.controllers.GlobalAPI
import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.Util
import util.base.BaseTest.Companion.loggedInUser
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.ElementByLanguage.Companion.getElementValueByPlatform

class ConditionRobot : BaseRobot() {

    private val conditionFirstElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("status_item_cell", Android.CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/XCUIElementTypeCell[1]")
        )

    private fun conditionElement(selectedCondition: String): VintedElement {
        return VintedDriver.findElementByText(selectedCondition, Util.SearchTextOperator.CONTAINS)
    }

    private val conditionElementList: List<VintedElement>
        get() = VintedDriver.findElementList(
            androidBy = VintedBy.setWithParentAndChild("status_item_cell", Android.CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.accessibilityId("radioEmpty")
        )

    private val newWithTagsOnlyAllowedInfoText get() = getElementValueByPlatform("item_editor_extra_conditions_restricted", "new_with_tags_only_alowed")

    @Step("Assert only New With Tags condition is visible")
    fun assertOnlyNewWithTagsConditionIsVisible(): ConditionRobot {
        val newWithTagsConditionText = GlobalAPI.getConditions(user = loggedInUser)[0].title
        VintedAssert.assertTrue(conditionElement(newWithTagsConditionText).isVisible(), "New with Tags Condition is not visible")
        VintedAssert.assertTrue(conditionElementList.size == 1, "One (New with Tags) condition should be visible but found ${conditionElementList.size} visible conditions")
        VintedAssert.assertTrue(conditionElement(newWithTagsOnlyAllowedInfoText).isVisible(), "New with Tags Only Allowed Info text is not visible")
        return this
    }

    @Step("Assert that more than 1 condition is visible")
    fun assertMoreThanOneConditionIsVisible(): ConditionRobot {
        VintedAssert.assertTrue(conditionElementList.size > 1, "More than 1 condition should be visible but found ${conditionElementList.size} visible conditions")
        return this
    }

    @Step("Select first condition")
    fun selectFirstCondition(): UploadItemRobot {
        conditionFirstElement.click()
        return UploadItemRobot()
    }

    @Step("Select {selectedCondition} condition")
    fun selectCondition(selectedCondition: String): ConditionRobot {
        conditionElement(selectedCondition).withScrollIos().click()
        return this
    }
}
