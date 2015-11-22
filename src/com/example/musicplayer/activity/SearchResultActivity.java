package com.example.musicplayer.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongsAdapter;
import com.example.musicplayer.db.RecentPlayDAO;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.utils.ToastUtil;

/**
 * 
 *搜索及界面
 * @author guoqiang.ma
 *
 */
public class SearchResultActivity extends Activity implements
		OnItemClickListener {
	private ListView listView;
	private ArrayList<Music> list;
	private SongsAdapter adapter;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);

		searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!TextUtils.isEmpty(query)) {
					search(query);
					if (list.size() > 0) {
						adapter.notifyDataSetChanged();
					} else {
						ToastUtil.showToast("没有匹配的内容");
					}

				} else {
					ToastUtil.showToast("输入内容");
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		listView = (ListView) findViewById(R.id.listView_search_result);
		listView.setOnItemClickListener(this);
		list = new ArrayList<Music>();
		adapter = new SongsAdapter(this, list);
		listView.setAdapter(adapter);
	}

	/**
	 * 对于歌曲名的模糊查询
	 * 
	 * @param word
	 */
	private void search(String word) {
		list.clear();
		adapter.notifyDataSetChanged();
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?",
				new String[] { "%" + word + "%" },
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		if (cursor != null && cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
			int albumIdIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
			int singerIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int albumIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			int durationIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
			int nameIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);

			do {
				long id = cursor.getLong(idIndex);
				long albumId = cursor.getLong(albumIdIndex);
				String singer = cursor.getString(singerIndex);
				if (MediaStore.UNKNOWN_STRING.equals(singer)) {
					singer = "未知艺术家";
				}
				String album = cursor.getString(albumIndex);
				long time = cursor.getLong(durationIndex);
				String path = cursor.getString(pathIndex);
				String name = cursor.getString(nameIndex);

				Music music = new Music();
				music.setId(id);
				music.setAlbumId(albumId);
				music.setName(name);
				music.setArtist(singer);
				music.setAlbum(album);
				music.setDuration(time);
				music.setPath(path);

				list.add(music);
			} while (cursor.moveToNext());
			cursor.close();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 将播放的歌曲插入到最近播放数据库列表
		RecentPlayDAO dao = new RecentPlayDAO();
		if (dao.insert(list.get(position)) > 0) {
			// 通知最近播放列表更新
			Intent boardIntent = new Intent(Constant.ACTION_UPDATE_LIST);
			boardIntent.putExtra(Constant.MUSIC, list.get(position));
			sendBroadcast(boardIntent);
		}
		// 播放歌曲
		Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra(Constant.POSITION, position);
		MusicService.setPlayList(list);
		startActivity(intent);
	}
}
