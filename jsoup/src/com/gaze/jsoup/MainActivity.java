package com.gaze.jsoup;

import java.io.IOException;
import java.net.URLDecoder;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		TextView outputTextView;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			outputTextView = (TextView) rootView.findViewById(R.id.text);
			outputTextView.setMovementMethod(new ScrollingMovementMethod());
			
			AsyncTask<Void, Void, Void> read = new AsyncTask<Void, Void, Void>(){

				String t = "";
				@Override
				protected Void doInBackground(Void... params) {
					t = readHtml();
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					outputTextView.setText(t);
					super.onPostExecute(result);
				}
				
				
				
			}.execute();
			
			return rootView;
		}
		
		
		public String readHtml(){
			String t ="";
			try{
		        Document doc = Jsoup.connect("http://www.rebonline.com.au/breaking-news/8095-east-coast-leading-growth").get();

		        Elements elementsHtml = doc.getElementsByAttributeValue("itemprop", "articleBody");

		        for(Element element: elementsHtml)
		        {
//		            Log.i("PARSED ELEMENTS:",URLDecoder.decode(element.text(), HTTP.UTF_8));
		        	
		        	Elements p= element.getElementsByTag("p");
		        	
		        	for (Element x: p) {
		        	
		                 t +=x.text();;
		                 t += "\n";
		                 t += "\n";
		                 
		        	}


		        }
		        
//		        t = t.substring(t.indexOf(" Linkedin ")+12);
//		        t = t.substring(0,t.indexOf(" Facebook "));
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
			return t;
		}
	}
}
