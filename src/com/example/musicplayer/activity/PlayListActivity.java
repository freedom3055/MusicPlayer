package com.example.musicplayer.activity;

import java.util.List;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongsAdapter;
import com.example.musicplayer.db.RecentPlayDAO;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 专辑或艺术家相关的歌曲详情列表界面
 * @author guoqiang.ma
 *
 */
public class PlayListActivity extends Activity implements OnItemClickListener {

	private static List<Music> playList;
	private ListView listView;

	public static void setPlayList(List<Music> playList) {
		PlayListActivity.playList = playList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_list);

		listView = (ListView) findViewById(R.id.listView_play_list);
		listView.setAdapter(new SongsAdapter(this, playList));
		listView.setOnItemClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		playList = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//将播放的歌曲插入到最近播放数据库列表
		RecentPlayDAO dao = new RecentPlayDAO();
		if (dao.insert(playList.get(position))>0) {
			//通知最近播放列表更新
			Intent boardIntent = new Intent(Constant.ACTION_UPDATE_LIST);
			boardIntent.putExtra(Constant.MUSIC, playList.get(position));
			sendBroadcast(boardIntent);
		}
		//播放歌曲
		Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra(Constant.POSITION, position);
		MusicService.setPlayList(playList);
		startActivity(intent);
	}
}
