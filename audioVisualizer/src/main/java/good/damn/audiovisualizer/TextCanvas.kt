package good.damn.audiovisualizer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class TextCanvas {

    private val mPaint = Paint()

    private var mHalfWidth = 0f
    private var mHalfHeight = 0f

    private var mWidth = 0f
    private var mHeight = 0f

    private var mRect = Rect()

    private var mx = 0f
    private var my = 0f

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

    fun setX(
        x: Float
    ) { mx = x}

    fun setY(
        y: Float
    ) { my = y }

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

    fun getWidthHalf(): Float {
        return mHalfWidth
    }

    fun getHeightHalf(): Float {
        return mHalfHeight
    }

    fun getWidth(): Float {
        return mWidth
    }

    fun getHeight(): Float {
        return mHeight
    }

    fun draw(
        canvas: Canvas
    ) {
        canvas.drawText(
            mText,
            mx,
            my,
            mPaint
        )
    }

}