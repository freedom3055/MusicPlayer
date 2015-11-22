package com.example.musicplayer.adapter.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 存储convertView的相关子视图
 * @author guoqiang.ma
 *
 */
public class ViewHolder {
	private final SparseArray<View> views;
	private View convertView;

	public View getConvertView() {
		return convertView;
	}

	private ViewHolder(Context context, ViewGroup parent, int layoutId) {
		this.convertView = LayoutInflater.from(context).inflate(layoutId,
				parent, false);
		this.views = new SparseArray<View>();
		this.convertView.setTag(this);
	}

	public static ViewHolder getViewHolder(Context context, View convertView,
			ViewGroup parent, int layoutId) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId);
		} else {
			return (ViewHolder) convertView.getTag();
		}

	}

	/**
	 * 以id为key来获取相应的view
	 * @param id
	 * @return
	 */
	public View getView(int id) {
		View view = views.get(id);
		if (view == null) {
			view = convertView.findViewById(id);
			views.put(id, view);
		}
		return view;
	}

	public TextView getTextView(int id) {
		return (TextView) getView(id);
	}

	public ImageView getImageView(int id) {
		return (ImageView) getView(id);
	}

}
