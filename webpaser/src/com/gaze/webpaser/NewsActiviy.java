package com.gaze.webpaser;

import java.net.MalformedURLException;

import com.gaze.webpaser.HtmlPaser.HtmlPaserFinishListner;
import com.gaze.webpaser.ImageThreadLoader.ImageLoadedListener;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class NewsActiviy extends Activity implements HtmlPaserFinishListner {
	
	
	TextView titleView, nameView, timeView, textView;
	ImageView imageView;
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_news);
		
		titleView = (TextView) findViewById(R.id.textView_title);
		nameView = (TextView) findViewById(R.id.textView_name);
		timeView = (TextView) findViewById(R.id.textView_time);
		textView = (TextView) findViewById(R.id.textView_text);
		imageView = (ImageView) findViewById(R.id.imageView1);
		
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {

		if (intent == null)
			return;
		
		String json = intent.getStringExtra(GlobalData.MAIN_LIST_ITEM_SERIALABLE);
		Gson gson = new Gson();
		MainListItem m = gson.fromJson(json, MainListItem.class);   
		if (m==null)
			return;
		
		titleView.setText(m.getTitle());
		timeView.setText(Util.getFormatTime(m.getTime()));
		nameView.setText(m.getAuthor());
		textView.setText(Html.fromHtml(m.getIntrotext()));
		
		try {
			Bitmap cachedImage = GlobalData.m_Imageloader.loadImage(
					GlobalData.baseUrl + m.imagePath,
					new ImageLoadedListener() {
						@Override
						public void imageLoaded(Bitmap imageBitmap) {
							try {
								imageBitmap = Bitmap.createScaledBitmap(
										imageBitmap, 300, 300, true);
							} catch (Exception e) {
								// MyLog.i(e);
								return;
							}
							imageView.setImageBitmap(imageBitmap);
							
						}
					});
			if (cachedImage != null) {
				try {
					cachedImage = Bitmap.createScaledBitmap(
							cachedImage, 300, 300, true);
				} catch (Exception e) {
					// MyLog.i(e);
					return;
				}
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(cachedImage);

			}
			else imageView.setVisibility(View.GONE);
		} catch (MalformedURLException e) {
			// MyLog.i("Bad remote image URL: "+ connection.img+
			// e.getMessage());
		}
		
		
		updateText(m.link);
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition(0, R.anim.right_slide_out);
	}

	private void updateText(String link) {
		HtmlPaser p = new HtmlPaser(GlobalData.baseUrl +link, this);
		
	}

	@Override
	public void OnParsingFinish(HtmlContent content) {
		if (content == null)
			return;
		
		textView.setText(Html.fromHtml(content.getContentText()));
		
		Log.i("NewsActiviy","load : "+content.getHtmlResource());
		if(content.getHtmlResource().length()>0){
//			RelativeLayout.LayoutParams webview_LayoutParams = new RelativeLayout.LayoutParams(
//					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//			webview_LayoutParams.addRule(RelativeLayout.ABOVE,
//					R.id.textView_text);
//			WebView webview = new WebView(this);
//			webview.setLayoutParams(webview_LayoutParams);
			webview = (WebView) findViewById(R.id.webView1);
			webview.setWebViewClient(new WebViewClient(){

				@Override
				public void onPageFinished(WebView view, String url) {
					// TODO Auto-generated method stub
					super.onPageFinished(view, url);
					Log.i("NewsActiviy",url);
					webview.setVisibility(View.VISIBLE);
				}
				
			});
			webview.getSettings().setJavaScriptEnabled(true);
			
			webview.loadData(content.getHtmlResource(), "text/html; charset=UTF-8", null);
			
//			RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
//			root.addView(webview);
		}
		
		
	}

	
}
