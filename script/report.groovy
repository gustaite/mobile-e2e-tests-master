System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "sandbox allow-same-origin allow-scripts; default-src 'self'; script-src * 'unsafe-inline'; img-src *; style-src * 'unsafe-inline'; font-src *")

import groovy.xml.MarkupBuilder
import hudson.EnvVars
import hudson.FilePath

def jobName = "mobile-e2e-tests"
EnvVars envVars = build.getEnvironment()

def jobCount = envVars.get("TRIGGERED_JOB_NAMES").toString().count(jobName)
def platform = envVars.get("PLATFORM").toString()
def sandboxportal = envVars.get("APPSANDBOXPORTAL").toString()
def portal = envVars.get("PORTAL").toString()
def portals = [portal, sandboxportal].findAll({it != null && it != ""}).join(",").replaceAll("[,]+", ",").split(",")
def vpn = envVars.get("VPN_IP").toString()
theDir = new File(envVars.get('WORKSPACE'))
println theDir.exists()

if(build.workspace.isRemote())
{
    channel = build.workspace.channel;
    fp = new FilePath(channel, build.workspace.toString() + "/report.html")
} else {
    fp = new FilePath(new File(build.workspace.toString() + "/report.html"))
}

def writer = new StringWriter()

def markup = new MarkupBuilder(writer)

markup.html{
    head {
        title: "Allure report"
    }
    body(id: "main") {
        h2 align: "center", "Allure $platform ${envVars.get("APP_VERSION")} Report ${new Date().format("yyyy-MM-dd HH:mm:ss")}"

        p {

        }
        section(style: "float:left;") {
            nav {
                ul(style: "list-style-type:none;padding:0;") {
                    for (int i = 1; i <= jobCount; i++) {
                        def buildNumber = envVars.get(generateName(i, jobName, "BUILD_NUMBER", jobCount))
                        def buildResult = envVars.get(generateName(i, jobName, "BUILD_RESULT", jobCount))
                        def url = "${envVars.get("HUDSON_URL")}job/${jobName}/${buildNumber}/allure"
                        def color
                        def status = buildResult.toString().lowercase()
                        switch(status) {
                            case "success":
                                color = "LightGreen"
                                break;
                            case "failure":
                                color = "LightPink"
                                break;
                            default:
                                color = "LightGrey"
                                url = "${envVars.get("HUDSON_URL")}job/${jobName}/${buildNumber}"
                        }
                        li(style:"background-color:${color}") {
                            a href: "${url}", "#${portals[i-1]}_${buildNumber}", target: "iframe"
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

def generateName(Integer index, String name, String build, Integer count) {
    if(count == 1) {
        return "${name.toUpperCase()}_${build}".replace("-", "_")
    }
    return "${name.toUpperCase()}_${index}_${build}".replace("-", "_")
}
