package com.android.smartpic.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;

public class SmartPICClient extends AsyncTask<Void, Void, Boolean> {

	public interface ClientListener {
		void taskSuccessful();

		void taskFailed();
	}

	public static final String DEVICE_ID = "device_id";
	public static final String DEVICE_STATE = "device_state";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "android_app";

	private ClientListener mClientListener;
	private String mUrl;
	private String mDeviceId;
	private String mDeviceState;

	public SmartPICClient(String url, String deviceId, String deviceState) {
		super();
		mUrl = url;
		mDeviceId = deviceId;
		mDeviceState = deviceState;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (postData() != 200)
			return false;
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

	private int postData() {
		int statusCode = 0;
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
		httpClient.setParams(httpParameters);
		HttpPost httppost = new HttpPost(mUrl);

		try {
			// Add parameters and headers
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair(DEVICE_ID, mDeviceId));
			nameValuePairs.add(new BasicNameValuePair(DEVICE_STATE,
					mDeviceState));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.addHeader(CLIENT_ID, CLIENT_SECRET);

			// Execute HTTP Post Request
			HttpResponse httpResponse = httpClient.execute(httppost);
			statusCode = httpResponse.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusCode;
	}

}
