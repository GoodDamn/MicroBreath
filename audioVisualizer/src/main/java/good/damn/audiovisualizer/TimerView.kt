package good.damn.audiovisualizer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

class TimerView(context: Context)
    : View(context),
      Runnable {

    private val TAG = "TimerView"

    private var mOnTickListener: OnTickListener? = null

    private var mHalfWidth = 0f
    private var mHalfHeight = 0f

    private var mTextCanvas: TextCanvas
    private var mTextCanvas2: TextCanvas
    private var mTextCanvasMsg: TextCanvas

    private val mPaintWaves = Paint()

    private val mAnimatorTextTimer = ValueAnimator()
    private val mAnimatorWaves = ValueAnimator()

    private val mCirclesRect = ArrayList<RectF>()
    private val mCircles = ArrayList<Circle>()

    private val mHandler = Handler(Looper.getMainLooper())

    private var mTimeSec = 0

    init {
        val textColor = 0xffaaaaaa.toInt()

        mPaintWaves.color = textColor
        mPaintWaves.style = Paint.Style.STROKE

        mTextCanvas = TextCanvas()
        mTextCanvas2 = TextCanvas()
        mTextCanvasMsg = TextCanvas()

        mTextCanvas.setColor(textColor)
        mTextCanvas2.setColor(textColor)
        mTextCanvasMsg.setColor(textColor)

        mAnimatorWaves.duration = 5250
        mAnimatorWaves.setFloatValues(0.0f, 1.0f)
        mAnimatorWaves.repeatCount = ValueAnimator.INFINITE
        mAnimatorWaves.repeatMode = ValueAnimator.REVERSE
        mAnimatorWaves.addUpdateListener {
            val f = it.animatedValue as Float
            val w = width * 0.5f
            val h = height * 0.5f

            val hw = w / 2
            val hh = h / 2

            val leftLimit = mHalfWidth - hw
            val topLimit = mHalfHeight - hh

            for (i in mCircles.indices) {
                val c = mCircles[i]
                val r = mCirclesRect[i]

                val dcx = (leftLimit - r.left) * f
                val dcy = (topLimit - r.top) * f
                c.left = r.left + dcx
                c.right = r.right - dcx

                c.top = r.top + dcy
                c.bottom = r.bottom - dcy
            }
            invalidate()
        }

        mAnimatorTextTimer.duration = 175
        mAnimatorTextTimer.setFloatValues(0.0f,1.0f)
        mAnimatorTextTimer.addUpdateListener {
            val f = it.animatedValue as Float
            val rf = 1.0f - f

            mTextCanvas.apply {
                setAlpha(rf)
                setY(mHalfHeight - getHeight() * f)
            }

            mTextCanvas2.apply {
                setAlpha(f)
                setY(mHalfHeight + getHeight() * rf)
            }

            //invalidate()
        }

        val wavesCount = 4
        val dAlpha = 1.0f / wavesCount
        var alpha = dAlpha
        for (i in 0 until wavesCount) {
            val c = Circle()
            val r = RectF()
            c.alpha = (alpha * 255).toInt()
            mCirclesRect.add(r)
            mCircles.add(c)
            alpha += dAlpha
        }

        mAnimatorWaves.start()
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

        val textSize = k * 0.13f
        mTextCanvas.setTextSize(textSize)
        mTextCanvas2.setTextSize(textSize)

        mTextCanvasMsg.setTextSize(k * 0.065f)

        mPaintWaves.strokeWidth = s

        val ss = 2 * s
        val w = width - ss
        val h = height - ss

        val o = k * 0.4f / mCircles.size
        val dOff = mCircles.size / o
        var off = 0f

        for (i in mCircles.indices) {
            val it = mCircles[i]
            val hof = w * off
            val vof = h * off

            it.top = s + vof
            it.bottom = height - s - vof

            it.left = s + hof
            it.right = width - s - hof

            mCirclesRect[i].set(it)

            off += dOff
        }

    }

    override fun run() {
        if (mTimeSec < 1) {
            return
        }

        mTextCanvas.setText(mTimeSec.toString())
        mTextCanvas2.setText((mTimeSec - 1).toString())

        mTextCanvas.setX(mHalfWidth - mTextCanvas.getWidthHalf())
        mTextCanvas2.setX(mHalfWidth - mTextCanvas2.getWidthHalf())

        mAnimatorTextTimer.start()

        mHandler.postDelayed(this,1000)
        mTimeSec--
    }

    override fun onDraw(
        canvas: Canvas?
    ) {
        if (canvas == null) {
            return
        }
        super.onDraw(canvas)

        mCircles.forEach {
            mPaintWaves.alpha = it.alpha
            canvas.drawArc(
                it,
                0f,
                360f,
                true,
                mPaintWaves
            )
        }

        mTextCanvas2.draw(canvas)
        mTextCanvas.draw(canvas)

        val textMsg = mOnTickListener?.onTickMessage(mTimeSec) ?: ""

        mTextCanvasMsg.setText(textMsg)
        mTextCanvasMsg.setPosition(
            mHalfWidth - mTextCanvasMsg.getWidthHalf(),
            mHalfHeight + mTextCanvasMsg.getHeight()
        )
        mTextCanvasMsg.draw(canvas)


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

    fun setOnTickListener(
        l: OnTickListener
    ) {
        mOnTickListener = l
    }

    fun startTimer() {
        if (mTimeSec <= 0) {
            mTimeSec = 12
        }
        invalidate()
        mHandler.postDelayed(
            this,
            1000)
    }

    fun pauseTimer() {
        mHandler.removeCallbacks(this)
    }

    fun stopTimer() {
        mHandler.removeCallbacks(this)
        mTimeSec = 0
    }

    class Circle: RectF() {
        var alpha = 255
    }

    interface OnTickListener {
        fun onTickMessage(
            tickTime: Int
        ): String
    }
}