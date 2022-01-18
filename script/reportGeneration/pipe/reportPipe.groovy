System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-same-origin allow-scripts; default-src 'self'; script-src * 'unsafe-inline'; img-src *; style-src * 'unsafe-inline'; font-src *")

import groovy.xml.MarkupBuilder
import hudson.EnvVars
import hudson.FilePath

class BuildEnvironmentPipe {

    private def env
    private def Map portalsMap

    BuildEnvironmentPipe(script) {
        this.env = script.env
    }

    @NonCPS
    def setPortalsMap(Map portalsMap) {
        this.portalsMap = portalsMap
    }

    @NonCPS
    def getPortals() {
        return portalsMap.collect { entry -> entry.key }
    }

    @NonCPS
    def getBuildResult(def portal) {
        portalsMap.get(portal).result.lowercase()
    }

    @NonCPS
    def getAbsoluteUrl(def portal) {
        portalsMap.get(portal).absoluteUrl
    }

    @NonCPS
    def getBuildNumber(def portal) {
        portalsMap.get(portal).buildNumber
    }

    def getHudsonUrl() {
        return env.HUDSON_URL.toString()
    }
}

class ReportWorkflowPipe {

    private BuildEnvironmentPipe build

    ReportWorkflowPipe(BuildEnvironmentPipe build) {
        this.build = build
    }

    def generateBuildNumberName(Integer index) {
        return generateName(index, "BUILD_NUMBER")
    }

    @NonCPS
    def generateAllureUrl(def portal) {
        return "${generateBaseUrl(portal)}/allure"
    }

    @NonCPS
    def generateBaseUrl(def portal) {
        return build.getAbsoluteUrl(portal)
    }

    @NonCPS
    def getPortals() {
        return build.getPortals()
    }

    @NonCPS
    def getBuildResult(def portal) {
        return build.getBuildResult(portal)
    }

    @NonCPS
    def getReportName(def portal) {
        return "${portal}_${build.getBuildNumber(portal)}"
    }

    @NonCPS
    static
    def getCollectionName() {
        return "Allure API tests Report"
    }
}

class ReportGeneration {
    def buildEnvironment
    def reportWorkflow

    ReportGeneration(script) {
        this.buildEnvironment = new BuildEnvironmentPipe(script)
        this.reportWorkflow = new ReportWorkflowPipe(this.buildEnvironment)
    }

    def generateReport() {
        def portals = reportWorkflow.getPortals()
        return createHtml(portals)
    }

    @NonCPS
    def createHtml(portals) {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        markup.html {
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
                            li() {
                                h4 "PORTALS"
                            }
                            portals.each { portal ->
                                def url = reportWorkflow.generateAllureUrl(portal)
                                def color
                                def status = reportWorkflow.getBuildResult(portal)
                                switch (status) {
                                    case "success":
                                        color = "LightGreen"
                                        break;
                                    case "failure":
                                        color = "LightPink"
                                        break;
                                    default:
                                        color = "LightGrey"
                                        url = reportWorkflow.generateBaseUrl(portal)
                                }
                                li(style: "background-color:${color}") {
                                    a href: "${url}", reportWorkflow.getReportName(portal), target: "iframe"
                                }
                            }
                        }
                    }
                }
                article(style: "float:right;padding:20px;width:85%;height:100%;") {
                    iframe width: "100%", height: "100%", name: "iframe", src: ""
                }
            }
        }
        return writer.toString()
    }
}

return new ReportGeneration(this)
