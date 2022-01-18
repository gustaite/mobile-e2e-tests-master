@GrabResolver(name = 'jenkins-ci-org-releases', root = 'http://repo.jenkins-ci.org/releases/')
@Grab(group = 'org.jenkins-ci.main', module = 'jenkins-core', version = '2.245')

import groovy.xml.MarkupBuilder
import hudson.model.*
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
