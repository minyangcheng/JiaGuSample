package com.min.dump.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.min.dump.Constants;

public class Utils {

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(
                Constants.CONFIG_PRE_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                Constants.CONFIG_PRE_NAME, Context.MODE_WORLD_READABLE);
        return sp.getString(key, defaultValue);
    }

}
