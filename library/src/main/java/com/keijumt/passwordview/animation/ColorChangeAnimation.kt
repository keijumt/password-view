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

package com.keijumt.passwordview.animation

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import com.keijumt.passwordview.CircleView

internal abstract class ColorChangeAnimation(
    target: CircleView
) : Animator(target) {

    private var animator: ValueAnimator? = null
    var toColor: Int = 0
    var duration: Long = 200
    var startDelay: Long = 100

    override fun start() {
        animator = ValueAnimator.ofArgb(getColor(), toColor).apply {
            duration = this@ColorChangeAnimation.duration
            startDelay = this@ColorChangeAnimation.startDelay
            addUpdateListener {
                setColor(it.animatedValue as Int)
            }
        }
        animator?.start()
    }

    override fun addListener(listener: AnimatorListener) {
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator?) {
                listener.onAnimationEnd()
            }
        })
    }

    protected abstract fun getColor(): Int
    abstract fun setColor(color: Int)
}