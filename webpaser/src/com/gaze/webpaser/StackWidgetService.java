/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gaze.webpaser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gaze.webpaser.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int mCount = 5;
//    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    
    private static final String LOG_TAG = StackRemoteViewsFactory.class.getSimpleName();
	String listUrl;
	ArrayList<MainListItem> mlist = new ArrayList<MainListItem>();
	
	InputStream inputStream = null;
	String result = "";
	public ImageLoader imageLoader; 

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
//        for (int i = 0; i < mCount; i++) {
//            mWidgetItems.add(new WidgetItem(i + "!"));
//        }

        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        try {
//            Thread.sleep(3000);
        	 imageLoader = new ImageLoader(mContext);
        	listUrl = GlobalData.PRELOAD_LIST_URL;
        	fillData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void fillData(){
		new AsyncTask<String, Void, String>() {
			
			
			@Override
			protected void onPreExecute() {
				
				super.onPreExecute();
				onStartDataLoading();
			}



			@Override
			protected String doInBackground(String... params) {
				
				ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

				try {
					// Set up HTTP post

					// HttpClient is more then less deprecated. Need to change to
					// URLConnection
					HttpClient httpClient = new DefaultHttpClient();

					HttpPost httpPost = new HttpPost(listUrl);
					httpPost.setEntity(new UrlEncodedFormEntity(param));
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();

					Log.i(LOG_TAG, "finish http");

					// Read content & Log
					inputStream = httpEntity.getContent();
				} catch (UnsupportedEncodingException e1) {
					Log.e("UnsupportedEncodingException", e1.toString());
					e1.printStackTrace();
				} catch (ClientProtocolException e2) {
					Log.e("ClientProtocolException", e2.toString());
					e2.printStackTrace();
				} catch (IllegalStateException e3) {
					Log.e("IllegalStateException", e3.toString());
					e3.printStackTrace();
				} catch (IOException e4) {
					Log.e("IOException", e4.toString());
					e4.printStackTrace();
				}
				// Convert response to string using String Builder
				try {
					BufferedReader bReader = new BufferedReader(new InputStreamReader(
							inputStream, "iso-8859-1"), 8);
					StringBuilder sBuilder = new StringBuilder();

					String line = null;
					while ((line = bReader.readLine()) != null) {
						sBuilder.append(line + "\n");
					}

					inputStream.close();
					result = sBuilder.toString();

					Log.i(LOG_TAG, "finish read stream: "+result);
					if(!isStreamTheTargetJson(result)){
						result = "";
						sBuilder.delete(0, sBuilder.length()-1);
					}
					
					//parse json string here
					if (!result.isEmpty()) {
						if(result.startsWith("<html>"))
							return "";
						
						JSONObject titleJson = new JSONObject(result);
						JSONArray datajson = titleJson.getJSONArray("data");
						JSONArray urlQueue = datajson.getJSONArray(0);
						
						
						for (int i = 0; i < urlQueue.length(); i++) {
							JSONObject item = urlQueue.getJSONObject(i);
							String url = item.getString("link");
							String introtext = item.getString("introtext");
							String title = item.getString("title");
							String images = item.getString("images");
							String name = item.getString("name");
							String time = item.getString("publish_up");
							if(url!=null){
//								addToQueue(GlobalData.baseUrl+'/'+url);
								addToList(url,introtext,title,images,name,time);
							}
						}
					}
					
				} catch (Exception e) {
					Log.e("StringBuilding & BufferedReader", "Error converting result "
							+ e.toString());

				}

				return "";
			}
			
			
			
			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
//				Log.i(LOG_TAG, msg.toString());
				onFinishDataLoading();
				
			}

		}.execute();
	}
	
    
	protected void addToList(String url, String introtext, String title,
			String images, String name, String time) {
		MainListItem item = new MainListItem(title, name, images, time, url,introtext);
		mlist.add(item);
		
	}
    
	private boolean isStreamTheTargetJson(String result2) {
		if(result2==null)
			return false;
		if(result2.contains("404 Not Found"))
			return false;
		
		JSONObject j;
		try {
			j = new JSONObject(result2);
			return (j.has("success")&&(j.has("data")));
		} catch (JSONException e) {
			
			e.printStackTrace();
			return false;
		}
	}
    
    protected void onFinishDataLoading() {
		// TODO Auto-generated method stub
    	AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.textView_title);
	}

	protected void onStartDataLoading() {
		// TODO Auto-generated method stub
		
	}



	public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
//        mWidgetItems.clear();
        mlist.clear();
    }

    public int getCount() {
        return mlist.size();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
//        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
//        rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).text);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
        rv.setTextViewText(R.id.textView_title, mlist.get(position).title);
        rv.setTextViewText(R.id.textView_time, mlist.get(position).time);

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget1, fillInIntent);

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            System.out.println("Loading view " + position);
//            Thread.sleep(500);
//           imageLoader
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the remote views object.
        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }
}