package com.world.one.oneworld.logger;

import android.util.Log;

/**
 * Created by Shailendra on 17-05-2016.
 */
public class Logger {
    public static String TEST = "oneWorld";
    private static boolean isLogEnable = true;

    public static void logInfo(String tag, String msg) {
        if (isLogEnable) {
            Log.v(tag, msg);
        }
    }

    public static void logError(String tag, String msg) {
        if (isLogEnable) {
            Log.e(tag, msg);
        }
    }

    public static void logError(String tag, String msg, Throwable throwable) {
        if (isLogEnable) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void logException(String tag, Exception exception) {
        if (isLogEnable) {
            Log.w(tag, exception);
        }
    }
}
