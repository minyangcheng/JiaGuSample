package com.min.source.util;

import android.text.TextUtils;
import android.util.Log;

public class L {

    private static final String TAG = "ShellDemo";
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static volatile boolean writeLogs = true;

    private L() {
    }

    public static void d(String message, Object... args) {
        log(Log.DEBUG, null, message, args);
    }

    public static void i(String message, Object... args) {
        log(Log.INFO, null, message, args);
    }

    public static void w(String message, Object... args) {
        log(Log.WARN, null, message, args);
    }

    public static void e(Throwable ex) {
        log(Log.ERROR, ex, null);
    }

    public static void e(String message, Object... args) {
        log(Log.ERROR, null, message, args);
    }

    public static void e(Throwable ex, String message, Object... args) {
        log(Log.ERROR, ex, message, args);
    }

    private static void log(int priority, Throwable ex, String message, Object... args) {
        if (!writeLogs)
            return;
        if (args.length > 0) {
            message = String.format(message, args);
        }

        String log;
        if (ex == null) {
            log = message;
        } else {
            String logMessage = message == null ? ex.getMessage() : message;
            String logBody = Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }

        int index = 0;
        int count = 1;
        int maxLength = 3 * 1024;
        String finalString;
        if (TextUtils.isEmpty(log)) {
            return;
        }
        while (index < log.length()) {
            if (log.length() <= index + maxLength) {
                finalString = log.substring(index);
            } else {
                finalString = log.substring(index, maxLength * (count++));
            }
            index += maxLength;
            Log.println(priority, TAG, finalString);
        }
    }

}
