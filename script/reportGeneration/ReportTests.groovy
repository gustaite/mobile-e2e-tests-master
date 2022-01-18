@GrabResolver(name = 'jenkins-ci-org-releases', root = 'http://repo.jenkins-ci.org/releases/')
@Grab(group = 'org.jenkins-ci.main', module = 'jenkins-core', version = '2.245')

import groovy.util.GroovyTestCase
import groovy.mock.interceptor.*
import hudson.model.*
import hudson.EnvVars
import hudson.FilePath
import hudson.remoting.*
import java.util.concurrent.ForkJoinPool

class BuildEnvironmentTests extends GroovyTestCase {
    private MockFor mock

    static String expectedHudsonUrl = 'http://192.168.16.16:8080/'

    def void setUp() {
        mock = new MockFor(BuildEnvironment)
    }

    def void tearDown() {
        mock = null
    }

    def void loopFunction(Integer count, Closure c) {
        for (int i = 0; i < count; i++) {
            c()
        }
    }

    def void testHomeFilePath() {
        mock.demand.with {
            isRemoteWorkspace() { false }
            getWorkspacePath() { '/Users/steve/.jenkins/workspace/mobile-e2e-tests-android-all' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def fp = reportWorkflow.getFilePath()
            assert "/Users/steve/.jenkins/workspace/mobile-e2e-tests-android-all/report.html" == fp.toString()
        }
    }

    def void testRemoteFilePath() {
        mock.demand.with {
            isRemoteWorkspace() { true }
            getWorkspaceChannel() { new LocalChannel(new ForkJoinPool()) }
            //Probably should be Channel but no idea how to mock it
            getWorkspacePath() { '/Users/steve/.jenkins/workspace/mobile-e2e-tests-android-all' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def fp = reportWorkflow.getFilePath()
            assert "/Users/steve/.jenkins/workspace/mobile-e2e-tests-android-all/report.html" == fp.toString()
        }
    }

    def void testGenerateBuildNumberWithMultipleJobs_RUNNER_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildNumberName(4)
            assert 'SB_PL_API_RUNNER_BUILD_NUMBER' == name
        }
    }

