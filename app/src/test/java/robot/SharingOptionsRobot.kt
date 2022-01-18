package robot

import commonUtil.asserts.VintedAssert
import io.qameta.allure.Step
import util.Session.Companion.sessionDetails
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import util.values.LaboratoryDevice

class SharingOptionsRobot : BaseRobot() {

    private val sharingOptionsViewElement: VintedElement
        get() = VintedDriver.findElement(
            androidBy = VintedBy.id("android:id/resolver_list"),
            iOSBy = VintedBy.accessibilityId("ActivityListView")
        )

    private val samsungSharingOptionsViewElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("android:id/sem_resolver_pagemode_page_list"))
    private val samsungGalaxyNote20SharingOptionsViewElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("android:id/chooser_row_text_option"))
    private val lgSharingOptionsViewElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("com.lge:id/resolver_list_lg"))
    private val xiaomiSharingOptionsViewElement: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("android.miui:id/resolver_grid"))

    @Step("Assert sharing options are visible")
    fun assertSharingOptionsAreVisible() {
        val element = when {
            sessionDetails.deviceModel == LaboratoryDevice.S6_EDGE.model -> samsungSharingOptionsViewElement
            sessionDetails.deviceModel in listOf(
                LaboratoryDevice.GALAXY_NOTE_20.model, LaboratoryDevice.S20_FE.model,
                LaboratoryDevice.S20.model, LaboratoryDevice.M51.model
            ) -> samsungGalaxyNote20SharingOptionsViewElement
            sessionDetails.deviceManufacturer == "LGE" -> lgSharingOptionsViewElement
            sessionDetails.deviceManufacturer == "Xiaomi" -> xiaomiSharingOptionsViewElement
            else -> sharingOptionsViewElement
        }
        VintedAssert.assertTrue(element.withWait().isVisible(), "Sharing options should be visible")
    }
}
