package good.damn.audiovisualizer

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import java.util.*
import android.os.Handler
import android.os.Looper

class TimerView(context: Context)
    : View(context) {

    private val TAG = "TimerView"

    private var mHalfWidth = 0f
    private var mHalfHeight = 0f

    private val mPaint = Paint()
    private val mPaintText = Paint()

    private val mRectCircle = RectF()
    private val mRectCircleTemp = RectF()
    private val mRectText = Rect()

    private val mHandler = Handler(Looper.getMainLooper())
    private val mRunInvalidate = Runnable {
        mTimeSec--
        invalidate()
    }

    private var mTimeSec = 0

    init {
        mPaint.color = 0xffaaaaaa.toInt()
        mPaint.style = Paint.Style.STROKE

        mPaintText.color = 0xffaaaaaa.toInt()
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)

        mHalfWidth = width * 0.5f
        mHalfHeight = height * 0.5f

        val k = if (width > height) height else width

        val s = k * 0.03f

        mPaint.strokeWidth = s
        mPaintText.textSize = k * 0.2f

        mRectCircle.top = s
        mRectCircle.bottom = height - s

        mRectCircle.left = s
        mRectCircle.right = width - s
    }

    override fun onDraw(
        canvas: Canvas?
    ) {
        if (canvas == null) {
            return
        }
        super.onDraw(canvas)

        val w = mRectCircle.width()
        val h = mRectCircle.height()

        var alpha = 0.1f
        var off = 0.1f
        while (alpha < 1.0f) {
            mPaint.alpha = (255 * alpha).toInt()

            val hof = w * off
            val vof = h * off

            mRectCircleTemp.top = mRectCircle.top + vof
            mRectCircleTemp.bottom = mRectCircle.bottom - vof

            mRectCircleTemp.left = mRectCircle.left + hof
            mRectCircleTemp.right = mRectCircle.right - hof

            canvas.drawArc(
                mRectCircleTemp,
                0f,
                360f,
                true,
                mPaint
            )

            off += 0.1f
            alpha += 0.3f
        }

        val s = mTimeSec.toString()

        mPaintText.getTextBounds(
            s,
            0,
            s.length,
            mRectText)

        val textX = mHalfWidth - (mRectText.right + mRectText.left) * 0.5f
        val textY = mHalfHeight + mRectText.height() * 0.5f

        canvas.drawText(
            s,
            textX,
            textY,
            mPaintText
        )

        if (mTimeSec <= 0) {
            return
        }

        mHandler.postDelayed(
            mRunInvalidate,
            1000)

        /*canvas.drawLine(
            0f,
            height * 0.5f,
            width.toFloat(),
            height * 0.5f,
            mPaint
        )

        canvas.drawLine(
            width * 0.5f,
            0f,
            width * 0.5f,
            height.toFloat(),
            mPaint
        )*/
    }

    fun startTimer() {
        if (mTimeSec <= 0) {
            mTimeSec = 12
        }
        invalidate()
    }

    fun pauseTimer() {
        mHandler.removeCallbacks(mRunInvalidate)
    }

    fun stopTimer() {
        mHandler.removeCallbacks(mRunInvalidate)
        mTimeSec = 0
    }
}