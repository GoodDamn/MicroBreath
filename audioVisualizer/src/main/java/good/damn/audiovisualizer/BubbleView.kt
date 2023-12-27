package good.damn.audiovisualizer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import java.util.*
import kotlin.math.abs

class BubbleView(context: Context)
    : View(context),
      Runnable {

    private val TAG = "BubbleView"

    private val mPaint = Paint()
    private val mRandom = Random()

    private val mBubblesRect = LinkedList<RectF>()

    private val mHandlerMain = Handler(Looper.getMainLooper())
    private val mRunMainInvalidate = Runnable {
        invalidate()
    }

    private var mIsInterrupted = false

    init {
        mPaint.color = 0xffff0000.toInt()
        mPaint.style = Paint.Style.STROKE
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

        val sWidth = k * 0.01f
        mPaint.strokeWidth = sWidth

    }

    override fun onDraw(
        canvas: Canvas?
    ) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        if (mBubblesRect.isEmpty()) {
            return
        }

        if (mBubblesRect[0].bottom - 1 < 0) {
            Log.d(TAG, "onDraw: REMOVE ELEMENT OUT OF VIEW")
            mBubblesRect.remove()
        }

        mBubblesRect.forEach {
            it.top--
            it.bottom--

            canvas.drawArc(
                it,
                0f,
                360f,
                true,
                mPaint
            )
        }
    }

    override fun run() {
        while (!mIsInterrupted) {
            mHandlerMain.post(mRunMainInvalidate)
            Thread.sleep(1)
        }
        mIsInterrupted = false

        Thread.currentThread()
            .interrupt()
    }

    fun interrupt() {
        mIsInterrupted = true
    }

    fun listen() {
        mIsInterrupted = false
        Thread(this)
            .start()
    }

    fun addBubble(
        normRadius: Float
    ) {

        val rad = abs(normRadius)
        if (!isLaidOut || rad < 0.2f) {
            return
        }

        val r = RectF()

        val bound = width * 0.08f * rad
        val hb = bound * 0.5f
        val hw = width * 0.5f
        var offsetX = mRandom.nextInt((width * 0.05f).toInt())

        if (mRandom.nextBoolean()) {
            offsetX = -offsetX
        }

        r.top = height - bound
        r.left = hw - hb + offsetX

        r.bottom = height.toFloat()
        r.right = hw + hb + offsetX

        mBubblesRect.add(r)
    }
}