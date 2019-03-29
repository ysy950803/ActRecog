package com.ysy.actrecog

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.recog_btn).setOnClickListener {
            startActivity(Intent(this, RecogActivity::class.java))
        }
        findViewById<View>(R.id.trans_btn).setOnClickListener {
            startActivity(Intent(this, TransActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }
}
