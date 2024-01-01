package good.damn.audiovisualizer.canvas

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class TextCanvas {

    private val mPaint = Paint()

    var mHalfWidth = 0f
        private set

    var mHalfHeight = 0f
        private set

    var mWidth = 0f
        private set(value) {
            field = value
            mHalfWidth = value / 2
        }

    var mHeight = 0f
        private set(value) {
            field = value
            mHalfHeight = value / 2
        }

    var mx = 0f
    var my = 0f

    private var mRect = Rect()

    private var mText = ""

    constructor()

    constructor(
        paint: Paint
    ) {
        mPaint.color = paint.color
        mPaint.textSize = paint.textSize
        mPaint.alpha = paint.alpha
    }

    fun setText(
        text: String
    ) {
        mText = text
        mPaint.getTextBounds(
            text,
            0,
            text.length,
            mRect)

        mWidth = (mRect.left + mRect.right).toFloat()
        mHeight = mRect.height().toFloat()

        mHalfWidth = mWidth * 0.5f
        mHalfHeight = mHeight * 0.5f
    }

    fun setPosition(
        x: Float,
        y: Float
    ) {
        mx = x
        my = y
    }

    fun setColor(
        color: Int
    ) {
        mPaint.color = color
    }

    fun setAlpha(
        alpha: Float
    ) {
        mPaint.alpha = (alpha * 255).toInt()
    }

    fun setAlpha(
        alpha: Int
    ) {
        mPaint.alpha = alpha
    }

    fun setTextSize(
        size: Float
    ) {
        mPaint.textSize = size
    }


    fun draw(
        canvas: Canvas
    ) {
        canvas.drawText(
            mText,
            mx - mHalfWidth,
            my,
            mPaint
        )
    }

}