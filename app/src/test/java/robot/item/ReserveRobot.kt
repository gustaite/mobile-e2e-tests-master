package robot.item

import io.qameta.allure.Step
import api.data.models.VintedUser

class ReserveRobot : ReserveSellBaseRobot() {

    @Step("Reserve item for user {user.username}")
    fun reserve(user: VintedUser): ItemRobot {
        selectUser(user)
        return ItemRobot()
    }
}
