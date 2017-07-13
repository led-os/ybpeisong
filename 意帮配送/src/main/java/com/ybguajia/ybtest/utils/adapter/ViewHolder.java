package com.ybguajia.ybtest.utils.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybguajia.ybtest.utils.net.ImgUtils;

/**
 * 统一的ViewHoler
 *
 * @author Administrator
 */
public class ViewHolder {

    /**
     * 存放键值对，效率比Map高
     */
    private SparseArray<View> mSparseArray;
    private View mConvertView;
    private int mPosition;
    private Context context;

    public ViewHolder(int layoutId, ViewGroup parent, Context context) {
        this.context = context;
        mSparseArray = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHoler(Context context, View convertView, int layoutId, ViewGroup parent, int
            position) {
        if (convertView == null) {
            return new ViewHolder(layoutId, parent, context);
        } else {
            ViewHolder mViewHolder = (ViewHolder) convertView.getTag();
            mViewHolder.mPosition = position;
            return mViewHolder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mSparseArray.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public View getmConvertView() {
        return mConvertView;
    }

    /**
     * 设置文本
     */
    public ViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置本地图片
     */
    public ViewHolder setDiskImg(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /**
     * 设置网络图片
     */
    public ViewHolder setNetImg(int viewId, String imgPath) {
        ImageView imageView = getView(viewId);
        ImgUtils.getInstance(context).getImageLoader().displayImage(imgPath, imageView);
        return this;
    }

    public int getmPosition() {
        return mPosition;
    }
}
