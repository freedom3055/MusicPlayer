package com.example.musicplayer.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MusicApplication;
import com.example.musicplayer.adapter.base.DefaultAdapter;
import com.example.musicplayer.adapter.base.ViewHolder;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.utils.DateUtil;
import com.example.musicplayer.utils.MediaUtil;

/**
 * 歌曲列表adapter
 * @author guoqiang.ma
 *
 */
public class SongsAdapter extends DefaultAdapter<Music> {

	private Bitmap defaultAlbumIcon;

	public SongsAdapter(Context context, List<Music> objects) {
		super(context, objects);
		defaultAlbumIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_mp_song_list);
	}

	@Override
	public int getItemLayoutId() {
		return R.layout.item_song;
	}

	@Override
	public void convert(final ViewHolder viewHolder, final Music item) {
		viewHolder.getTextView(R.id.textView_title).setText(item.getName());
		viewHolder.getTextView(R.id.textView_artist).setText(item.getArtist());
		viewHolder.getTextView(R.id.textView_duration).setText(
				DateUtil.formatTime(item.getDuration()));
		
		//设置相关图片
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				return MediaUtil.getCachedArtwork(MusicApplication.getContext(),
						item.getAlbumId(), defaultAlbumIcon);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				viewHolder.getImageView(R.id.imageView).setImageBitmap(result);
				super.onPostExecute(result);
			}
		}.execute();
	}

}
