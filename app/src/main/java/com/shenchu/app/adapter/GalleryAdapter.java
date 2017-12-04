package com.shenchu.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shenchu.app.vo.BaseBean;
import com.shenchu.app.vo.ViewHolder;

import java.util.List;

public abstract class GalleryAdapter<T> extends
        RecyclerView.Adapter<ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private LayoutInflater mInflater;
    private List<T> mList;
    private List<Integer> mLayoutList;
    private int mTypeCount;
    private View mConvertView;
    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(Context context, List<T> list, List<Integer> layoutList) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mLayoutList = layoutList;
        mTypeCount = layoutList.size();
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return ((BaseBean) mList.get(position)).getLayoutType();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        int item_type = getItemViewType(position);
        mConvertView = mInflater.inflate(mLayoutList.get(item_type),
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(mConvertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        int item_type = getItemViewType(position);
        convert(viewHolder, mList.get(position), position, item_type);
//        if (mOnItemClickListener != null) {
//            View view = viewHolder.getView(R.id.ll_root);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mOnItemClickListener.onItemClick(position);
//                }
//            });
//        }
    }

    public abstract void convert(ViewHolder holder, T item, int position, int type);

    public void notifyData(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //设置点击事件的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}