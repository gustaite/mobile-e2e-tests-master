package robot.upload

import io.qameta.allure.Step
import robot.BaseRobot
import util.Android
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class MaterialRobot : BaseRobot() {

    private val materialFirstElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.setWithParentAndChild("material_row", Android.CELL_TITLE_FIELD_ID),
            iOSBy = VintedBy.iOSClassChain("**/XCUIElementTypeCollectionView/XCUIElementTypeCell[1]")
        )

    private fun materialElement(selectedMaterial: String): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder(text = selectedMaterial, scroll = true),
            iOSBy = VintedBy.iOSTextByBuilder(text = selectedMaterial, onlyVisibleInScreen = true)
        )

    @Step("Select first material")
    fun selectFirstMaterial(): UploadItemRobot {
        materialFirstElement.click()
        return UploadItemRobot()
    }

    @Step("Select {selectedMaterial} material")
    fun selectMaterial(selectedMaterial: String): UploadItemRobot {
        materialElement(selectedMaterial).withScrollIos().click()
        return UploadItemRobot()
    }
}
