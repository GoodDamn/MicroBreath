package good.damn.statemachine

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.View

class VectorView(context: Context)
    : View(context) {

    private val mAnimator = ValueAnimator()

    private var mPaint = Paint()
    private var mVectorImages: Array<VectorImage>? = null

    private var mFromIndex = 0
    private var mToIndex = 0

    init {
        mPaint.color = 0xffff0000.toInt()

        mAnimator.addUpdateListener {
            invalidate()
        }

        mAnimator.duration = 250
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

        val k = if (width > height) height else width

        mPaint.strokeWidth = k * 0.03f
    }


    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        super.onDraw(canvas)

        if (mVectorImages == null) {
            return
        }

        val v = mVectorImages!!

        val ff = v[mFromIndex]
            .vertices

        val tt = v[mToIndex]
            .vertices

        for (i in ff.indices) {
            val f = ff[i]
            val t = tt[i]

            val j = if (i+1>=ff.size) 0 else i + 1

            val f2 = ff[j]
            val t2 = tt[j]

            canvas.drawLine(
                interpX(f,t),
                interpY(f,t),
                interpX(f2,t2),
                interpY(f2,t2),
                mPaint
            )
        }

    }

    fun setTransitionIndex(
        from: Int,
        to: Int
    ) {
        mFromIndex = from
        mToIndex = to
    }

    fun setVectorImages(
        inp: Array<VectorImage>
    ) {
        mVectorImages = inp
    }

    fun startAnimation() {
        mAnimator.setFloatValues(0.0f,1.0f)
        mAnimator.start()
    }

    private fun interpY(
        a: PointF,
        b: PointF
    ): Float {
        return interp(a.y,b.y)
    }

    private fun interpX(
        a: PointF,
        b: PointF
    ): Float {
        return interp(a.x,b.x)
    }

    private fun interp(
        from: Float,
        to: Float
    ): Float {

        return from + (to - from) * (mAnimator.animatedValue as Float)
    }
}