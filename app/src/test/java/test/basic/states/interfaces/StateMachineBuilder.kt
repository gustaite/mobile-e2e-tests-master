package test.basic.states.interfaces

/*
How to use test state machine. Let's say one has test that is impacted by 2 feature flags A and B.
Thus test might be in 4 different states: default (A and B is off), A is on, B is on, A and B is on.

Then create state machine builder for this test:

class ABTestStateMachineBuilder : StateMachineBuilder {
    enum class TestStates : FeatureState {
        A,
        B,
        AB
    }
    override val abFlagsAndStates: List<Pair<Boolean, FeatureState>> = listOf(
        Pair(AbTestController.isA() && !AbTestController.isB(), TestStates.A)
        Pair(AbTestController.isB() && !AbTestController.isA(), TestStates.B)
        Pair(AbTestController.isA() && AbTestController.isB(), TestStates.AB)
    )
}

Then test might look something like this:

val testStateMachine = ABTestStateMachineBuilder().build()
@Test(description = "Test description")
fun testForTestingPurposes() {
    runThisMethodToSetUpTest()
    testStateMachine
        .addStateOffMethod { defaultTestMethod() }
        .addStateAndMethods(ABTestStateMachine.TestStates.A) { testMethodA() }
        .addStateAndMethods(ABTestStateMachine.TestStates.B) { testMethodB() }
        .addStateAndMethods(ABTestStateMachine.TestStates.AB) { testMethodAB() }
        .run()
}
 */

interface StateMachineBuilder {
    val abFlagsAndStates: List<Pair<Boolean, FeatureState>>
    fun build(): TestStateMachine {
        return object : TestStateMachine {
            override val currentState: FeatureState = abFlagsAndStates.find { it.first }?.second ?: TestStateMachine.BaseTestStates.STATE_OFF
            override var stateOffMethod: () -> Unit = {}
            override val statesAndMethods: MutableList<Pair<FeatureState, () -> Unit>> = mutableListOf()
        }
    }
}
