package com.iii360.box.ximalaya;

import java.io.Serializable;
import java.util.Date;

public class Album implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String imageUrl;
	private String title;
	private Date lastUpTrack;
	private int currentPage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Album) {
			Album album = (Album) o;
			return id == album.id;
		}
		return false;
	}

	public Date getLastUpTrack() {
		return lastUpTrack;
	}

	public void setLastUpTrack(Date lastUpTrack) {
		this.lastUpTrack = lastUpTrack;
	}
}
