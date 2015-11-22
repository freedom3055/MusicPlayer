package com.example.musicplayer.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.service.MusicConnect;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.utils.DateUtil;
import com.example.musicplayer.utils.MediaUtil;

/**
 * 播放界面
 * 
 * @author guoqiang.ma
 * 
 */
public class PlayActivity extends Activity {

	private Button buttonStart;
	private SeekBar seekBar;
	private TextView playTime;
	private TextView leftTime;
	private Timer timer;
	private TextView title;
	private ImageView imageView;
	private Bitmap defaultAlbumIcon;
	private Button playMode;
	private CompeleteReceiver receiver = new CompeleteReceiver();

	private MusicConnect musicConnect;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicConnect = MusicConnect.Stub.asInterface(service);
			startPlay();
		}

	};
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				// 刷新时间及进度条
				playTime.setText(DateUtil.formatTime(musicConnect
						.getCurPosition()));
				leftTime.setText(DateUtil.formatTime(musicConnect.getDuration()
						- musicConnect.getCurPosition()));
				seekBar.setProgress(musicConnect.getCurPosition());

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
		registerReceiver(receiver, new IntentFilter(Constant.ACTION_COMPELETE));

		initView();

		Intent intent = getIntent();
		intent.setClass(this, MusicService.class);
		if (!isServiceRunning(MusicApplication.getContext(),
				MusicService.class.getName())) {
			startService(intent);
		}
		bindService(intent, connection, Context.BIND_AUTO_CREATE);

	}

	private void initView() {
		buttonStart = (Button) findViewById(R.id.buttonStart);
		seekBar = (SeekBar) findViewById(R.id.seekBarProgressChange);
		playTime = (TextView) findViewById(R.id.textViewTimeOne);
		leftTime = (TextView) findViewById(R.id.textViewTimeTwo);
		title = (TextView) findViewById(R.id.textViewTitle);
		imageView = (ImageView) findViewById(R.id.imageView_play_song);
		playMode = (Button) findViewById(R.id.buttonPlayMode);
		defaultAlbumIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_mp_song_list);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				try {
					musicConnect.seekTo(seekBar.getProgress());
					startUpdateUI();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				resetUI();
				try {
					seekBar.setMax(musicConnect.getDuration());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					playTime.setText(DateUtil.formatTime(progress));
					leftTime.setText(DateUtil.formatTime(seekBar.getMax()
							- progress));
				}
			}
		});
	}

	/**
	 * 开始播放音乐
	 */
	private void startPlay() {
		try {
			int position = getIntent().getIntExtra(Constant.POSITION, -1);
			if (position < 0) {
				position = musicConnect.getPosition();
			}
			if (musicConnect.isPlaying()) {
				if (position != musicConnect.getPosition()) {
					musicConnect.stop();
					resetUI();
					musicConnect.play(position);
				}
			} else {
				musicConnect.play(position);
			}
			startUpdateUI();

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始定时刷新UI
	 */
	private void startUpdateUI() {

		timer = new Timer();
		try {
			seekBar.setMax(musicConnect.getDuration());
			buttonStart.setBackgroundResource(R.drawable.pause_button);
			title.setText(musicConnect.getMusicName().replace(".mp3", ""));

			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {
					try {
						return MediaUtil.getCachedArtwork(PlayActivity.this,
								musicConnect.getAlbumId(), defaultAlbumIcon);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					imageView.setImageBitmap(result);
					super.onPostExecute(result);
				}
			}.execute();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 0, 1000);
	}

	/**
	 * 重置UI
	 */
	private void resetUI() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		playTime.setText("00:00");
		leftTime.setText("00:00");
		seekBar.setProgress(0);
		seekBar.setMax(0);
		buttonStart.setBackgroundResource(R.drawable.play_button);
	}

	public void buttonClick(View view) {
		switch (view.getId()) {
		case R.id.buttonNext:// 下一首
			try {
				resetUI();
				musicConnect.playNext();
				startUpdateUI();
			} catch (RemoteException e2) {
				e2.printStackTrace();
			}
			break;

		case R.id.buttonPrevious:// 前一首
			try {
				resetUI();
				musicConnect.playPre();
				startUpdateUI();

			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			break;

		case R.id.buttonStart:// 播放或暂停
			try {

				if (musicConnect.isPlaying()) {
					musicConnect.pause();
					buttonStart.setBackgroundResource(R.drawable.play_button);
				} else {
					musicConnect.restart();
					buttonStart.setBackgroundResource(R.drawable.pause_button);
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;

		case R.id.buttonPlayMode:// 播放模式的切换
			try {
				int mode = musicConnect.getPlayMode();
				mode++;
				if (mode > Constant.RANDOM_PLAY_MODE) {
					mode = Constant.LIST_PLAY_MODE;
				}
				musicConnect.setPlayMode(mode);

				if (mode == Constant.LIST_PLAY_MODE) {// 以列表顺序播放
					playMode.setBackgroundResource(R.drawable.btn_playmode_normal_normal);
				} else if (mode == Constant.SINGLE_LOOP_PLAY_MODE) {// 单曲循环播放
					playMode.setBackgroundResource(R.drawable.btn_playmode_repeat_single_normal);
				} else {// 随机播放
					playMode.setBackgroundResource(R.drawable.btn_playmode_random_normal);
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}

			break;

		case R.id.buttonPlaylist:// 切换到播放列表
			startActivity(new Intent(this, MainActivity.class));
			break;
		}
	}

	@Override
	protected void onDestroy() {

		// 解除界面与service的绑定
		if (connection != null) {
			unbindService(connection);
		}
		try {
			if (!musicConnect.isPlaying()) {
				stopService(new Intent(this, MusicService.class));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private class CompeleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// service播放完成回调
			resetUI();
			startUpdateUI();
		}

	}

	/**
	 * 
	 * 判断service是否在运行
	 * 
	 * @param context
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
