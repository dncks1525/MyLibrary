package com.chani.mylibrary.utils;

import android.util.Log;

public class AppLog {
    private static final String TAG = "chani";

    private static Inspection sInspection = Inspection.DEBUG;
    private static StringBuilder sStringBuilder = new StringBuilder();

    public static void v(String content) {
        if (sInspection == Inspection.DEBUG) Log.v(TAG, log(content));
    }

    public static void vF(String content) {
        Log.v(TAG, log(content));
    }

    public static void d(String content) {
        if (sInspection == Inspection.DEBUG) Log.d(TAG, log(content));
    }

    public static void dF(String content) {
        Log.d(TAG, log(content));
    }

    public static void i(String content) {
        if (sInspection == Inspection.DEBUG) Log.i(TAG, log(content));
    }

    public static void iF(String content) {
        Log.i(TAG, log(content));
    }

    public static void w(String content) {
        if (sInspection == Inspection.DEBUG) Log.w(TAG, log(content));
    }

    public static void wF(String content) {
        Log.w(TAG, log(content));
    }

    public static void e(String content) {
        Log.e(TAG, log(content));
    }

    public static void inspection(Inspection inspection) {
        sInspection = inspection;
    }

    private static String log(String msg) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        String fileName = ste.getFileName();
        String name = fileName.substring(0, fileName.length()-5);
        String method = ste.getMethodName();

        sStringBuilder.setLength(0);
        sStringBuilder.append("[");
        sStringBuilder.append(name);
        sStringBuilder.append("::");
        sStringBuilder.append(method);
        sStringBuilder.append("] ");
        sStringBuilder.append(msg);

        return sStringBuilder.toString();
    }

    public enum Inspection {
        DEBUG,
        RELEASE,
    }
}
