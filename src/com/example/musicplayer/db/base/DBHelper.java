package com.example.musicplayer.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

/**
 * 最近播放列表相关的数据库Helper
 * @author guoqiang.ma
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "database.db";

	public static final String TABLE_NAME = "RecentPlayList";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL("Create  TABLE IF NOT EXISTS " + TABLE_NAME + "("
				+ MediaStore.Audio.Media._ID + " int PRIMARY KEY,"
				+ MediaStore.Audio.Media.ALBUM_ID + " long,"
				+ MediaStore.Audio.Media.ARTIST + " int,"
				+ MediaStore.Audio.Media.DURATION + " long,"
				+ MediaStore.Audio.Media.DISPLAY_NAME + " text,"
				+ MediaStore.Audio.Media.DATA + " text,"
				+ MediaStore.Audio.Media.ALBUM + " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
