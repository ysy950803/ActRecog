package com.ysy.actrecog

import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Process
import android.util.Log
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
            keepAlive()
//            startActivity(Intent(this, TransActivity::class.java))
        }

//        keepAlive()
//        startActivity(Intent(this, Main2Activity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        })
    }

    private fun keepAlive() {
//        startService(Intent(this, LocalService::class.java))
//        try {
//            val AppOpsUtils = Class.forName("android.miui.AppOpsUtils")
//            val setApplicationAutoStart = AppOpsUtils.getDeclaredMethod(
//                "setApplicationAutoStart",
//                Context::class.java, String::class.java, Boolean::class.java
//            )
//            setApplicationAutoStart.isAccessible = true
//            setApplicationAutoStart.invoke(null, applicationContext, packageName, true)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("TEST-1", e.toString())
//        }
        Log.d("TEST-1", "${Binder.getCallingPid()} ${Process.myPid()}")
        try {
            val bundle = Bundle().apply {
                putLong("extra_permission", 16384)
                putInt("extra_action", 3)
                putStringArray("extra_package", arrayOf(packageName))
            }
            contentResolver.call(Uri.parse("content://com.lbe.security.miui.permmgr"), "6", null, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TEST-1", e.toString())
        }
    }
}
