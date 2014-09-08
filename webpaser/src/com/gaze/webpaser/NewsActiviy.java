package com.gaze.webpaser;

import java.net.MalformedURLException;

import com.gaze.webpaser.HtmlPaser.HtmlPaserFinishListner;
import com.gaze.webpaser.ImageThreadLoader.ImageLoadedListener;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsActiviy extends Activity implements HtmlPaserFinishListner {
	
	
	TextView titleView, nameView, timeView, textView;
	ImageView imageView;

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
		timeView.setText(TimeUtil.getFormatTime(m.getTime()));
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
				imageView.setImageBitmap(cachedImage);

			}
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
		
		textView.setText(content.getContentText());
	}

	
}
