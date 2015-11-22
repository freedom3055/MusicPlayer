package com.example.musicplayer.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.RemoteException;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.MusicApplication;
import com.example.musicplayer.activity.PlayActivity;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.service.MusicConnect.Stub;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.utils.DateUtil;
import com.example.musicplayer.utils.ToastUtil;

/**
 * 后台歌曲播放service
 * @author guoqiang.ma
 *
 */
public class MusicService extends Service implements OnPreparedListener,
		OnCompletionListener {

	private MediaPlayer mediaPlayer;
	private static List<Music> playList;

	private MusicBinder binder;
	private int playMode = Constant.LIST_PLAY_MODE;
	private static int playPosition = -1;

	public static int getPlayPosition() {
		return playPosition;
	}

	public static void setPlayList(List<Music> playList) {
		if (PlayActivity.isServiceRunning(MusicApplication.getContext(),
				MusicService.class.getName())) {
			playPosition = -1;
		}
		MusicService.playList = playList;
	}

	public static List<Music> getPlayList() {
		return playList;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new MusicBinder();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		playList = null;
		playPosition = -1;

		sendBroadcast(new Intent(Constant.ACTION_STATUS_BAR));
	}

	private void playMusic(int position) {
		playPosition = position;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setLooping(false);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		Music music = playList.get(position);
		try {
			mediaPlayer.setDataSource(music.getPath());
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopMusic() {
		if (mediaPlayer != null) {
			stopForeground(true);
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	private void playNextMusic() {
		playPosition++;
		if (playPosition >= playList.size()) {
			playPosition = 0;
		}
		stopMusic();
		playMusic(playPosition);
	}

	private class MusicBinder extends Stub {

		@Override
		public void play(int position) throws RemoteException {
			playMusic(position);
		}

		@Override
		public void pause() throws RemoteException {
			stopForeground(true);
			mediaPlayer.pause();
		}

		@Override
		public void stop() throws RemoteException {
			stopMusic();
		}

		@Override
		public void playNext() throws RemoteException {
			playNextMusic();

		}

		@Override
		public void playPre() throws RemoteException {
			playPosition--;
			if (playPosition < 0) {
				playPosition = 0;
			}
			stopMusic();
			playMusic(playPosition);
		}

		@Override
		public void seekTo(int rate) throws RemoteException {
			mediaPlayer.seekTo(rate);
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			if (mediaPlayer == null) {
				return false;
			}
			return mediaPlayer.isPlaying();
		}

		@Override
		public void setPlayMode(int mode) throws RemoteException {
			if (mode == Constant.SINGLE_LOOP_PLAY_MODE) {
				mediaPlayer.setLooping(true);
				ToastUtil.showToast("单曲循环");
			} else {
				mediaPlayer.setLooping(false);
				if (mode == Constant.LIST_PLAY_MODE) {
					ToastUtil.showToast("顺序播放");
				}else {
					ToastUtil.showToast("随机播放");
				}
			}
			playMode = mode;
		}

		@Override
		public int getCurPosition() throws RemoteException {
			return mediaPlayer.getCurrentPosition();
		}

		@Override
		public int getDuration() throws RemoteException {
			return mediaPlayer.getDuration();
		}

		@Override
		public void restart() throws RemoteException {
			if (!mediaPlayer.isPlaying()) {
				setNotification(mediaPlayer, playPosition);
				mediaPlayer.start();
			}
		}

		@Override
		public int getPosition() throws RemoteException {

			return playPosition;
		}

		@Override
		public String getMusicName() throws RemoteException {
			return playList.get(playPosition).getName();
		}

		@Override
		public long getAlbumId() throws RemoteException {
			return playList.get(playPosition).getAlbumId();
		}

		@Override
		public int getPlayMode() throws RemoteException {

			return playMode;
		}

	};

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		setNotification(mediaPlayer, playPosition);
		//通知更新播放状态栏
		Intent intent = new Intent(Constant.ACTION_STATUS_BAR);
		intent.putExtra(Constant.MUSIC, playList.get(playPosition));
		sendBroadcast(intent);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		sendBroadcast(new Intent(Constant.ACTION_COMPELETE));

		switch (playMode) {
		case Constant.LIST_PLAY_MODE:// 以列表顺序播放
			playNextMusic();
			break;

		case Constant.RANDOM_PLAY_MODE:// 随机播放
			Random random = new Random();
			int tempPosition = random.nextInt(playList.size());
			if (playList.size() > 1) {
				while (playPosition == tempPosition) {
					tempPosition = random.nextInt(playList.size());
				}
			}
			stopMusic();
			playMusic(tempPosition);
			break;
		}
	}

	/**
	 * 设置通知栏信息
	 * @param mediaPlayer
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	private void setNotification(MediaPlayer mediaPlayer, int position) {
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher,
				playList.get(position).getName(), System.currentTimeMillis());
		Intent intent = new Intent(this, PlayActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this, playList.get(position).getName(),
				DateUtil.formatTime(mediaPlayer.getDuration()), pendingIntent);
		startForeground(Notification.FLAG_ONGOING_EVENT, notification);
	}

}
