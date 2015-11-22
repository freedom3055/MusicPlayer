package com.example.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.musicplayer.R;
import com.example.musicplayer.fragment.AlbumsPage;
import com.example.musicplayer.fragment.ArtistsPage;
import com.example.musicplayer.fragment.RecentPlayPage;
import com.example.musicplayer.fragment.SongsPage;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.view.IndexViewPager;

/**
 * 歌曲主界面
 * @author guoqiang.ma
 *
 */
public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener, OnPageChangeListener {
	private List<Fragment> fragments;
	private RadioGroup radioGroup;
	private IndexViewPager viewPager;
	private Button playing;
	private StatusBarReceiver receiver = new StatusBarReceiver();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		initView();
		
		registerReceiver(receiver, new IntentFilter(Constant.ACTION_STATUS_BAR));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initView() {
		viewPager = (IndexViewPager) findViewById(R.id.viewPager);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

		fragments = new ArrayList<Fragment>();
		fragments.add(new RecentPlayPage());
		fragments.add(new ArtistsPage());
		fragments.add(new AlbumsPage());
		fragments.add(new SongsPage());
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOffscreenPageLimit(3);

		playing = (Button) findViewById(R.id.button_playing);
		if (PlayActivity.isServiceRunning(MusicApplication.getContext(),
				MusicService.class.getName())) {
			
			//显示播放状态栏
			Music music = MusicService.getPlayList().get(
					MusicService.getPlayPosition());
			playing.setVisibility(View.VISIBLE);
			playing.setText("正在播放：" + music.getName());
		} else {
			playing.setVisibility(View.GONE);
		}
		playing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PlayActivity.class));
			}
		});

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		int page = 0;
		switch (arg1) {
		case R.id.radio_recent_play:
			page = 0;
			break;

		case R.id.radio_artists:
			page = 1;
			break;
		case R.id.radio_albums:
			page = 2;
			break;
		case R.id.radio_songs:
			page = 3;
			break;
		}
		viewPager.setCurrentItem(page, false);
	}

	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	private class StatusBarReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Music music = (Music) intent.getSerializableExtra(Constant.MUSIC);
			
			//收到通知更新状播放态栏
			if (music!=null) {
				playing.setVisibility(View.VISIBLE);
				playing.setText("正在播放：" + music.getName());
			}else {
				playing.setVisibility(View.GONE);
			}
		}

	}

}
