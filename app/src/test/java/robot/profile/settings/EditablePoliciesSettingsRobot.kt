package robot.profile.settings

import robot.BaseRobot
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement

class EditablePoliciesSettingsRobot : BaseRobot() {
    private val termsAndConditionsInputTab: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("termsAndConditions"))

    private val returnPolicyInputTab: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("returnPolicy"))

    private val additionalInformationInputTab: VintedElement
        get() = VintedDriver.findElement(androidBy = VintedBy.id("additionalInformation"))
}
