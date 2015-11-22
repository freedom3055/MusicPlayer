package com.example.musicplayer.adapter.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * 
 * adapter的基类
 * @author guoqiang.ma
 *
 * @param <T>
 */
public abstract class DefaultAdapter<T> extends ArrayAdapter<T> {

	public DefaultAdapter(Context context, List<T> objects) {
		super(context, 1, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = ViewHolder.getViewHolder(getContext(),
				convertView, parent, getItemLayoutId());
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}

	/**
	 * 获取item的layout的id
	 * @return
	 */
	public abstract int getItemLayoutId();

	/**
	 * 数据模型与视图的转化
	 * @param viewHolder
	 * @param item
	 */
	public abstract void convert(ViewHolder viewHolder, T item);
}
