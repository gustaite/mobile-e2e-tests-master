package test.basic.states.interfaces

import commonUtil.reporting.Report

interface TestStateMachine {
    enum class BaseTestStates : FeatureState {
        STATE_OFF
    }

    val currentState: FeatureState
    var stateOffMethod: () -> Unit
    val statesAndMethods: MutableList<Pair<FeatureState, () -> Unit>>

    fun addStateOffMethod(method: () -> Unit): TestStateMachine {
        stateOffMethod = method
        return this
    }

    fun addStateAndMethods(state: FeatureState, methods: () -> Unit): TestStateMachine {
        statesAndMethods.add(Pair(state, methods))
        return this
    }

    fun run() {
        Report.addMessage("State machine running in $currentState state")
        when (currentState) {
            BaseTestStates.STATE_OFF -> stateOffMethod()
            else -> statesAndMethods.forEach {
                val featureState = it.first
                val methods = it.second
                if (featureState == currentState) methods()
            }
        }
    }
}

interface FeatureState
