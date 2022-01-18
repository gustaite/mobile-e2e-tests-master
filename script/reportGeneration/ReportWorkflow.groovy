@GrabResolver(name = 'jenkins-ci-org-releases', root = 'http://repo.jenkins-ci.org/releases/')
@Grab(group = 'org.jenkins-ci.main', module = 'jenkins-core', version = '2.245')

import hudson.model.*
import hudson.EnvVars
import hudson.FilePath

class ReportWorkflow {

    private BuildEnvironment build

    ReportWorkflow(BuildEnvironment build) {
        this.build = build
    }

    def getFilePath() {
        def FilePath fp
        if (build.isRemoteWorkspace()) {
            def channel = build.getWorkspaceChannel()
            fp = new FilePath(channel, build.getWorkspacePath() + "/report.html")
        } else {
            fp = new FilePath(new File(build.getWorkspacePath() + "/report.html"))
        }
    }

    def generateBuildNumberName(Integer index) {
        return generateName(index, "BUILD_NUMBER")
    }

    def generateBuildResultName(Integer index) {
        return generateName(index, "BUILD_RESULT")
    }

    def generateAllureUrl(Integer index) {
        return "${generateBaseUrl(index)}/allure"
    }

    def generateBaseUrl(Integer index) {
        def jobName = getJobName(index)
        def hudsonUrl = build.getHudsonUrl()
        def buildNumber = getBuildNumber(index)
        return "${hudsonUrl}job/$jobName/$buildNumber"
    }

    def getPortals() {
        def portal = build.getPortal()
        def sandboxPortal = build.getSandboxPortal()
        def portals = [portal, sandboxPortal].findAll({ it != null && it != "" && it != "null" }).join(",").replaceAll("[,]+", ",").split(",")
        return portals
    }

    def getBuildNumber(Integer index) {
        def variableName = generateBuildNumberName(index)
        return build.getVariable(variableName)
    }

    def getBuildResult(Integer index) {
        def variableName = generateBuildResultName(index)
        return build.getVariable(variableName).lowercase()
    }

    def getReportName(Integer index) {
        def portals = getPortals()
        def buildNumber = getBuildNumber(index)
        def reportName
        if (isE2eTest()) {
            reportName = "#${portals[index]}_$buildNumber"
        } else {
            def jobName = getJobName(index).replace(" api runner", "")
            reportName = "#${jobName}_$buildNumber"
        }
        return reportName

    }

    def getCollectionName() {
        def platform = build.getPlatform()
        def appVersion = build.getAppVersion()
        def middlePartName
        if (isE2eTest()) {
            middlePartName = "$platform $appVersion"
        } else {
            middlePartName = "API tests"
        }
        return "Allure $middlePartName Report"
    }

    def isE2eTest() {
        build.getTriggeredJobNames().toString().lowercase().contains("e2e")
    }

    def getJobName(Integer index) {
        build.getTriggeredJobNames().split(',')[index]
    }

    def getJobsCount() {
        def jobs = build.getTriggeredJobNames().toString()
        if (isE2eTest()) {
            jobs.count("e2e-tests")
        } else {
            jobs.count("runner")
        }
    }

    private def generateName(Integer index, String buildName) {
        def name = getJobName(index)
        def count = getJobsCount()
        def generatedName
        if (name.contains("runner")) {
            generatedName = "${name.toUpperCase()}_${buildName}".replace("-", "_").replace(" ", "_")
        } else {
            if (count == 1) {
                generatedName = "${name.toUpperCase()}_${buildName}".replace("-", "_")
            } else {
                generatedName = "${name.toUpperCase()}_${index + 1}_${buildName}".replace("-", "_")
            }

        }

        return generatedName
    }
}
