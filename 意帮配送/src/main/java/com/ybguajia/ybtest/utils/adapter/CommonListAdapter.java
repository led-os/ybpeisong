package com.ybguajia.ybtest.utils.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * 公共的adapter
 *
 * @author Administrator
 */

public abstract class CommonListAdapter<T> extends BaseAdapter {
    protected List<T> datas;
    protected LayoutInflater inflater;
    protected Context context;
    private int itemlayoutId;

    private ViewHolder mViewHolder;


    public void setDatas(List<T> datas) {
        if (datas != null) {
            this.datas = datas;
        } else {
            this.datas = new ArrayList<T>();
        }
    }

    public CommonListAdapter(List<T> datas, Context context, int itemlayoutId) {
        this.datas = datas;
        this.itemlayoutId = itemlayoutId;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public T getItem(int arg0) {
        return datas.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHoler(context, convertView, itemlayoutId, parent, position);
        mViewHolder = viewHolder;
        converView(viewHolder, getItem(position));
        return viewHolder.getmConvertView();
    }

    public ViewHolder getViewHolder() {
        return mViewHolder;
    }

    public abstract void converView(ViewHolder viewHolder, T item);

}
