package good.damn.statemachine

import java.util.LinkedList

class AnimStateMachine {

    private val mStates = LinkedList<AnimState>()

    fun addAnimation(

    ) {

    }

    fun addState(
        state: AnimState
    ) {
        mStates.add(state)
    }

    fun start() {
        val iterator = mStates.iterator()
        startState(iterator)
    }

    private fun startState(
        iter: MutableIterator<AnimState>
    ) {
        if (!iter.hasNext()) {
            return
        }
        val state = iter.next()
        state.start {
            startState(iter)
        }
    }
}