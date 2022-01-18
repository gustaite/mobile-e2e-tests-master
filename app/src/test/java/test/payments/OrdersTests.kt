package test.payments

import RobotFactory.navigationRobot
import commonUtil.testng.LoginToDefaultUser
import commonUtil.testng.config.VintedCountry
import commonUtil.testng.config.VintedEnvironment
import commonUtil.testng.mobile.RunMobile
import io.qameta.allure.Feature
import io.qameta.allure.TmsLink
import org.testng.annotations.Test
import util.base.BaseTest
import util.testng.*
import util.values.Visibility

@Feature("Order screen tests")
@LoginToDefaultUser
class OrdersTests : BaseTest() {

    @RunMobile(env = VintedEnvironment.SANDBOX, country = VintedCountry.SANDBOX_PAYMENT_COUNTRIES)
    @Test(description = "Test my orders screen")
    @TmsLink("97")
    fun testMyOrdersScreen() {
        navigationRobot
            .openProfileTab()
            .openMyOrdersScreen()
            .checkIfTransactionCellIsVisible()
            .openTransaction()
            .assertMessageInputVisibility(Visibility.Visible)
    }
}
