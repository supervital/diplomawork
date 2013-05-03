package com.android.smartpic.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.os.AsyncTask;

public class SmartPICClient extends AsyncTask<Void, Void, Boolean> {

	public interface ClientListener {
		void taskSuccessful();

		void taskFailed();
	}

	private ClientListener mClientListener;
	private String mUrl;
	private Map<String, Integer> mParams;

	public SmartPICClient(String mUrl, Map<String, Integer> mParams) {
		super();
		this.mUrl = mUrl;
		this.mParams = mParams;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		URL url = null;
		try {
			url = new URL(mUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + mUrl);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, Integer>> iterator = mParams.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			mClientListener.taskSuccessful();
		} else {
			mClientListener.taskFailed();
		}
		super.onPostExecute(result);
	}

	public void setClientListener(ClientListener clientListener) {
		mClientListener = clientListener;
	}

}
