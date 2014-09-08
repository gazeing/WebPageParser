package com.gaze.webpaser;

import android.annotation.SuppressLint;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public static String getFormatTime(String timestamp_in_string) {

		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy");
		sdf.setTimeZone(tz);
		long dv = Long.valueOf(timestamp_in_string) * 1 + 12 * 60 * 60 * 1000;// its need to be in milisecond
		Date df = new java.util.Date(dv);
		String vv = sdf.format(df);
		return vv;
	}

	public static String convertStringToMd5(String s) {
		// Create MD5 Hash
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(s.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.i("TimeUtil", e.getMessage());
		}
		return "";
	}
}
