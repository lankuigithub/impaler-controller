package com.shenchu.app.utils;

/**
 * Created by admin on 2017/11/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUtils {
    private static DataUtils mInstance;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;
    private boolean isInit = false;

    private DataUtils() {
    }

    public static DataUtils getInstance() {
        if (mInstance == null) {
            mInstance = new DataUtils();
        }
        return mInstance;
    }

    public void initPreferences(Context context) {
        if (!isInit) {
            isInit = true;
            mSharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
    }

    public void savePreferences(String key, Object value) {
        // 信息保存到preferences中
        if (value == null) {
            mEditor.putString(key, null);
        } else {
            mEditor.putString(key, value.toString());
        }
        // 提交信息
        mEditor.commit();
    }

    public String loadPreferences(String key) {
        if (mSharedPreferences == null) {
            return "";
        }
        // 取出数据
        return mSharedPreferences.getString(key, "");
    }

    public void cleanData() {
        mEditor.clear();
        mEditor.commit();
    }
}
