package good.damn.statemachine

class AnimState() {

    fun start(
        withEnd: Runnable
    ) {
        withEnd.run()
    }
}