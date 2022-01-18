package util.reporting

import org.testng.ITestNGMethod
import org.testng.xml.Parser
import org.testng.xml.XmlClass
import java.io.File

object ResponsibleTeam {
    private val teamTestList: HashMap<Team, MutableList<XmlClass>> = getTestsForTeamsList()

    private fun getTestsForTeamsList(): HashMap<Team, MutableList<XmlClass>> {
        val map = hashMapOf<Team, MutableList<XmlClass>>()
        for (team in Team.values()) {
            val file = File("${System.getProperty("user.dir")}/${team.name.lowercase()}.xml")
            if (file.exists()) {
                val suite = Parser(file.absolutePath).parse().first().tests.first()
                val tests = suite.classes
                map[team] = tests
            }
        }
        return map
    }

    fun getTeam(test: ITestNGMethod): Team {
        teamTestList.forEach {
            it.value.forEach { c ->
                if (c.name == test.testClass.name) {
                    return when {
                        c.includedMethods.size > 0 -> {
                            when {
                                c.includedMethods.find { t -> t.name == test.methodName } != null -> it.key
                                else -> Team.TEAM_UNASSIGNED
                            }
                        }
                        else -> it.key
                    }
                }
            }
        }
        return Team.TEAM_UNASSIGNED
    }
}

enum class Team {
    TEAM_UNASSIGNED,
    AUTOBAHN,
    FIND,
    NOMADS,
    SHIPPING,
    KARMA,
    MARIO,
    COZY,
    A_TEAM
}
