package com.ysy.actrecog.ka.s

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity

class TransferActivity : Activity() {

    companion object {
        const val ACTION_FROM_SELF = "com.ysy.actrecog.ka.s.TransferActivity.FROM_SELF"
        const val ACTION_FROM_OTHER = "com.ysy.actrecog.ka.s.TransferActivity.FROM_OTHER"
        private const val TAG = "TEST-1-S-TransferActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 创建中转 Activity")
        window.apply {
            setGravity(Gravity.START or Gravity.TOP)
            window.attributes.apply {
                height = 1
                width = height
                y = 0
                x = y
            }
        }
        if (intent != null) {
            if (ACTION_FROM_OTHER == intent.action) {
                startService(Intent(this, RemoteService::class.java))
            } else if (ACTION_FROM_SELF == intent.action) {
                startActivity(Intent("com.ysy.actrecog.ka.c.TransferActivity.FROM_OTHER").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    component = ComponentName("com.ysy.actrecog", "com.ysy.actrecog.ka.c.TransferActivity")
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: 销毁中转 Activity")
    }

    // 判断Service是否在运行
    @Throws(ClassNotFoundException::class)
    private fun isServiceRunning(context: Context, serviceName: String?): Boolean {
        if ("" == serviceName || serviceName == null) {
            return false
        }
        val myManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningService = myManager.getRunningServices(Integer.MAX_VALUE)
            as ArrayList<ActivityManager.RunningServiceInfo>
        for (i in 0 until runningService.size) {
            if (runningService[i].service.className.toString() == serviceName) {
                return true
            }
        }
        return false
    }
}
