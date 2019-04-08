package com.ysy.actrecog

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

class ARApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
    }
}
