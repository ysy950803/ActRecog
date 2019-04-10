package com.ysy.actrecog

import android.annotation.SuppressLint

@SuppressLint("PrivateApi")
@Deprecated("")
object ChreApi {

    fun sendMessageToNanoApp() {
        try {
            val ContextHubClient = Class.forName("android.hardware.location.ContextHubClient")
            val ContextHubInfo = Class.forName("android.hardware.location.ContextHubInfo")
            val NanoAppMessage = Class.forName("android.hardware.location.NanoAppMessage")
            val IContextHubClient = Class.forName("android.hardware.location.IContextHubClient")

            val clientCons = ContextHubClient.getDeclaredConstructor(ContextHubInfo)
            clientCons.isAccessible = true
            val msgCons = NanoAppMessage.getDeclaredConstructor(
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                ByteArray::class.java,
                Boolean::class.javaPrimitiveType
            )
            msgCons.isAccessible = true

            val info = ContextHubInfo.newInstance()
            val client = clientCons.newInstance(info)
            val msg = msgCons.newInstance(1, 1, byteArrayOf(1), false)
            val iClient = IContextHubClient.newInstance()

            val setClientProxy = ContextHubClient.getDeclaredMethod("setClientProxy", IContextHubClient)
            setClientProxy.isAccessible = true
            setClientProxy.invoke(client, iClient)

            val sendMessageToNanoApp = ContextHubClient.getDeclaredMethod("sendMessageToNanoApp", NanoAppMessage)
            sendMessageToNanoApp.isAccessible = true
            sendMessageToNanoApp.invoke(client, msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun close() {
        try {
            val ContextHubClient = Class.forName("android.hardware.location.ContextHubClient")
            val ContextHubInfo = Class.forName("android.hardware.location.ContextHubInfo")

            val clientCons = ContextHubClient.getDeclaredConstructor(ContextHubInfo)
            clientCons.isAccessible = true

            val info = ContextHubInfo.newInstance()
            val client = clientCons.newInstance(info)

            val close = ContextHubClient.getDeclaredMethod("close")
            close.isAccessible = true
            close.invoke(client)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
