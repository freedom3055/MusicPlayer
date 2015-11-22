package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePage extends Fragment {
	protected View rootView;
	private boolean once;

	@Override
	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {
		if (rootView == null) {
			rootView = paramLayoutInflater.inflate(getLayoutId(), null);
			initView();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint() && !once) {
			once = true;
			initData();
		}

	}

	public View findViewById(int id) {
		return rootView.findViewById(id);
	}

	public abstract int getLayoutId();

	public abstract void initData();

	public abstract void initView();
}
