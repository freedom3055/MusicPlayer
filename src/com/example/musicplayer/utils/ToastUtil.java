package com.example.musicplayer.utils;

import android.widget.Toast;
import com.example.musicplayer.activity.MusicApplication;

public class ToastUtil {
	public static void showToast(String message) {
		Toast.makeText(MusicApplication.getContext(), message,
				Toast.LENGTH_SHORT).show();
	}
}
