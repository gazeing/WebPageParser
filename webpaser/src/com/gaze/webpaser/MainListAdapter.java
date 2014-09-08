package com.gaze.webpaser;

import java.net.MalformedURLException;
import java.util.List;

import com.gaze.webpaser.ImageThreadLoader.ImageLoadedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAdapter extends ArrayAdapter<MainListItem> {

	List<MainListItem> objects;
	ImageView imgview;

	public MainListAdapter(Context context, List<MainListItem> objects) {
		super(context, R.layout.item_mainlist, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.item_mainlist, null);

		}
		MainListItem m = objects.get(position);
		TextView title = (TextView) convertView
				.findViewById(R.id.textView_title);
		TextView description = (TextView) convertView
				.findViewById(R.id.textView_description);
		TextView time = (TextView) convertView
				.findViewById(R.id.textView3_time);
		imgview = (ImageView) convertView.findViewById(R.id.imageView1);

		try {
			Bitmap cachedImage = GlobalData.m_Imageloader.loadImage(
					GlobalData.baseUrl + m.imagePath,
					new ImageLoadedListener() {
						@Override
						public void imageLoaded(Bitmap imageBitmap) {
							try {
								imageBitmap = Bitmap.createScaledBitmap(
										imageBitmap, 100, 100, true);
							} catch (Exception e) {
								// MyLog.i(e);
								return;
							}
							imgview.setImageBitmap(imageBitmap);
							notifyDataSetChanged();
						}
					});
			if (cachedImage != null) {
				imgview.setImageBitmap(cachedImage);

			}
		} catch (MalformedURLException e) {
			// MyLog.i("Bad remote image URL: "+ connection.img+
			// e.getMessage());
		}

		title.setText(m.getTitle());
		description.setText(Html.fromHtml(m.getIntrotext()));
		time.setText(TimeUtil.getFormatTime(m.getTime()));

		return convertView;
	}

}
