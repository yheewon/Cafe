package kr.heewon.cafe

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var btn_start : ImageButton? = null


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btn_start = findViewById(R.id.btn_start) as ImageButton

        btn_start!!.setOnClickListener {
            var main_intent = Intent(applicationContext,MainActivity::class.java)
            setResult(Activity.RESULT_OK,main_intent)
            finish()

        }

    }
}