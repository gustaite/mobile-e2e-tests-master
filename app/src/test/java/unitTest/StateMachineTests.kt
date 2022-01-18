package unitTest

import commonUtil.asserts.VintedAssert
import io.mockk.*
import org.junit.Before
import org.junit.Test
import test.basic.states.interfaces.FeatureState
import test.basic.states.interfaces.StateMachineBuilder

class StateMachineTests {
    private val mockedTestStateMachineBuilder = spyk(TestStateMachineBuilder())
    private val stateMachine get() = mockedTestStateMachineBuilder.build()
    private val mockedObject = mockk<TestObject>()
    @Before
    fun before() {
        every { mockedObject.test() } returns false
    }
    @Test
    fun runStateMachineWithoutCrash_NoDefaultMethod() {
        assertDoesNotThrow {
            stateMachine
                .addStateAndMethods(TestStateMachineBuilder.TestStates.A) { }
                .run()
        }
    }
    @Test
    fun runStateMachineWithoutCrash_NoStateMethod() {
        assertDoesNotThrow {
            stateMachine
                .addStateOffMethod { }
                .run()
        }
    }
    @Test
    fun runStateMachineWithoutCrash_NoDefaultMethodWrongState() {
        assertDoesNotThrow {
            stateMachine
                .addStateAndMethods(TestStateMachineBuilder.TestStates.B) { }
                .run()
        }
    }
    @Test
    fun runStateMachine_WrongStateMethodIsNotRun() {
        stateMachine
            .addStateAndMethods(TestStateMachineBuilder.TestStates.B) { mockedObject.test() }
            .run()
        verify { mockedObject wasNot Called }
    }
    @Test
    fun runStateMachine_DefaultMethodIsNotRun() {
        stateMachine
            .addStateOffMethod { mockedObject.test() }
            .run()
        verify { mockedObject wasNot Called }
    }

    @Test
    fun runStateMachine_StateMachineDoesNotRunAddedMethodsSecondTime() {
        repeat(2) {
            stateMachine
                .addStateAndMethods(TestStateMachineBuilder.TestStates.A) { mockedObject.test() }.run()
        }
        verify(exactly = 2) { mockedObject.test() }
    }
    @Test
    fun runStateMachine_DefaultStateOnlyMethodIsRun() {
        every { mockedTestStateMachineBuilder.abFlagsAndStates } returns listOf(
            Pair(false, TestStateMachineBuilder.TestStates.A),
            Pair(false, TestStateMachineBuilder.TestStates.B)
        )

        stateMachine
            .addStateOffMethod { }
            .addStateAndMethods(TestStateMachineBuilder.TestStates.A) { mockedObject.test() }
            .addStateAndMethods(TestStateMachineBuilder.TestStates.B) { mockedObject.test() }
            .run()

        verify { mockedObject wasNot Called }
    }
}

private fun assertDoesNotThrow(method: () -> Unit) {
    try {
        method()
    } catch (e: Exception) {
        VintedAssert.assertNull(e, "Method should have not thrown an exception. Error message: ${e.message}")
    }
}

class TestStateMachineBuilder : StateMachineBuilder {
    enum class TestStates : FeatureState {
        A,
        B
    }
    override val abFlagsAndStates: List<Pair<Boolean, FeatureState>> = listOf(
        Pair(true, TestStates.A),
        Pair(false, TestStates.B)
    )
}

class TestObject {
    fun test(): Boolean {
        return false
    }
}
