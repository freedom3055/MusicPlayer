package com.example.musicplayer.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MusicApplication;
import com.example.musicplayer.adapter.base.DefaultAdapter;
import com.example.musicplayer.adapter.base.ViewHolder;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.utils.MediaUtil;

/**
 * 艺术家列表adapter
 * @author guoqiang.ma
 *
 */
public class ArtistsAdapter extends DefaultAdapter<Artist> {

	private Bitmap defaultAlbumIcon;

	public ArtistsAdapter(Context context, List<Artist> objects) {
		super(context, objects);
		defaultAlbumIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_mp_artist_list);
	}

	@Override
	public int getItemLayoutId() {
		return R.layout.item_song;
	}

	@Override
	public void convert(final ViewHolder viewHolder, final Artist item) {
		if (MediaStore.UNKNOWN_STRING.equals(item.getArtist())) {
			viewHolder.getTextView(R.id.textView_title).setText("未知艺术家");
		} else {
			viewHolder.getTextView(R.id.textView_title).setText(
					item.getArtist());
		}
		viewHolder.getTextView(R.id.textView_artist).setText(
				item.getSongCount() + "首歌");
		viewHolder.getTextView(R.id.textView_duration).setVisibility(View.GONE);
		viewHolder.getImageView(R.id.imageView)
				.setImageBitmap(defaultAlbumIcon);
		
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