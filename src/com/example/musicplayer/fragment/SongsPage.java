package com.example.musicplayer.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayActivity;
import com.example.musicplayer.activity.SearchResultActivity;
import com.example.musicplayer.adapter.SongsAdapter;
import com.example.musicplayer.db.RecentPlayDAO;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.utils.ToastUtil;

/**
 * 
 * 歌曲界面
 * @author guoqiang.ma
 *
 */
public class SongsPage extends BasePage implements OnItemClickListener {
	private ListView listView;
	private SongsAdapter adapter;
	private List<Music> list;

	public int getLayoutId() {
		return R.layout.songs;
	}

	public void initView() {
		listView = (ListView) findViewById(R.id.listView_songs);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);
		Button searchButton = new Button(getActivity());
		searchButton.setText("搜索");
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		searchButton.setLayoutParams(params);
		listView.addHeaderView(searchButton);

		searchButton
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//进入播放界面
						Intent intent = new Intent(getActivity(),
								SearchResultActivity.class);
						startActivity(intent);
					}
				});
	}

	@Override
	public void initData() {
		//获取所有歌曲
		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		list = new ArrayList<Music>();

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
		adapter = new SongsAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(1, 1, 1, " 重命名");
		menu.add(1, 2, 2, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			rename(item);
			break;
		case 2:
			delete(item);
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删除歌曲
	 * @param item
	 */
	private void delete(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final Music music = adapter.getItem(info.position-1);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("删除！");
		builder.setMessage("确定删除音乐？");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				ContentResolver contentResolver = getActivity()
						.getContentResolver();
				int result = contentResolver.delete(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						MediaStore.Audio.Media._ID + "=?",
						new String[] { music.getId() + "" });
				if (result > -1) {
					File f = new File(music.getPath());
					try {
						if (f.delete()) {
							adapter.remove(music);
							adapter.notifyDataSetChanged();
							ToastUtil.showToast("删除成功");
						}
					} catch (Exception e) {

					}

				} else {
					ToastUtil.showToast("删除失败");
				}

			}
		});
		builder.show();
	}

	/**
	 * 重命名歌曲
	 * @param item
	 */
	private void rename(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final Music music = adapter.getItem(info.position-1);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View inputView = LayoutInflater.from(getActivity()).inflate(
				R.layout.input_edittext, null);
		builder.setView(inputView);
		final EditText inputEditText = (EditText) inputView
				.findViewById(R.id.edittext_input);
		inputEditText.setText(music.getName());
		builder.setTitle("重命名");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String name = inputEditText.getText().toString().trim();
				if (!TextUtils.isEmpty(name)) {
					ContentValues values = new ContentValues(1);
					values.put(MediaStore.Audio.Media.DISPLAY_NAME, name);

					ContentResolver contentResolver = getActivity()
							.getContentResolver();
					int result = contentResolver.update(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							values, MediaStore.Audio.Media._ID + "=?",
							new String[] { music.getId() + "" });

					if (result > -1) {
						music.setName(name);
						adapter.notifyDataSetChanged();
						ToastUtil.showToast("重命名成功");
					} else {
						ToastUtil.showToast("重命名失败");
					}

				} else {
					ToastUtil.showToast("请输入文字");
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//将播放的歌曲插入到最近播放数据库列表
		RecentPlayDAO dao = new RecentPlayDAO();
		if (dao.insert(list.get(position-1)) > 0) {
			//通知最近播放列表更新
			Intent boardIntent = new Intent(Constant.ACTION_UPDATE_LIST);
			boardIntent.putExtra(Constant.MUSIC, list.get(position-1));
			getActivity().sendBroadcast(boardIntent);
		}
		//播放歌曲
		Intent intent = new Intent(getActivity(), PlayActivity.class);
		intent.putExtra(Constant.POSITION, position-1);
		MusicService.setPlayList(list);
		startActivity(intent);

	}

}
