package com.example.musicplayer.model;

/**
 * 艺术家模型
 * @author guoqiang.ma
 *
 */
public class Artist {
	private String artist;
	private long albumId;
	private int songCount;
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
	public int getSongCount() {
		return songCount;
	}
	public void setSongCount(int songCount) {
		this.songCount = songCount;
	}
	
}
