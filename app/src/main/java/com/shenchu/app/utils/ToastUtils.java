package com.shenchu.app.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 16/11/21.
 */

public class ToastUtils {
    private static ToastUtils mInstance;
    private Toast mToast;

    private ToastUtils() {
    }

    public static ToastUtils getInstance() {
        if (mInstance == null) {
            mInstance = new ToastUtils();
        }
        return mInstance;
    }

    public void showToast(Context context, String text, int time) {
        if (context == null) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, text, time);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
