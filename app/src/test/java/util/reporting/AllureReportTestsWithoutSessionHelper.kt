package util.reporting

import io.qameta.allure.Allure
import org.testng.IInvokedMethod
import util.values.Devices
import java.lang.reflect.Method

object AllureReportTestsWithoutSessionHelper {
    private val testsWithoutDriverSession: MutableMap<String, String> = mutableMapOf()

    fun extractDeviceUdidFromErrorMessage(errorMessage: String, method: Method?) {
        if (method == null)
            return
        runCatching {
            val regex = "-s ([a-zA-Z0-9]+)".toRegex()
            if (errorMessage.contains("Error executing adbExec") && regex.containsMatchIn(errorMessage)) {
                val udid = regex.find(errorMessage)!!.value.replace("-s ", "")
                testsWithoutDriverSession["${method.declaringClass.`package`.name}.${method.name}"] = udid
            }
        }
    }

    fun addDeviceNameAndUdid(method: IInvokedMethod) {
        runCatching {
            if (method.isTestMethod) {
                val test = "${method.testMethod.realClass.`package`.name}.${method.testMethod.methodName}"
                if (testsWithoutDriverSession.containsKey(test)) {
                    val udid = testsWithoutDriverSession[test]!!
                    val deviceName = Devices.getName(udid)
                    Allure.feature("By device: $deviceName")
                    Allure.parameter("1. Device", deviceName)
                    Allure.parameter("2. UDID", udid)
                    Allure.getLifecycle().updateTestCase { testResult ->
                        testResult.name = "[PHONE ISSUE] ${testResult.name}"
                    }
                }
            }
        }
    }
}
