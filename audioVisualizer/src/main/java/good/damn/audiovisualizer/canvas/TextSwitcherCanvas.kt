package good.damn.audiovisualizer.canvas

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.util.Log
import androidx.core.animation.addListener
import java.lang.Float.max
import java.lang.Float.min

class TextSwitcherCanvas {

    private val TAG = "TextSwitcherCanvas"

    private val mTextCanvas = TextCanvas()
    private val mTextCanvas2 = TextCanvas()

    private val mAnimator = ValueAnimator()

    var mx = 0f
        set(value) {
            field = value
            mTextCanvas.mx = value
            mTextCanvas2.mx = value
        }

    var my = 0f
        set(value) {
            field = value
            mTextCanvas.my = value
            mTextCanvas2.my = value
        }

    var minWidth = 0f
        private set(value) {
            field = value
        }

    var minHeight = 0f
        private set(value) {
            field = value
        }

    var mWidth = 0f
        private set(value) {
            field = value
        }

    var mHeight = 0f
        private set(value) {
            field = value
        }

    var mText = ""
        private set(value) {
            field = value
        }

    init {
        mAnimator.duration = 250
        mAnimator.setFloatValues(0f,1f)
        mAnimator.addListener({}) {
            // onAnimationEnd:
            Log.d(TAG, "onAnimationEnd: ")
        }

        mAnimator.addUpdateListener {
            val f = it.animatedValue as Float
            val rf = 1.0f - f

            mTextCanvas.apply {
                setAlpha(rf)
                my = this@TextSwitcherCanvas.my - mHeight * f
            }

            mTextCanvas2.apply {
                setAlpha(f)
                my = this@TextSwitcherCanvas.my + mHeight * rf
            }
        }
    }

    fun setTextSize(
        t1: Float,
        t2: Float
    ) {
        mTextCanvas.setTextSize(t1)
        mTextCanvas2.setTextSize(t2)
    }

    fun setColor(
        c1: Int,
        c2: Int
    ) {
        mTextCanvas.setColor(c1)
        mTextCanvas2.setColor(c2)
    }

    fun setText(
        prev: String,
        next: String
    ) {
        mTextCanvas.setText(prev)
        mTextCanvas2.setText(next)

        mText = next

        minWidth = min(
            mTextCanvas.mWidth,
            mTextCanvas2.mWidth
        )

        minHeight = min(
            mTextCanvas.mHeight,
            mTextCanvas2.mHeight
        )

        mWidth = max(
            mTextCanvas.mWidth,
            mTextCanvas2.mWidth
        )

        mHeight = max(
            mTextCanvas.mHeight,
            mTextCanvas2.mHeight
        )
    }

    fun switch() {
        mAnimator.start()
    }

    fun draw(
        canvas: Canvas
    ) {
        mTextCanvas.draw(canvas)
        mTextCanvas2.draw(canvas)
    }
}