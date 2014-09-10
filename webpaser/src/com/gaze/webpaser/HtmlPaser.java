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
		String htmlResouce = "";
		try{
	        Document doc = Jsoup.connect(htmlPath).get();

	        Elements elementsHtml = doc.getElementsByAttributeValue("itemprop", "articleBody");

	        for(Element element: elementsHtml)
	        {

	        	
	        	Elements p= element.getElementsByTag("p");
	        	
	        	for (Element x: p) {
	        		
	        		String html = x.html();
	        		if(html.contains("iframe")||html.contains("img")){
	        			
	        			htmlResouce += "<p>";
	        			htmlResouce+=html;
	        			htmlResouce+= "</p>";
	        			
	        		}else{
	        		
	        		t += "<p>";
	        	
	                 t +=html;
	        		
	        		t += "</p>";
	        		}

	                 
	        	}


	        }
	        
	        
	        //for disqus
	        doc.getElementsByAttributeValue("id", "jwDisqusBackToTop").remove();
	        
	        Elements elementsDisqus = doc.getElementsByAttributeValue("class", "jwDisqusForm");
	
	        
	       
	        disqusId = elementsDisqus.html();
	        

	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		
		HtmlContent hc = new HtmlContent(title, time, author, t, imagePath, disqusId,htmlResouce);
		
		return hc;
	}

}
