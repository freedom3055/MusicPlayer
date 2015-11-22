package com.example.musicplayer.model;

import java.io.Serializable;

import android.provider.MediaStore;

import com.example.musicplayer.db.base.DBHelper;
import com.example.musicplayer.db.base.annotation.Column;
import com.example.musicplayer.db.base.annotation.ID;
import com.example.musicplayer.db.base.annotation.TableName;

/**
 * 歌曲模型
 * @author guoqiang.ma
 *
 */
@TableName(DBHelper.TABLE_NAME)
public class Music implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ID(autoincrement = true)
	@Column(MediaStore.Audio.Media._ID)
	private long id;
	@Column(MediaStore.Audio.Media.ALBUM_ID)
	private long albumId;
	@Column(MediaStore.Audio.Media.ARTIST)
	private String artist;
	@Column(MediaStore.Audio.Media.DURATION)
	private long duration;
	@Column(MediaStore.Audio.Media.DISPLAY_NAME)
	private String name;
	@Column(MediaStore.Audio.Media.DATA)
	private String path;
	@Column(MediaStore.Audio.Media.ALBUM)
	private String album;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}