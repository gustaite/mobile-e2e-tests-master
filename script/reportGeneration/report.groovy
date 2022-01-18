System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-same-origin allow-scripts; default-src 'self'; script-src * 'unsafe-inline'; img-src *; style-src * 'unsafe-inline'; font-src *")

import groovy.xml.MarkupBuilder
import hudson.EnvVars
import hudson.FilePath

class BuildEnvironment {

	private def EnvVars env
	private def workspace

	BuildEnvironment(EnvVars env, def workspace) {
		this.env = env
		this.workspace = workspace
	}

	def getTriggeredJobNames() {
		return env.get("TRIGGERED_JOB_NAMES")
	}

	def getPlatform() {
		return env.get("PLATFORM").toString()
	}

	def getAppVersion() {
		return env.get("APP_VERSION").toString()
	}

	def getSandboxPortal() {
		return env.get("APPSANDBOXPORTAL").toString()
	}

	def getPortal() {
		return env.get("PORTAL").toString()
	}

	def getHudsonUrl() {
		return env.get("HUDSON_URL").toString()
	}

	def getVariable(String name) {
		return env.get(name).toString()
	}

	def isRemoteWorkspace() {
		return workspace.isRemote()
	}

	def getWorkspacePath() {
		return workspace.toString()
	}

	def getWorkspaceChannel() {
		return workspace.channel
	}
}

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

EnvVars envVars = build.getEnvironment();
def workspace = build.workspace
def buildEnvironment = new BuildEnvironment(envVars, workspace)
def reportWorkflow = new ReportWorkflow(buildEnvironment)

def jobCount = reportWorkflow.getJobsCount()
def platform = buildEnvironment.getPlatform()

def fp = reportWorkflow.getFilePath()
def writer = new StringWriter()
def markup = new MarkupBuilder(writer)

markup.html{
    head {
        title: "Allure report"
    }
    body(id: "main") {
        h2 align: "center", "${reportWorkflow.getCollectionName()} ${new Date().format("yyyy-MM-dd HH:mm:ss")}"

        p {

        }
        section(style: "float:left;") {
            nav {
                ul(style: "list-style-type:none;padding:0;") {
                    for (int i = 0; i <= jobCount - 1; i++) {
                        def url = reportWorkflow.generateAllureUrl(i)
                        def color
                        def status = reportWorkflow.getBuildResult(i)
                        switch(status) {
                            case "success":
                                color = "LightGreen"
                                break;
                            case "failure":
                                color = "LightPink"
                                break;
                            default:
                                color = "LightGrey"
                                url = reportWorkflow.generateBaseUrl(i)
                        }
                        li(style:"background-color:${color}") {
                            a href: "${url}", reportWorkflow.getReportName(i), target: "iframe"
                        }
                    }
                }
            }
        }
        article(style: "float:right;padding:20px;width:85%;height:100%;") {
            iframe width: "100%", height:"100%", name: "iframe", src: ""
        }
    }
}

println writer.toString()
fp.write(writer.toString(), null)
