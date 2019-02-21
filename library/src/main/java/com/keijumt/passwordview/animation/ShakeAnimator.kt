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
import android.view.View
import kotlin.math.sin

internal class ShakeAnimator(
    private val target: View
) : Animator(target) {

    private var animator = ValueAnimator.ofFloat(0f, 1f)
    var duration: Long = 400
    var startDelay: Long = 0
    var shakeMaxWidth: Int = 40
    var shakeTimes: Int = 4

    override fun start() {
        animator.run {
            cancel()
            duration = this@ShakeAnimator.duration
            startDelay = this@ShakeAnimator.startDelay

            val initialPositionX = target.x
            addUpdateListener {
                val progress = it.animatedValue as Float
                target.run {
                    x = initialPositionX + sin(shakeTimes * Math.PI * progress).toFloat() * shakeMaxWidth / 2
                }
            }
            start()
        }
    }

    override fun addListener(listener: AnimatorListener) {
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator?) {
                listener.onAnimationEnd()
            }
        })
    }

    override fun removeAllListener() {
        animator.removeAllListeners()
    }
}