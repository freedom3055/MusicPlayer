package com.example.musicplayer.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayListActivity;
import com.example.musicplayer.adapter.AlbumsAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Music;

/**
 * 专辑界面
 * @author guoqiang.ma
 *
 */
public class AlbumsPage extends BasePage implements OnItemClickListener {
	private ListView listView;
	private List<Album> list;

	public int getLayoutId() {
		return R.layout.albums;
	}

	public void initView() {
		listView = ((ListView) findViewById(R.id.listView_albums));

		listView.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		//获取所有专辑
		list = new ArrayList<Album>();

		HashSet<String> albumNames = new HashSet<String>();
		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.moveToFirst()) {
			int albumIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM);
			int albumIdIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
			int artistIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);

			do {
				String albumStr = cursor.getString(albumIndex);
				if (!albumNames.contains(albumStr)) {
					Album album = new Album();
					album.setAlbumId(cursor.getLong(albumIdIndex));
					album.setArtist(cursor.getString(artistIndex));
					album.setAlbum(albumStr);
					albumNames.add(albumStr);

					list.add(album);
				}

			} while (cursor.moveToNext());
			cursor.close();
		}
		listView.setAdapter(new AlbumsAdapter(getActivity(), list));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id1) {
		//进入浏览与专辑相关的歌曲
		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Audio.Media.ALBUM_ID + "=?",
				new String[] { list.get(position).getAlbumId() + "" }, null);

		ArrayList<Music> tempList = new ArrayList<Music>();

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

				tempList.add(music);
			} while (cursor.moveToNext());
			cursor.close();
		}
		PlayListActivity.setPlayList(tempList);
		startActivity(new Intent(getActivity(), PlayListActivity.class));
	}

}
