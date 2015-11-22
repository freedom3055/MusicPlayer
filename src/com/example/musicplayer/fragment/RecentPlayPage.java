package com.example.musicplayer.fragment;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlayActivity;
import com.example.musicplayer.adapter.SongsAdapter;
import com.example.musicplayer.db.RecentPlayDAO;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;

/**
 * 最近播放界面
 * @author guoqiang.ma
 *
 */
public class RecentPlayPage extends BasePage implements OnItemClickListener {
	private ListView listView;
	private SongsAdapter adapter;
	private UpdateListReceiver receiver=new UpdateListReceiver();
	private List<Music> list;

	public int getLayoutId() {
		return R.layout.recent_play;
	}

	public void initView() {

		listView = (ListView) findViewById(R.id.listView_recent_play);
		//从数据获取最近播放歌曲
		RecentPlayDAO dao = new RecentPlayDAO();
		list = dao.findAll();

		adapter = new SongsAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void initData() {

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().registerReceiver(receiver, new IntentFilter(Constant.ACTION_UPDATE_LIST));
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//进入播放界面
		Intent intent = new Intent(getActivity(), PlayActivity.class);
		intent.putExtra(Constant.POSITION, position);
		MusicService.setPlayList(list);
		startActivity(intent);
	}
	
	public class UpdateListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//更新最近播放列表
			Music music = (Music) intent.getSerializableExtra(Constant.MUSIC);
			list.add(music);
			adapter.notifyDataSetChanged();
		}

	}

}
