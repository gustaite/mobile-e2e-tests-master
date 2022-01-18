package util

import io.qameta.allure.Step
import org.openqa.selenium.*

class Util {
    companion object {
        @Step("[INFO] Retry action")
        fun retryOnException(block: () -> Unit, count: Int) {
            for (i in 0 until count) {
                try {
                    block()
                    return
                } catch (e: WebDriverException) {
                    commonUtil.reporting.Report.addMessage("Retry count: $i")
                    commonUtil.reporting.Report.addMessage("error: $e")
                }
            }
        }
    }

    enum class SearchTextOperator(val androidUiSelectorTextMethodName: String, val iOSOperator: String) {
        MATCHES("textMatches", "MATCHES"),
        EXACT("text", "=="),
        CONTAINS("textContains", "CONTAINS"),
        STARTS_WITH("textStartsWith", "BEGINSWITH")
    }
}
