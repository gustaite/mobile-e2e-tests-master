package robot

import commonUtil.data.enums.VintedPortal
import commonUtil.testng.config.PortalFactory
import io.qameta.allure.Step
import util.Android
import util.IOS
import util.Util
import util.VintedDriver
import util.driver.VintedBy
import util.driver.VintedElement
import java.text.SimpleDateFormat
import java.util.*

class CalendarRobot : BaseRobot() {

    private val okButton: VintedElement get() = VintedDriver.findElement(
        VintedBy.id("android:id/button1"),
        VintedBy.iOSClassChain("**/XCUIElementTypeToolbar[`name == 'Toolbar'`]/**/XCUIElementTypeButton")
    )

    private val backButtonAndroid: VintedElement get() = VintedDriver.findElement(
        androidBy = VintedBy.id("android:id/prev")
    )

    private val datePickerWheelElement: List<VintedElement> get() = VintedDriver.findElementList(
        iOSBy = VintedBy.className("XCUIElementTypePickerWheel")
    )

    private val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private val yesterdayDay = todayDay - 1

    private fun yesterdayDayElementAndroid(yesterdayDay: Int): VintedElement =
        VintedDriver.findElement(
            androidBy = VintedBy.androidTextByBuilder("$yesterdayDay", searchType = Util.SearchTextOperator.MATCHES)
        )

    @Step("US only: Select yesterday in calendar")
    fun selectYesterdayInCalendar() {
        if (PortalFactory.isCurrentRegardlessEnv(VintedPortal.US)) {
            if (todayDay == 1) {
                selectTheLastDayOfPreviousMonth()
            } else {
                Android.doIfAndroid { yesterdayDayElementAndroid(yesterdayDay).click() }
                IOS.doIfiOS { datePickerWheelElement[0].sendKeys(yesterdayDay.toString()) }
            }
        }
    }

    @Step("Click ok in calendar")
    fun clickOkInCalendar() {
        okButton.click()
    }

    @Step("Select the last day of previous month")
    private fun selectTheLastDayOfPreviousMonth() {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val theLastDayOfPreviousMonth = cal.get(Calendar.DAY_OF_MONTH)
        val previousMonthName = SimpleDateFormat("MMMM").format(cal.time)
        Android.doIfAndroid {
            clickBackInCalendar()
            yesterdayDayElementAndroid(theLastDayOfPreviousMonth).click()
        }
        IOS.doIfiOS {
            datePickerWheelElement[0].sendKeys(theLastDayOfPreviousMonth.toString())
            datePickerWheelElement[1].sendKeys(previousMonthName.toString())
        }
    }

    @Step("Android: Click back in calendar")
    fun clickBackInCalendar() {
        backButtonAndroid.click()
    }
}
