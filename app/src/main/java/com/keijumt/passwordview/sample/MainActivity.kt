package com.keijumt.passwordview.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.keijumt.passwordview.ActionListener
import kotlinx.android.synthetic.main.activity_main.password_view
import kotlinx.android.synthetic.main.activity_main.text_0
import kotlinx.android.synthetic.main.activity_main.text_1
import kotlinx.android.synthetic.main.activity_main.text_2
import kotlinx.android.synthetic.main.activity_main.text_3
import kotlinx.android.synthetic.main.activity_main.text_4
import kotlinx.android.synthetic.main.activity_main.text_5
import kotlinx.android.synthetic.main.activity_main.text_6
import kotlinx.android.synthetic.main.activity_main.text_7
import kotlinx.android.synthetic.main.activity_main.text_8
import kotlinx.android.synthetic.main.activity_main.text_9
import kotlinx.android.synthetic.main.activity_main.text_d

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CORRECT_PASSWORD = "1234"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        password_view.setListener(object : ActionListener {
            override fun onCompleteInput(inputText: String) {
                if (CORRECT_PASSWORD == inputText) {
                    password_view.correctAnimation()
                } else {
                    password_view.incorrectAnimation()
                }
            }

            override fun onEndJudgeAnimation() {
                password_view.reset()
            }
        })

        text_0.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_1.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_2.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_3.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_4.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_5.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_6.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_7.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_8.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_9.setOnClickListener { password_view.appendInputText((it as TextView).text.toString()) }
        text_d.setOnClickListener { password_view.removeInputText() }
    }
}
