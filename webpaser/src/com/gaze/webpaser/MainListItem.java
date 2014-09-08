package com.gaze.webpaser;

public class MainListItem {
	
	String title;
	String author;
	String imagePath;
	String time;
	String link;
	String introtext;
	public MainListItem(String title, String author, String imagePath,
			String time, String link, String introtext) {
		super();
		this.title = title;
		this.author = author;
		this.imagePath = imagePath;
		this.time = time;
		this.link = link;
		this.introtext = introtext;
	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
	public String getImagePath() {
		return imagePath;
	}
	public String getTime() {
		return time;
	}
	public String getLink() {
		return link;
	}
	public String getIntrotext() {
		return introtext;
	}
	
	

}
