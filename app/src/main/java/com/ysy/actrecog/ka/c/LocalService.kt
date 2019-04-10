package com.ysy.actrecog.ka.c

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.ysy.actrecog.ka.IKeepAlive

class LocalService : Service() {

    companion object {
        private const val TAG = "TEST-1-LocalService"
    }

    private lateinit var iKeepAlive: IKeepAlive
    private var mIsBound: Boolean = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: 创建 LocalService")
        bindRemoteService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: 绑定 LocalService")
        return stub
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: 解绑 LocalService")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: 销毁 LocalService")
        super.onDestroy()
    }

    private val stub = object : IKeepAlive.Stub() {

        @Throws(RemoteException::class)
        override fun bindSuccess() {
            Log.d(TAG, "bindSuccess: RemoteService 绑定 LocalService 成功")
        }

        @Throws(RemoteException::class)
        override fun unbind() {
            applicationContext.unbindService(connection)
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected: RemoteService 链接成功")
            mIsBound = true
            iKeepAlive = IKeepAlive.Stub.asInterface(service)
            try {
                iKeepAlive.bindSuccess()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: RemoteService 断开连接，重新启动")
            mIsBound = false
            createTransferActivity()
        }
    }

    private fun createTransferActivity() {
        startActivity(Intent(this, TransferActivity::class.java).apply {
            action = TransferActivity.ACTION_FROM_SELF
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun bindRemoteService() {
        val intent = Intent().apply {
            component = ComponentName("com.ysy.actrecog", "com.ysy.actrecog.ka.s.RemoteService")
        }
        if (!applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            Log.d(TAG, "bindRemoteService: 绑定 RemoteService 失败")
            stopSelf()
        }
    }

    private fun unbindRemoteService() {
        if (mIsBound) {
            try {
                // 先让RemoteService解除绑定LocalService
                iKeepAlive.unbind()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            // 解除LocalService与RemoteService
            applicationContext.unbindService(connection)
            stopSelf()
        }
    }
}
