package com.gaze.webpaser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class HtmlPaser {
	
	interface HtmlPaserFinishListner{
        /**
         * Callback when parsing finish.
         * @param HtmlContent content
         */
		public void OnParsingFinish(HtmlContent content);
			
		
		
	}
	
	String htmlPath;
	HtmlPaserFinishListner listener;

	public HtmlPaser(String htmlPath,HtmlPaserFinishListner l) {
		super();
		this.htmlPath = htmlPath;
		this.listener  = l;
		startParse();
	}
	

	private void startParse() {

		new AsyncTask<Void, Void, Void>(){

			HtmlContent h;
			@Override
			protected Void doInBackground(Void... params) {
				h = readHtmlContent();
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {

				listener.OnParsingFinish(h);
				super.onPostExecute(result);
			}
			
			
			
		}.execute();
		
	}


	
	
	public HtmlContent readHtmlContent(){
		String t ="";
		String title = "";
		String author= "";
		String imagePath= "";
		String disqusId = "";
		String time = "";
		try{
	        Document doc = Jsoup.connect("http://www.rebonline.com.au/breaking-news/8095-east-coast-leading-growth").get();

	        Elements elementsHtml = doc.getElementsByAttributeValue("itemprop", "articleBody");

	        for(Element element: elementsHtml)
	        {

	        	
	        	Elements p= element.getElementsByTag("p");
	        	
	        	for (Element x: p) {
	        	
	                 t +=x.text();;
	                 t += "\n";
	                 t += "\n";
	                 
	        	}


	        }
	        
	        

	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		
		HtmlContent hc = new HtmlContent(title, time, author, t, imagePath, disqusId);
		
		return hc;
	}

}
