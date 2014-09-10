package com.gaze.webpaser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class CommentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_comment);

		handleIntent(getIntent());

		getActionBar().setTitle("DISQUS");

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void handleIntent(Intent intent) {

		if (intent == null)
			return;

		String disqus = intent.getStringExtra(GlobalData.DISQUS_COMMENT);

		if (disqus == null)
			return;

		WebView webview = (WebView) findViewById(R.id.webView2);
		
		
		// create popup view container layout
		RelativeLayout browserLayout = new RelativeLayout(this);
		browserLayout.setBackgroundColor(android.graphics.Color
				.parseColor("#282A79"));
		RelativeLayout.LayoutParams browser_LayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		browser_LayoutParams.addRule(RelativeLayout.ALIGN_TOP,
				R.id.webView2);
		browserLayout.setLayoutParams(browser_LayoutParams);
		
		RelativeLayout root = (RelativeLayout) findViewById(R.id.relative1);
		root.addView(browserLayout);
		browserLayout.setVisibility(View.INVISIBLE);
		
		DisqusWebviewChromeClient client = new DisqusWebviewChromeClient(browserLayout, this);
		webview.setWebChromeClient(client);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setSupportMultipleWindows(true);

		webview.loadDataWithBaseURL(GlobalData.baseUrl, disqus,
				"text/html; charset=UTF-8", null, null);
		
		


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			closeActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		closeActivity();

	}

	private void closeActivity() {
		this.finish();
		overridePendingTransition(0, R.anim.right_slide_out);
	}
}
