package util.testng

import com.google.common.math.IntMath
import commonUtil.reporting.AnnotationExtractor
import org.testng.annotations.Test
import org.testng.xml.Parser
import org.testng.xml.XmlClass
import util.EnvironmentManager
import util.SeleniumGridManager
import java.io.File
import java.math.RoundingMode

object ReliabilitySuite {

    val gridNodesCount get() = SeleniumGridManager.getGridNodesCount().let { if (EnvironmentManager.isAndroid) it.android else it.ios }
    private val testClasses = getTestClassesOnReliabilitySuite()
    val testsCount: Int get() = getTestsCountOnClasses(testClasses)
    val invocationThreadPoolSize: Int get() = calculateInvocationCount()

    fun getTestsCountOnClasses(testClasses: MutableList<XmlClass>): Int {
        val testsList: MutableList<String> = mutableListOf()
        testClasses.forEach { testClass ->
            val testsOnClass = getTestsFromClass(testClass)
            testsList.addAll(testsOnClass)
        }
        return testsList.count()
    }

    fun getAnnotatedTests(className: String): List<String> {
        return AnnotationExtractor.getAllFunctionNamesAnnotatedWith(Test::class, className)
    }

    fun getTestsFromClass(testClass: XmlClass): List<String> {
        val testsByAnnotation = getAnnotatedTests(testClass.name)
        val testsFromClass = mutableListOf<String>()
        val isIncludedMethods = testClass.includedMethods.isNotEmpty()
        val isExcludedMethods = testClass.excludedMethods.isNotEmpty()

        when {
            isIncludedMethods -> {
                testsFromClass.addAll(testClass.includedMethods.map { it.name })
            }
            isExcludedMethods && !isIncludedMethods -> {
                testsFromClass.addAll(testsByAnnotation)
                testsFromClass.removeAll(testClass.excludedMethods)
            }
            else -> {
                testsFromClass.addAll(testsByAnnotation)
            }
        }
        return testsFromClass
    }

    private fun calculateInvocationCount(): Int {
        return if (gridNodesCount > testsCount) {
            IntMath.divide(gridNodesCount, testsCount, RoundingMode.DOWN)
        } else {
            1
        }
    }

    private fun getTestClassesOnReliabilitySuite(): MutableList<XmlClass> {
        val file = File("${System.getProperty("user.dir")}/reliability.xml")
        if (file.exists()) {
            val suite = Parser(file.absolutePath).parse().first().tests.first()
            return suite.classes
        }
        return mutableListOf()
    }
}
