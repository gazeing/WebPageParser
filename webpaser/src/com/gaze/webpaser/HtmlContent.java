package com.gaze.webpaser;

public class HtmlContent {
	
	String title;
	String time;
	String author;
	String contentText;
	String imagePath;
	String disqusId;
	String htmlResource;

	
	public HtmlContent(String title, String time, String author,
			String contentText, String imagePath, String disqusId,String htmlResource) {
		super();
		this.title = title;
		this.time = time;
		this.author = author;
		this.contentText = contentText;
		this.imagePath = imagePath;
		this.disqusId = disqusId;
		this.htmlResource = htmlResource;
	}

	public String getTitle() {
		return title;
	}

	public String getTime() {
		return time;
	}

	public String getContentText() {
		return contentText;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getDisqusId() {
		return disqusId;
	}

	public String getAuthor() {
		return author;
	}

	public String getHtmlResource() {
		return htmlResource;
	}
	
	
	
	

}
