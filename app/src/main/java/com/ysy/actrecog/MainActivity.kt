package com.ysy.actrecog

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ysy.actrecog.ka.c.LocalService
import com.ysy.actrecog.ka.s.Main2Activity

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

        keepAlive()
        startActivity(Intent(this, Main2Activity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun keepAlive() {
        startService(Intent(this, LocalService::class.java))
    }
}
