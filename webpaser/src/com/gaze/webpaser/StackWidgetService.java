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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	public static final String LOG_TAG = "widget_info";
	
    private static final int mCount = 10;
    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    ArrayList<MainListItem> mlist = new ArrayList<MainListItem>();
    
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache= new FileCache(mContext);

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
    	
    	Log.i(LOG_TAG,"onCreate");
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        for (int i = 0; i < mCount; i++) {
            mWidgetItems.add(new WidgetItem(i + "!"));
        }

        if(GlobalData.globle_list.size()>0){
        	
        	mlist = GlobalData.globle_list;
        }
        
        
        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    
    private void getDatafromNetwork(){
    	
    	InputStream inputStream = null;
    	String result = "";
    	String listUrl = GlobalData.PRELOAD_LIST_URL;
    	
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
					return ;
				
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
//						addToQueue(GlobalData.baseUrl+'/'+url);
						addToList(url,introtext,title,images,name,time);
					}
				}
			}
			
		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result "
					+ e.toString());

		}
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
	protected void addToList(String url, String introtext, String title,
			String images, String name, String time) {
		MainListItem item = new MainListItem(title, name, images, time, url,introtext);
		mlist.add(item);
		
	}
    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear();
    }

    public int getCount() {
//        return mCount;
    	return mlist.size();
    }

    public RemoteViews getViewAt(int position) {
    	
    	Log.i(LOG_TAG,"getViewAt " + position);
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
        rv.setTextViewText(R.id.textView_title, mlist.get(position).getTitle());
        String time = Util.getFormatTime(mlist.get(position).getTime());
        rv.setTextViewText(R.id.textView3_time,time);

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
//        try {
//            System.out.println("Loading view " + position);
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Bitmap bitmap = getBitmap(GlobalData.baseUrl+mlist.get(position).getImagePath());
        if (bitmap != null)
        	rv.setImageViewBitmap(R.id.imageView1, bitmap);
        
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
    
    
    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);
         
        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
         
        // Download image file from web
        try {
             
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
             
            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(f);
             
            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            Util.CopyStream(is, os);
             
            os.close();
            conn.disconnect();
             
            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(f);
             
            return bitmap;
             
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
    

    
 
    //Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
         
        try {
             
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
             
          //Find the correct scale value. It should be the power of 2.
          
            // Set width/height of recreated image
            final int REQUIRED_SIZE=150;
             
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2 < REQUIRED_SIZE || height_tmp/2 < REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
             
            //decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
             
        } catch (FileNotFoundException e) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}