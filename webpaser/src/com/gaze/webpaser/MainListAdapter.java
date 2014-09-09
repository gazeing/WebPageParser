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
	 public ImageLoader imageLoader; 

	public MainListAdapter(Context context, List<MainListItem> objects) {
		super(context, R.layout.item_mainlist, objects);
		this.objects = objects;
		
		 imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.item_mainlist, null);
			
			viewHolder = new ViewHolder();
			TextView title = (TextView) convertView
					.findViewById(R.id.textView_title);
			TextView description = (TextView) convertView
					.findViewById(R.id.textView_description);
			TextView time = (TextView) convertView
					.findViewById(R.id.textView3_time);
			imgview = (ImageView) convertView.findViewById(R.id.imageView1);
			
			viewHolder.image = imgview;
			viewHolder.title = title;
			viewHolder.time = time;
			viewHolder.text = description;
			
			convertView.setTag(viewHolder);
					

		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		MainListItem m = objects.get(position);
//		TextView title = (TextView) convertView
//				.findViewById(R.id.textView_title);
//		TextView description = (TextView) convertView
//				.findViewById(R.id.textView_description);
//		TextView time = (TextView) convertView
//				.findViewById(R.id.textView3_time);
//		imgview = (ImageView) convertView.findViewById(R.id.imageView1);
//		imgview.setImageResource(R.drawable.ic_launcher);
		
		

//		try {
//			Bitmap cachedImage = GlobalData.m_Imageloader.loadImage(
//					GlobalData.baseUrl + m.imagePath,
//					new ImageLoadedListener() {
//						@Override
//						public void imageLoaded(Bitmap imageBitmap) {
//							try {
//								imageBitmap = Bitmap.createScaledBitmap(
//										imageBitmap, 200, 200, true);
//							} catch (Exception e) {
//								e.printStackTrace();
//								return;
//							}
//							imgview.setImageBitmap(imageBitmap);
//							notifyDataSetChanged();
//						}
//					});
//			if (cachedImage != null) {
//				
//				try {
//					cachedImage = Bitmap.createScaledBitmap(
//							cachedImage, 200, 200, true);
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
////				viewHolder.image.setImageBitmap(cachedImage);
//
//			}
//		} catch (MalformedURLException e) {
//			// MyLog.i("Bad remote image URL: "+ connection.img+
//			// e.getMessage());
//		}
		
        ImageView image = viewHolder.image;
        
        //DisplayImage function from ImageLoader Class
        imageLoader.DisplayImage(GlobalData.baseUrl + m.imagePath, image);

		viewHolder.title.setText(m.getTitle());
		viewHolder.text.setText(Html.fromHtml(m.getIntrotext()));
		viewHolder.time.setText(Util.getFormatTime(m.getTime()));

		return convertView;
	}
	
	static class ViewHolder{
		ImageView image;
		TextView title,time,text;
		
	}

}
