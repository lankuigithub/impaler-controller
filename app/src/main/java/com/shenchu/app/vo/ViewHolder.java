package com.shenchu.app.vo;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by admin on 2017/11/21.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(View convertView) {
        super(convertView);
        mConvertView = convertView;
        this.mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public void setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
    }

    /**
     * 为TextView设置颜色
     *
     * @param viewId
     * @param resId
     * @return
     */
    public void setTextColorRes(int viewId, int resId) {
        TextView view = getView(viewId);
        view.setTextColor(resId);
    }

    /**
     * 为TextView设置字符串划掉
     *
     * @param viewId
     * @return
     */
    public void setTextObsolete(int viewId) {
        TextView view = getView(viewId);
        view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bitmap
     * @return
     */
    public void setImageByBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        //ImageLoaderUtils.getInstance().displayImage(url, view);
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public void setImageByUrl(int viewId, String url) {
        ImageView view = getView(viewId);
        //ImageLoaderUtils.getInstance().displayImage(url, view);
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param visible
     * @return
     */
    public void setVisibility(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
    }
}
