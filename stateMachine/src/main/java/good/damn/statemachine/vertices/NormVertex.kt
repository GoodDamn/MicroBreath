package good.damn.statemachine.vertices

import android.util.DisplayMetrics
import android.util.FloatMath

class NormVertex(
    fromX: Float,
    fromY: Float,
    toX: Float,
    toY: Float,
    dm: DisplayMetrics
) : StateVertex(fromX,fromY, toX, toY) {

    init {
        this.fromX = fromX * dm.widthPixels
        this.fromY = fromY * dm.heightPixels

        this.toX = toX * dm.widthPixels
        this.toY = toY * dm.heightPixels
    }

}