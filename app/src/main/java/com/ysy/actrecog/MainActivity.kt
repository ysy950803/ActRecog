package com.ysy.actrecog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.recog_btn).setOnClickListener {
//            startActivity(Intent(this, RecogActivity::class.java))
            ChreApi.sendMessageToNanoApp()
        }
        findViewById<View>(R.id.trans_btn).setOnClickListener {
//            startActivity(Intent(this, TransActivity::class.java))
            ChreApi.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }
}
