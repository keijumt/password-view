/*
 * Copyright 2019 Keiju Matsumoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.keijumt.passwordview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutLinearInInterpolator

internal class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val outLinePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val fillCirclePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val fillAndStrokeCirclePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
    }

    private var radius = 16f

    private var animator: ValueAnimator? = null

    private var inputAndRemoveAnimationDuration = 200L

    private var progress = 0.0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ((radius * 2) + (outLinePaint.strokeWidth)).toInt()
        val height = ((radius * 2) + (outLinePaint.strokeWidth)).toInt()
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        val halfOutLineStrokeWidth = outLinePaint.strokeWidth / 2

        // fill circle
        canvas.drawCircle(
            radius + halfOutLineStrokeWidth,
            radius + halfOutLineStrokeWidth,
            lerp(radius - halfOutLineStrokeWidth, 0f, progress),
            fillCirclePaint
        )

        // outline circle
        canvas.drawCircle(
            radius + halfOutLineStrokeWidth,
            radius + halfOutLineStrokeWidth,
            lerp(radius, 0f, progress),
            outLinePaint
        )

        // fill and stroke circle
        canvas.drawCircle(
            radius + halfOutLineStrokeWidth,
            radius + halfOutLineStrokeWidth,
            lerp(0f, radius + halfOutLineStrokeWidth, progress),
            fillAndStrokeCirclePaint
        )
    }

    fun animateAndInvoke(onEnd: (() -> Unit)? = null) {
        if (animator != null) {
            return
        }

        val newProgress = if (progress == 0f) 1f else 0f
        animator = ValueAnimator.ofFloat(progress, newProgress).apply {
            duration = inputAndRemoveAnimationDuration
            addUpdateListener {
                progress = it.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    animator = null
                    onEnd?.invoke()
                }
            })
            interpolator = FastOutLinearInInterpolator()
        }
        animator?.start()
    }

    fun setRadius(radius: Float) {
        this.radius = radius
        invalidate()
    }

    fun setFillCircleColor(color: Int) {
        fillCirclePaint.color = color
        postInvalidateOnAnimation()
    }

    fun setOutLineColor(color: Int) {
        outLinePaint.color = color
        postInvalidateOnAnimation()
    }

    fun setFillAndStrokeCircleColor(color: Int) {
        fillAndStrokeCirclePaint.color = color
        postInvalidateOnAnimation()
    }

    fun setOutlineStrokeWidth(strokeWidth: Float) {
        outLinePaint.strokeWidth = strokeWidth
    }

    fun isAnimating(): Boolean = animator != null

    fun getFillAndStrokeCircleColor(): Int = fillAndStrokeCirclePaint.color

    fun getFillCircleColor(): Int = fillCirclePaint.color

    fun getOutLineColor(): Int = outLinePaint.color

    fun setInputAndRemoveAnimationDuration(duration: Long) {
        inputAndRemoveAnimationDuration = duration
    }

    /*
     * Linearly interpolate between two values.
     */
    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t
    }
}