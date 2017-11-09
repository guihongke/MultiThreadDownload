package com.kgh.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * SharedPreferences工具类
 *
 * @author guihong_ke
 */
public class SharedPreferencesUtil {

    public static String PREFS_NAME = "multi-thread-download";

    private SharedPreferences preferences;
    private static SharedPreferencesUtil SharedPreferencesUtil;


    public static SharedPreferencesUtil getInstance() {
        return SharedPreferencesUtil;
    }

    public static void initSharedPreferencesUtil(Context context) {
        SharedPreferencesUtil = new SharedPreferencesUtil(context);
        PREFS_NAME = context.getPackageName();
    }

    public SharedPreferencesUtil(Context context) {
        init(context);
    }

    private void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getInstance().preferences.getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getInstance().preferences.getInt(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return getInstance().preferences.getString(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getInstance().preferences.getLong(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return getInstance().preferences.getFloat(key, defValue);
    }

    public static void setLong(String key, long value) {
        Editor editor = getInstance().preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setBoolean(String key, boolean value) {
        Editor editor = getInstance().preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setString(String key, String value) {
        Editor editor = getInstance().preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setInt(String key, int value) {
        Editor editor = getInstance().preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setFloat(String key, float value) {
        Editor editor = getInstance().preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void remove(String key) {
        Editor editor = getInstance().preferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
