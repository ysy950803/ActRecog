package com.ysy.actrecog;

import android.annotation.SuppressLint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class ChreApi {

    public static void sendMessageToNanoApp() {
        try {
            Class<?> ContextHubClient = Class.forName("android.hardware.location.ContextHubClient");
            Class<?> ContextHubInfo = Class.forName("android.hardware.location.ContextHubInfo");
            Class<?> NanoAppMessage = Class.forName("android.hardware.location.NanoAppMessage");

            Constructor<?> con = ContextHubClient.getConstructor(ContextHubInfo);
            Object info = ContextHubInfo.newInstance();
            Object client = con.newInstance(info);
            Object msg = NanoAppMessage.newInstance();

            Method sendMessageToNanoApp = ContextHubClient.getDeclaredMethod("sendMessageToNanoApp", NanoAppMessage);
            sendMessageToNanoApp.setAccessible(true);
            sendMessageToNanoApp.invoke(client, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            Class<?> ContextHubClient = Class.forName("android.hardware.location.ContextHubClient");
            Object client = ContextHubClient.newInstance();
            Method close = ContextHubClient.getDeclaredMethod("close");
            close.setAccessible(true);
            close.invoke(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
