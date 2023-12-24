package good.damn.statemachine

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.View
import good.damn.statemachine.vertices.StateVertex

class VectorView(context: Context)
    : View(context) {

    private val mAnimator = ValueAnimator()

    private var mPaint = Paint()
    private var mVertices: Array<StateVertex>? = null
    private var mFraction = 0.0f

    init {
        mPaint.color = 0xffaaff00.toInt()
        mPaint.strokeWidth = 15f

        mAnimator.addUpdateListener { animator ->
            mFraction = animator.animatedValue as Float
            invalidate()
        }

        mAnimator.duration = 3250
        mAnimator.setFloatValues(0.0f,1.0f)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
    }


    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        super.onDraw(canvas)

        if (mVertices == null) {
            return
        }

        val v = mVertices!!

        for (i in v.indices) {
            val a = v[i]
            val b = if ((i+1) >= v.size)
                        v[0]
                    else v[i+1]

            canvas.drawLine(
                interpX(a),
                interpY(a),
                interpX(b),
                interpY(b),
                mPaint
            )
        }
    }

    fun setStateVertices(
        inp: Array<StateVertex>
    ) {
        mVertices = inp
    }

    fun startAnimation() {
        startAnimation(false)
    }

    fun startAnimation(
        reversed: Boolean
    ) {
        if (reversed) {
            mAnimator.setFloatValues(1.0f,0.0f)
        } else {
            mAnimator.setFloatValues(0.0f,1.0f)
        }
        mAnimator.start()
    }

    private fun interpY(
        a: StateVertex
    ): Float {
        return interp(a.fromY,a.toY)
    }

    private fun interpX(
        a: StateVertex
    ): Float {
        return interp(a.fromX,a.toX)
    }

    private fun interp(
        from: Float,
        to: Float
    ): Float {

        return from + (to - from) * mFraction
    }
}