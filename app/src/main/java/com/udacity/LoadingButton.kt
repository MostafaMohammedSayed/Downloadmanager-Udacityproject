package com.udacity

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentPercentage = 0
    private var currentWidth = 0
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
    }

    init {
        isClickable = true
    }

    val backgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
    val circleColor = ContextCompat.getColor(context, R.color.colorAccent)

    val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        color = circleColor
    }

    val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        color = Color.YELLOW
    }

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
        textAlign = Paint.Align.RIGHT
        textSize = 55.0f
    }

    val ovalSpace = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(backgroundColor)
        if (buttonState == ButtonState.Clicked) {
            val percentageOfWidth: Float = getPercentageOfWidth()
            rectPaint.strokeWidth = height.toFloat()
            canvas.drawRect(0f, 0f, percentageOfWidth, height.toFloat(), rectPaint)
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("We are loading", width / 2f, height / 1.8f, textPaint)
            setSpace()
            val percentageToFill: Float = getPercentageToFill()
            canvas.drawArc(ovalSpace, 0f, percentageToFill, false, arcPaint)
        } else {
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("Download", width / 2f, height / 1.8f,textPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setSpace() {
        val ovalSize = 35
        ovalSpace.set(
            (width / 1.9).toFloat(),
            height / 2.8f,
            ((width / 1.9) + ovalSize).toFloat(),
            height / 2.8f + ovalSize
        )
    }

    fun uponViewClicked() {
        buttonState = ButtonState.Clicked
        invalidate()
    }


    private fun getPercentageToFill() = (360 * (currentPercentage / 100.0)).toFloat()

    private fun getPercentageOfWidth(): Float = (width * currentWidth/100).toFloat()

    fun animateProgress() {
        val valueHolder = PropertyValuesHolder.ofFloat("percentage", 0f, 100f)

        val animator = ValueAnimator().apply {
            setValues(valueHolder)
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                val percentage = it.getAnimatedValue("percentage") as Float
                currentPercentage = percentage.toInt()
                invalidate()
            }
        }
        animator.start()

        val backgroundValueHolder = PropertyValuesHolder.ofFloat("widthPercentage", 0f, 100f)

        val secondAnimator = ValueAnimator().apply {
            setValues(backgroundValueHolder)
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                val percentage = it.getAnimatedValue("widthPercentage") as Float
                currentWidth = percentage.toInt()
                invalidate()
            }
        }
        secondAnimator.start()
    }

    fun uponDownloadComplete(){
        buttonState = ButtonState.Completed
    }

}