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
            Class<?> IContextHubClient = Class.forName("android.hardware.location.IContextHubClient");

            Constructor<?> clientCons = ContextHubClient.getDeclaredConstructor(ContextHubInfo);
            clientCons.setAccessible(true);
            Constructor<?> msgCons = NanoAppMessage.getDeclaredConstructor(long.class, int.class, byte[].class, boolean.class);
            msgCons.setAccessible(true);

            Object info = ContextHubInfo.newInstance();
            Object client = clientCons.newInstance(info);
            Object msg = msgCons.newInstance(1, 1, new byte[]{1}, false);
            Object iClient = IContextHubClient.newInstance();

            Method setClientProxy = ContextHubClient.getDeclaredMethod("setClientProxy", IContextHubClient);
            setClientProxy.setAccessible(true);
            setClientProxy.invoke(client, iClient);

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
            Class<?> ContextHubInfo = Class.forName("android.hardware.location.ContextHubInfo");

            Constructor<?> clientCons = ContextHubClient.getDeclaredConstructor(ContextHubInfo);
            clientCons.setAccessible(true);

            Object info = ContextHubInfo.newInstance();
            Object client = clientCons.newInstance(info);

            Method close = ContextHubClient.getDeclaredMethod("close");
            close.setAccessible(true);
            close.invoke(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
