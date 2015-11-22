package com.example.musicplayer.utils;

public class DateUtil {
	public static String formatTime(long time) {
		long  minutes= time / 60 / 1000;
		long seconds = time / 1000 % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
}
