package com.ysy.actrecog.ka.s

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ysy.actrecog.R

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        keepAlive()
    }

    private fun keepAlive() {
        startService(Intent(this, RemoteService::class.java))
    }
}
