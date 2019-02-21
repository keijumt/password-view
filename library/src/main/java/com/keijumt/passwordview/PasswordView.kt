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

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.keijumt.passwordview.animation.Animator
import com.keijumt.passwordview.animation.FillAndStrokeColorChangeAnimation
import com.keijumt.passwordview.animation.FillColorChangeAnimation
import com.keijumt.passwordview.animation.ShakeAnimator
import com.keijumt.passwordview.animation.SpringAnimator

class PasswordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_CIRCLE_COUNT = 4
        private const val DEFAULT_RADIUS = 20f
        private const val DEFAULT_BETWEEN_MARGIN = 72
        private const val DEFAULT_INPUT_COLOR = Color.BLACK
        private const val DEFAULT_NOT_INPUT_COLOR = Color.WHITE
        private const val DEFAULT_OUTLINE_COLOR = Color.GRAY
        private const val DEFAULT_CORRECT_COLOR = Color.GREEN
        private const val DEFAULT_INCORRECT_COLOR = Color.RED
        private const val DEFAULT_CORRECT_ANIMATION_DURATION = 150
        private const val DEFAULT_INCORRECT_ANIMATION_DURATION = 400
        private const val DEFAULT_COLOR_CHANGE_ANIMATION_DURATION = 200
        private const val DEFAULT_INPUT_AND_REMOVE_ANIMATION_DURATION = 200
        private const val DEFAULT_CORRECT_TOP = 40f
        private const val DEFAULT_CORRECT_BOTTOM = 15f
        private const val DEFAULT_INCORRECT_MAX_WIDTH = 40f
        private const val DEFAULT_OUTLINE_STROKE_WIDTH = 4f
    }

    private val array = context.obtainStyledAttributes(attrs, R.styleable.PasswordView)
    var passwordCount = array.getInteger(R.styleable.PasswordView_password_count, DEFAULT_CIRCLE_COUNT)
        set(value) {
            field = value
            addCircleView(passwordCount)
        }
    private val radius = array.getDimension(R.styleable.PasswordView_password_radius, DEFAULT_RADIUS)
    private val betweenMargin =
        array.getDimensionPixelOffset(R.styleable.PasswordView_password_between_margin, DEFAULT_BETWEEN_MARGIN)
    private val inputColor = array.getColor(R.styleable.PasswordView_password_input_color, DEFAULT_INPUT_COLOR)
    private val notInputColor = array.getColor(R.styleable.PasswordView_password_input_color, DEFAULT_NOT_INPUT_COLOR)
    private val outlineColor = array.getColor(R.styleable.PasswordView_password_outline_color, DEFAULT_OUTLINE_COLOR)
    private val correctColor = array.getColor(R.styleable.PasswordView_password_correct_color, DEFAULT_CORRECT_COLOR)
    private val incorrectColor =
        array.getColor(R.styleable.PasswordView_password_incorrect_color, DEFAULT_INCORRECT_COLOR)
    private val correctAnimationDuration =
        array.getInteger(R.styleable.PasswordView_password_correct_duration, DEFAULT_CORRECT_ANIMATION_DURATION)
            .toLong()
    private val incorrectAnimationDuration =
        array.getInteger(R.styleable.PasswordView_password_correct_duration, DEFAULT_INCORRECT_ANIMATION_DURATION)
            .toLong()
    private val colorChangeAnimationDuration =
        array.getInteger(
            R.styleable.PasswordView_password_color_change_duration,
            DEFAULT_COLOR_CHANGE_ANIMATION_DURATION
        )
            .toLong()
    private val inputAndRemoveAnimationDuration =
        array.getInteger(
            R.styleable.PasswordView_password_input_and_remove_duration,
            DEFAULT_INPUT_AND_REMOVE_ANIMATION_DURATION
        ).toLong()
    private val correctTop = array.getDimension(R.styleable.PasswordView_password_correct_top, DEFAULT_CORRECT_TOP)
    private val correctBottom =
        array.getDimension(R.styleable.PasswordView_password_correct_bottom, DEFAULT_CORRECT_BOTTOM)
    private val incorrectMaxWidth =
        array.getDimension(R.styleable.PasswordView_password_incorrect_max_width, DEFAULT_INCORRECT_MAX_WIDTH)
    private val outlineStrokeWidth = array.getDimension(
        R.styleable.PasswordView_password_outline_stroke_width,
        DEFAULT_OUTLINE_STROKE_WIDTH
    )

    private val circleViews = mutableListOf<CircleView>()

    private var input: String = ""
        set(value) {
            val oldInput = field
            field = value
            if (oldInput.length != value.length || value.length <= circleViews.size) {
                handleInputAnimate(oldInput, value)
            }
        }

    private var actionListener: ActionListener? = null

    init {
        array.recycle()

        orientation = LinearLayout.HORIZONTAL
        addCircleView(passwordCount)
    }

    private fun incorrectAnimation(duration: Long) {
        circleViews.forEachIndexed { index, circleView ->
            ShakeAnimator(circleView).apply {
                this.duration = duration
                this.shakeMaxWidth = incorrectMaxWidth.toInt()
                startDelay = (index * 40).toLong()

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd() {
                        if (index == passwordCount - 1) {
                            this@PasswordView.actionListener?.onEndJudgeAnimation()
                        }
                    }
                })

                start()
            }
        }
    }

    private fun correctAnimation(duration: Long) {
        circleViews.forEachIndexed { index, circleView ->
            SpringAnimator(circleView).run {
                this.duration = duration
                startDelay = (index * 40).toLong()
                moveTopY = correctTop
                moveBottomY = correctBottom


                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd() {
                        if (index == passwordCount - 1) {
                            this@PasswordView.actionListener?.onEndJudgeAnimation()
                        }
                    }
                })

                start()
            }
        }
    }

    private fun addCircleView(circleCount: Int) {

        removeAllViews()

        for (i in 0 until circleCount) {
            val circleView = CircleView(context).apply {
                setOutLineColor(outlineColor)
                setOutlineStrokeWidth(outlineStrokeWidth)
                setFillCircleColor(notInputColor)
                setFillAndStrokeCircleColor(inputColor)
                setInputAndRemoveAnimationDuration(inputAndRemoveAnimationDuration)
                setRadius(radius)
                layoutParams = calcMargin(i, circleCount)
            }
            this.addView(circleView)
            circleViews.add(circleView)
        }
    }

    private fun calcMargin(circleIndex: Int, circleCount: Int): ViewGroup.LayoutParams? {
        if (circleCount < 0) {
            throw IllegalArgumentException("passwordCount:$circleCount must be greater than or equal to 0")
        }

        if (circleIndex < 0) {
            throw IllegalArgumentException("circleIndex:$circleIndex must be greater than or equal to 0")
        }

        if (circleCount == 0) {
            return null
        }

        val halfMargin = betweenMargin / 2
        val halfIncorrectMaxWidth = (incorrectMaxWidth / 2).toInt()
        val layoutParams = LinearLayout.LayoutParams(width, height).apply {
            topMargin = correctTop.toInt()
            bottomMargin = correctBottom.toInt()
        }
        if (circleCount == 1) {
            return layoutParams.apply {
                leftMargin = halfIncorrectMaxWidth
                rightMargin = halfIncorrectMaxWidth
            }
        }

        if (circleCount == 2) {
            return when (circleIndex) {
                0 -> layoutParams.apply {
                    leftMargin = halfIncorrectMaxWidth
                    rightMargin = halfMargin
                }
                1 -> layoutParams.apply {
                    leftMargin = halfMargin
                    rightMargin = halfIncorrectMaxWidth
                }
                else -> throw IllegalArgumentException("circleIndex:$circleIndex must be greater than or equal to 0")
            }
        }

        return when (circleIndex) {
            0 -> layoutParams.apply {
                leftMargin = halfIncorrectMaxWidth
                rightMargin = halfMargin
            }
            in 1 until circleCount - 1 -> layoutParams.apply {
                leftMargin = halfMargin
                rightMargin = halfMargin
            }
            circleCount - 1 -> layoutParams.apply {
                leftMargin = halfMargin
                rightMargin = halfIncorrectMaxWidth
            }
            else -> throw IllegalArgumentException("circleIndex:$circleIndex must be greater than or equal to 0")
        }
    }

    private fun handleInputAnimate(oldInput: String, newInput: String) {

        // increase input text
        if (newInput.length > oldInput.length) {
            for (i in oldInput.length until newInput.length) {
                circleViews[i].animateAndInvoke {
                    if (newInput.length == circleViews.size) {
                        actionListener?.onCompleteInput(input)
                    }
                }
            }
        } else {
            for (i in newInput.length until oldInput.length) {
                circleViews[i].animateAndInvoke()
            }
        }
    }

    /**
     * run correct animation
     */
    fun correctAnimation() {
        correctAnimation(correctAnimationDuration)
        fillAndStrokeColorChangeAnimation(colorChangeAnimationDuration, correctColor)
    }

    /**
     * run incorrect animation
     */
    fun incorrectAnimation() {
        incorrectAnimation(incorrectAnimationDuration)
        fillAndStrokeColorChangeAnimation(colorChangeAnimationDuration, incorrectColor)
    }

    /**
     * Empty the value of input and run reset animation
     */
    fun reset() {
        input = ""
        fillColorChangeAnimation(colorChangeAnimationDuration, notInputColor)
        fillAndStrokeColorChangeAnimation(colorChangeAnimationDuration, inputColor)
    }

    fun setListener(actionListener: ActionListener) {
        this.actionListener = actionListener
    }


    fun removeListener() {
        this.actionListener = null
    }

    /**
     * append the value of input and run input animation
     */
    fun appendInputText(text: String) {
        if (text.length + input.length > passwordCount) {
            return
        }

        repeat(text.length) {
            if (circleViews[input.length + it].isAnimating()) {
                return
            }
        }

        input += text
    }

    /**
     * remove last characters from input values and run not input animation
     */
    fun removeInputText() {
        if (input.isEmpty()) {
            return
        }

        if (circleViews[input.length - 1].isAnimating()) {
            return
        }

        input = input.dropLast(1)
    }

    private fun fillAndStrokeColorChangeAnimation(duration: Long, color: Int) {
        circleViews.forEach { circleView ->
            FillAndStrokeColorChangeAnimation(circleView).run {
                this.duration = duration
                toColor = color
                start()
            }
        }
    }

    private fun fillColorChangeAnimation(duration: Long, color: Int) {
        circleViews.forEach { circleView ->
            FillColorChangeAnimation(circleView).run {
                this.duration = duration
                toColor = color
                start()
            }
        }
    }
}