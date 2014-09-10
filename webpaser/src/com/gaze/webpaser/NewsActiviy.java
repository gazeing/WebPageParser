package com.gaze.webpaser;

import com.gaze.webpaser.HtmlPaser.HtmlPaserFinishListner;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class NewsActiviy extends Activity implements HtmlPaserFinishListner {
	
	
	TextView titleView, nameView, timeView, textView;
	ImageView imageView;
	WebView webview;
	 public ImageLoader imageLoader; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_news);
		
		titleView = (TextView) findViewById(R.id.textView_title);
		nameView = (TextView) findViewById(R.id.textView_name);
		timeView = (TextView) findViewById(R.id.textView_time);
		textView = (TextView) findViewById(R.id.textView_text);
		imageView = (ImageView) findViewById(R.id.imageView1);
		
		imageLoader = new ImageLoader(this.getApplicationContext());
		handleIntent(getIntent());
		
		
	    gestureDetector = new GestureDetector(this.getApplicationContext(),new SwipeGestureDetector());
	    gestureListener = new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	        	v.performClick();
	            return gestureDetector.onTouchEvent(event);
	        }
	    };
	    
	    RelativeLayout rootview = (RelativeLayout) findViewById(R.id.root);
	    rootview.setOnTouchListener(gestureListener);
		 
	}
	
    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        super.dispatchTouchEvent(e);
        return gestureDetector.onTouchEvent(e);
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
		
       
        
        //DisplayImage function from ImageLoader Class
        imageLoader.DisplayImage(GlobalData.baseUrl + m.imagePath, imageView);
		

		
		
		updateText(m.link);
	}
	
	@Override
	public void onBackPressed() 
	{

		closeActivity();

	}
	
	private void closeActivity()
	{
	    this.finish();
	    overridePendingTransition(0, R.anim.right_slide_out);
	}

	@SuppressWarnings("unused")
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
					Log.i("NewsActiviy","url loaded: "+url);
					webview.setVisibility(View.VISIBLE);
				}
				
			});
			webview.getSettings().setJavaScriptEnabled(true);
			
			webview.loadDataWithBaseURL(GlobalData.baseUrl, content.getHtmlResource(), "text/html; charset=UTF-8", null, null);
			
//			RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
//			root.addView(webview);
		}
		
		
	}
	
	
	
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	
	private class SwipeGestureDetector extends SimpleOnGestureListener {
	    private static final int SWIPE_MIN_DISTANCE = 50;
	    private static final int SWIPE_MAX_OFF_PATH = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	            float velocityY) {
	        try {
//	            Toast t = Toast.makeText(NewsActiviy.this, "Gesture detected", Toast.LENGTH_SHORT);
//	            t.show();
	            float diffAbs = Math.abs(e1.getY() - e2.getY());
	            float diff = e1.getX() - e2.getX();

	            if (diffAbs > SWIPE_MAX_OFF_PATH)
	                return false;

	            // Left swipe
	            if (diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                NewsActiviy.this.onLeftSwipe();
	            } 
	            // Right swipe
	            else if (-diff > SWIPE_MIN_DISTANCE
	                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	            	NewsActiviy.this.onRightSwipe();
	            }
	        } catch (Exception e) {
	            Log.e("Home", "Error on gestures");
	        }
	        return false;
	    }

	}


	public void onLeftSwipe() {
		// TODO Auto-generated method stub
		
	}

	public void onRightSwipe() {
		closeActivity();
		
	}

	
}
