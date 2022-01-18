package unitTest

import commonUtil.asserts.VintedAssert
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testng.xml.XmlClass
import org.testng.xml.XmlInclude
import util.testng.ReliabilitySuite

class ReliabilitySuiteTest {
    companion object {
        private const val test1 = "test1"
        private const val test2 = "test2"
        private const val test3 = "test3"
        private const val test4 = "test4"
        private const val test5 = "test5"
        private const val testsNamesFromClassExplanationMsg = "Tests names from XmlClass does not match expected"
        private const val testsCountOnSuiteExplanationMsg = "Tests count on Suite does not match expected"
        private const val nodesCountExplanationMsg = "Nodes count does not match expected"
    }
    @Before
    fun before() {
        mockkObject(ReliabilitySuite)
        every { ReliabilitySuite.getAnnotatedTests(any()) } returns listOf(test1, test2, test3, test4, test5)
    }

    @Test
    fun getTestsOnClass_excluded() {
        val xmlClass = XmlClass()
        xmlClass.excludedMethods = listOf(test1, test5)
        VintedAssert.assertEquals(ReliabilitySuite.getTestsFromClass(xmlClass), listOf(test2, test3, test4), testsNamesFromClassExplanationMsg)
    }

    @Test
    fun getTestsOnClass_included() {
        val xmlClass = XmlClass()
        xmlClass.includedMethods = listOf(XmlInclude(test1), XmlInclude(test2))
        VintedAssert.assertEquals(ReliabilitySuite.getTestsFromClass(xmlClass), listOf(test1, test2), testsNamesFromClassExplanationMsg)
    }

    @Test
    fun getTestsOnClass_all() {
        val xmlClass = XmlClass()
        VintedAssert.assertEquals(ReliabilitySuite.getTestsFromClass(xmlClass), listOf(test1, test2, test3, test4, test5), testsNamesFromClassExplanationMsg)
    }

    @Test
    fun getTestsOnClass_excludedAndIncluded() {
        val xmlClass = XmlClass()
        xmlClass.includedMethods = listOf(XmlInclude(test2))
        xmlClass.excludedMethods = listOf(test1, test5)
        VintedAssert.assertEquals(ReliabilitySuite.getTestsFromClass(xmlClass), listOf(test2), testsNamesFromClassExplanationMsg)
    }

    @Test
    fun getTestsCountOnSuite_oneClass() {
        val xmlClass1 = XmlClass()
        xmlClass1.includedMethods = listOf(XmlInclude(test2), XmlInclude(test4))
        VintedAssert.assertEquals(ReliabilitySuite.getTestsCountOnClasses(mutableListOf(xmlClass1)), 2, testsCountOnSuiteExplanationMsg)
    }

    @Test
    fun getTestsCountOnSuite_twoClasses() {
        val xmlClass1 = XmlClass()
        xmlClass1.includedMethods = listOf(XmlInclude(test2), XmlInclude(test4))
        val xmlClass2 = XmlClass()

        VintedAssert.assertEquals(ReliabilitySuite.getTestsCountOnClasses(mutableListOf(xmlClass1, xmlClass2)), 7, testsCountOnSuiteExplanationMsg)
    }

    @Test
    fun getInvocationThreadPoolSize_AllNodes() {
        every { ReliabilitySuite.gridNodesCount } returns 10
        every { ReliabilitySuite.testsCount } returns 2
        VintedAssert.assertEquals(ReliabilitySuite.invocationThreadPoolSize, 5, nodesCountExplanationMsg)
    }

    @Test
    fun getInvocationThreadPoolSize_MostNodes() {
        every { ReliabilitySuite.gridNodesCount } returns 20
        every { ReliabilitySuite.testsCount } returns 6
        VintedAssert.assertEquals(ReliabilitySuite.invocationThreadPoolSize, 3, nodesCountExplanationMsg)
    }

    @Test
    fun getInvocationThreadPoolSize_LessNodesThanTests() {
        every { ReliabilitySuite.gridNodesCount } returns 5
        every { ReliabilitySuite.testsCount } returns 6
        VintedAssert.assertEquals(ReliabilitySuite.invocationThreadPoolSize, 1, nodesCountExplanationMsg)
    }

    @After
    fun afterTests() {
        unmockkAll()
    }
}
