package com.gaze.webpaser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainListFragment extends ListFragment implements
		OnItemClickListener, OnItemLongClickListener, OnItemSelectedListener {

	private static final String LOG_TAG = MainListFragment.class
			.getSimpleName();

	String listUrl;
	ArrayList<MainListItem> mlist = new ArrayList<MainListItem>();

	InputStream inputStream = null;
	String result = "";

	MainListAdapter mAdapter;

	static public MainListFragment newInstance(String listUrl) {

		MainListFragment fragment = new MainListFragment();
		Bundle args = new Bundle();
		args.putString(GlobalData.MAIN_LIST_URL, listUrl);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		listUrl = getArguments().getString(GlobalData.MAIN_LIST_URL);
		fillData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mAdapter = new MainListAdapter(getActivity(), mlist);
		this.setListAdapter(mAdapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

	}

	private void fillData() {
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

					// HttpClient is more then less deprecated. Need to change
					// to
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
					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sBuilder = new StringBuilder();

					String line = null;
					while ((line = bReader.readLine()) != null) {
						sBuilder.append(line + "\n");
					}

					inputStream.close();
					result = sBuilder.toString();

					Log.i(LOG_TAG, "finish read stream: " + result);
					if (!isStreamTheTargetJson(result)) {
						result = "";
						sBuilder.delete(0, sBuilder.length() - 1);
					}

					// parse json string here
					if (!result.isEmpty()) {
						if (result.startsWith("<html>"))
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
							if (url != null) {
								// addToQueue(GlobalData.baseUrl+'/'+url);
								addToList(url, introtext, title, images, name,
										time);
							}
						}
					}

				} catch (Exception e) {
					Log.e("StringBuilding & BufferedReader",
							"Error converting result " + e.toString());

				}

				return "";
			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				Log.i(LOG_TAG, msg.toString());
				onFinishDataLoading();

			}

		}.execute();
	}

	protected void onStartDataLoading() {

		if (mAdapter != null)
			mAdapter.clear();
	}

	protected void onFinishDataLoading() {

		mAdapter.notifyDataSetChanged();
		this.getListView().invalidate();
		this.getListView().setOnItemClickListener(this);
		this.getListView().setOnItemLongClickListener(this);
		if (this.listUrl.equals(GlobalData.PRELOAD_LIST_URL)) {
			GlobalData.globle_list = mlist;

			if (getActivity().getIntent() != null) {
				Intent i = getActivity().getIntent();
				if (!i.hasExtra("id"))
					return;
				Log.i("MainActivity", "MainListFragment:  "
						+ i.getExtras().toString());
				int id = i.getIntExtra("id", 0);

				startView(id);
			}

		}

	}

	protected void addToList(String url, String introtext, String title,
			String images, String name, String time) {
		MainListItem item = new MainListItem(title, name, images, time, url,
				introtext);
		mlist.add(item);

	}

	private boolean isStreamTheTargetJson(String result2) {
		if (result2 == null)
			return false;
		if (result2.contains("404 Not Found"))
			return false;

		JSONObject j;
		try {
			j = new JSONObject(result2);
			return (j.has("success") && (j.has("data")));
		} catch (JSONException e) {

			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		startView(position);
	}

	public void startView(int position) {
		Intent i = new Intent(getActivity(), NewsActiviy.class);
		Gson gson = new Gson();
		i.putExtra(GlobalData.MAIN_LIST_ITEM_SERIALABLE,
				gson.toJson(mlist.get(position)));
		getActivity().startActivity(i);
		getActivity().overridePendingTransition(R.anim.right_slide_in,
				R.anim.stay);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		TextView v = (TextView) view.findViewById(R.id.textView_description);
		if (v.getVisibility() == View.GONE)
			v.setVisibility(View.VISIBLE);
		else
			v.setVisibility(View.GONE);

		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TextView v = (TextView) view.findViewById(R.id.textView_description);
		// if(v.getVisibility() == View.GONE)
		// v.setVisibility(View.VISIBLE);
		// else
		// v.setVisibility(View.GONE);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
