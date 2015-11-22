package com.example.musicplayer.model;

/**
 * 专辑模型
 * @author guoqiang.ma
 *
 */
public class Album {
	private String album;
	private String artist;
	private long albumId;
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public long getAlbumId() {
		return albumId;
	}
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	
}