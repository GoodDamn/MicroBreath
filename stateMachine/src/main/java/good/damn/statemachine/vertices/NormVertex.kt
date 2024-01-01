package good.damn.statemachine.vertices

import android.util.DisplayMetrics
import android.util.FloatMath
import android.util.Size

class NormVertex(
    fromX: Float,
    fromY: Float,
    toX: Float,
    toY: Float,
    dm: Size
) : StateVertex(fromX,fromY, toX, toY) {

    init {

        this.fromX = fromX * dm.width
        this.fromY = fromY * dm.height

        this.toX = toX * dm.width
        this.toY = toY * dm.height
    }

}