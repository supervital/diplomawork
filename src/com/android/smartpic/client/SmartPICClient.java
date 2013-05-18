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

import com.android.smartpic.model.PicModel;

import android.os.AsyncTask;

public class SmartPICClient extends AsyncTask<Void, Void, Boolean> {

	public interface ClientListener {
		void taskSuccessful();

		void taskFailed();
	}

	/* Secret headers */
	public static final String CLIENT_ID = "client_id"; // Key
	public static final String CLIENT_SECRET = "android_app"; // Value

	/* Command headers */
	public static final String DEVICE_COMMAND = "device_command"; // Key
	public static final String READ_FORM_COM_PORT = "read_from"; // Value
	public static final String WRITE_TO_COM_PORT = "write_to"; // Value

	/* State headers */
	public static final String DEVICE_STATE = "device_state"; // Key

	private ClientListener mClientListener;
	private String mUrl;
	private int mDeviceValue;
	private ArrayList<PicModel> mList;

	public SmartPICClient(String url, int deviceValue, ArrayList<PicModel> list) {
		super();
		mUrl = url;
		mDeviceValue = deviceValue;
		mList = list;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (postData(getNumber(mDeviceValue)) != 200)
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

	private int postData(int comPortValue) {
		int statusCode = 0;
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
		httpClient.setParams(httpParameters);
		HttpPost httppost = new HttpPost(mUrl);

		try {
			// Add parameters and headers
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair(DEVICE_COMMAND,
					WRITE_TO_COM_PORT));
			nameValuePairs.add(new BasicNameValuePair(DEVICE_STATE, Integer
					.toString(comPortValue)));
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

	private int getNumber(int value) {
		int outputNumber = 0;
		for (PicModel model : mList) {
			if (model.isState()) {
				outputNumber = outputNumber + model.getDeviceValue();
			}
		}
		return outputNumber;
	}

}