    def void testGenerateBuildNumberWithMultipleJobsMixedOrder_RUNNER_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_LT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_DE api runner,SB_PL api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildNumberName(4)
            assert 'SB_US_API_RUNNER_BUILD_NUMBER' == name
        }
    }

    def void testGenerateBuildNumberWithMultipleJobs_E2E_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildNumberName(4)
            assert 'MOBILE_E2E_TESTS_5_BUILD_NUMBER' == name
        }
    }

    def void testGenerateBuildNumberWithOneJob_Runner_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildNumberName(0)
            assert 'SB_INT_API_RUNNER_BUILD_NUMBER' == name
        }
    }

    def void testGenerateBuildNumberWithOneJob_E2E_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildNumberName(0)
            assert 'MOBILE_E2E_TESTS_BUILD_NUMBER' == name
        }
    }

    def void testGenerateBuildResultWithMultipleJobs_RUNNER_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildResultName(3)
            assert 'SB_US_API_RUNNER_BUILD_RESULT' == name
        }
    }

    def void testGenerateBuildResultWithMultipleJobs_E2E_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildResultName(2)
            assert 'MOBILE_E2E_TESTS_3_BUILD_RESULT' == name
        }
    }

    def void testGenerateBuildResultWithOneJob_RUNNER_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildResultName(0)
            assert 'SB_CZ_API_RUNNER_BUILD_RESULT' == name
        }

    }

    def void testGenerateBuildResultWithOneJob_E2E_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def name = reportWorkflow.generateBuildResultName(0)
            assert 'MOBILE_E2E_TESTS_BUILD_RESULT' == name
        }

    }


    def void testGenerateAllureUrl_Runner_test() {
        mock.demand.with {
            loopFunction(1, { getTriggeredJobNames() { 'SB_INT api runner' } })
            getHudsonUrl() { expectedHudsonUrl }
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner' } })
            getVariable() { '11655' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def url = reportWorkflow.generateAllureUrl(0)
            assert "${expectedHudsonUrl}job/SB_INT api runner/11655/allure" == url
        }
    }

    def void testGenerateAllureUrl_E2E_tests() {
        mock.demand.with {
            loopFunction(1, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
            getHudsonUrl() { expectedHudsonUrl }
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
            getVariable() { '11655' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def url = reportWorkflow.generateAllureUrl(0)
            assert "${expectedHudsonUrl}job/mobile-e2e-tests/11655/allure" == url
        }
    }

    def void testGenerateAllureUrlWithIndex1_E2E_tests() {
        mock.demand.with {
            loopFunction(1, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests' } })
            getHudsonUrl() { expectedHudsonUrl }
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests' } })
            getVariable() { '11655' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def url = reportWorkflow.generateAllureUrl(1)
            assert "${expectedHudsonUrl}job/mobile-e2e-tests/11655/allure" == url
        }
    }

    def void testGenerateBaseUrl() {
        mock.demand.with {
            loopFunction(1, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
            getHudsonUrl() { expectedHudsonUrl }
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests' } })
            getVariable() { '11655' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def url = reportWorkflow.generateBaseUrl(0)
            assert "${expectedHudsonUrl}job/mobile-e2e-tests/11655" == url
        }
    }

    def void testGetPortals() {
        mock.demand.with {
            getPortal() { 'PL,LT' }
            getSandboxPortal() { 'SB_UK' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def portals = reportWorkflow.getPortals()
            assert '[PL, LT, SB_UK]' == portals.toString()
        }
    }

    def void testGetPortalsOnlySandbox() {
        mock.demand.with {
            getPortal() { '' }
            getSandboxPortal() { 'SB_CZ' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def portals = reportWorkflow.getPortals()
            assert '[SB_CZ]' == portals.toString()
        }
    }

    def void testGetBuildNumber() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
            getVariable() { "145456" }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def buildNumber = reportWorkflow.getBuildNumber(3)
            assert '145456' == buildNumber
        }
    }

    def void testGetBuildResult() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
            getVariable() { "SUCCESS" }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def buildResult = reportWorkflow.getBuildResult(3)
            assert 'success' == buildResult
        }
    }

    def void testGetBuildResult_RUNNER_test() {
        mock.demand.with {
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner' } })
            getVariable() { "SUCCESS" }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            def buildResult = reportWorkflow.getBuildResult(3)
            assert 'success' == buildResult
        }
    }

    def void testGetReportCollectionNameForVersion() {
        mock.demand.with {
            getPlatform() { "Android" }
            getAppVersion() { "20.14.0.0" }
            getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert 'Allure Android 20.14.0.0 Report' == reportWorkflow.getCollectionName()
        }
    }

    def void testGetReportCollectionNameForMaster() {
        mock.demand.with {
            getPlatform() { "Android" }
            getAppVersion() { '' }
            getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert 'Allure Android  Report' == reportWorkflow.getCollectionName()
        }
    }

    def void testGetReportCollectionNameForApiTests() {
        mock.demand.with {
            getPlatform() { "Android" }
            getAppVersion() { '' }
            getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' }
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert 'Allure API tests Report' == reportWorkflow.getCollectionName()
        }
    }

    def void testGetFirstReportName() {
        mock = mockDemandsForGetReportNameTests()
        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#PL_145456" == reportWorkflow.getReportName(0)
        }
    }

    def void testGetSecondReportName() {
        def mock = mockDemandsForGetReportNameTests()
        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#LT_145456" == reportWorkflow.getReportName(1)
        }
    }

    def void testGetThirdReportName() {
        mock = mockDemandsForGetReportNameTests()
        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#SB_UK_145456" == reportWorkflow.getReportName(2)
        }
    }

    def void testGetFirstReportName_Runner_job() {
        mock.demand.with {
            getPortal() { 'null' }
            getSandboxPortal() { 'SB_INT,SB_DE,SB_UK,SB_US,SB_PL,SB_LT,SB_CZ' }
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
            getVariable() { "17" }
            loopFunction(2, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#SB_INT_17" == reportWorkflow.getReportName(0)
        }
    }

    def void testGetLastReportName_Runner_job() {
        mock.demand.with {
            getPortal() { 'INT,DE,UK,US,PL,LT,CZ' }
            getSandboxPortal() { 'null' }
            loopFunction(3, { getTriggeredJobNames() { 'INT api runner,DE api runner,UK api runner,US api runner,PL api runner,LT api runner,CZ api runner' } })
            getVariable() { "17" }
            loopFunction(2, { getTriggeredJobNames() { 'INT api runner,DE api runner,UK api runner,US api runner,PL api runner,LT api runner,CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#CZ_17" == reportWorkflow.getReportName(6)
        }
    }

    def void testGetLastReportNameMixedTriggeredOrder_Runner_job() {
        mock.demand.with {
            getPortal() { 'null' }
            getSandboxPortal() { 'SB_INT,SB_DE,SB_UK,SB_US,SB_PL,SB_LT,SB_CZ' }
            loopFunction(3, { getTriggeredJobNames() { 'SB_INT api runner,SB_LT api runner,SB_PL api runner,SB_DE api runner,SB_US api runner,SB_UK api runner,SB_CZ api runner' } })
            getVariable() { "17" }
            loopFunction(2, { getTriggeredJobNames() { 'SB_INT api runner,SB_LT api runner,SB_PL api runner,SB_DE api runner,SB_US api runner,SB_UK api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "#SB_LT_17" == reportWorkflow.getReportName(1)
        }
    }

    def void testGetJobNameByIndex_Runner_test() {
        mock.demand.with {
            loopFunction(2, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "SB_UK api runner" == reportWorkflow.getJobName(2)
            assert "SB_CZ api runner" == reportWorkflow.getJobName(6)
        }
    }

    def void testGetJobNameByIndex_E2E_test() {
        mock.demand.with {
            loopFunction(2, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert "mobile-e2e-tests" == reportWorkflow.getJobName(0)
            assert "mobile-e2e-tests" == reportWorkflow.getJobName(6)
        }
    }


    def void testGetJobsCount_Runner_test() {
        mock.demand.with {
            loopFunction(2, { getTriggeredJobNames() { 'SB_INT api runner,SB_DE api runner,SB_UK api runner,SB_US api runner,SB_PL api runner,SB_LT api runner,SB_CZ api runner' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert 7 == reportWorkflow.getJobsCount()
        }
    }

    def void testGetJobsCount_E2E_test() {
        mock.demand.with {
            loopFunction(2, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
        }

        mock.use {
            def reportWorkflow = createReportWorkflow()
            assert 7 == reportWorkflow.getJobsCount()
        }
    }


    private def mockDemandsForGetReportNameTests() {
        mock.demand.with {
            getPortal() { 'PL,LT' }
            getSandboxPortal() { 'SB_UK' }
            loopFunction(3, { getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' } })
            getVariable() { "145456" }
            getTriggeredJobNames() { 'mobile-e2e-tests,mobile-e2e-tests,mobile-e2e-tests' }
        }
        return mock
    }


    private def createReportWorkflow() {
        def build = new BuildEnvironment(null, null)
        def reportWorkflow = new ReportWorkflow(build)
    }
}
