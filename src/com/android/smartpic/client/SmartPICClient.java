package com.android.smartpic.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;

import com.android.smartpic.model.PicModel;

public class SmartPICClient extends AsyncTask<Void, Void, Boolean> {

	public interface ClientListener {
		void taskSuccessful();

		void taskFailed();
	}

	public static final String URL = "http://192.168.0.3:8080/";

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
	private String mCommand;
	private int mDeviceValue;
	private ArrayList<PicModel> mList;
	private int mStatusCode = 0;

	public SmartPICClient(String command, int deviceValue,
			ArrayList<PicModel> list) {
		super();
		mCommand = command;
		mDeviceValue = deviceValue;
		mList = list;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (mCommand.equals(WRITE_TO_COM_PORT)) {
			if (postData(getNumber(mDeviceValue)) != 200)
				return false;
		} else {
			if (getData() != 200)
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

	private int postData(int comPortValue) {
		// Create a new HttpClient and POST Header
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
		httpClient.setParams(httpParameters);
		HttpPost httpPost = new HttpPost(URL);

		try {
			// Add parameters and headers
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			// nameValuePairs.add(new BasicNameValuePair(DEVICE_COMMAND,
			// WRITE_TO_COM_PORT));
			nameValuePairs.add(new BasicNameValuePair(DEVICE_STATE, Integer
					.toString(comPortValue)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpPost.addHeader(DEVICE_COMMAND, WRITE_TO_COM_PORT);
			httpPost.addHeader(CLIENT_ID, CLIENT_SECRET);

			// Execute HTTP Post Request
			HttpResponse httpResponse = httpClient.execute(httpPost);
			mStatusCode = httpResponse.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
		return mStatusCode;
	}

	private int getData() {
		// Create a new HttpClient and GET Header
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
		httpClient.setParams(httpParameters);
		HttpGet httpGet = new HttpGet(URL);

		try {
			httpGet.addHeader(CLIENT_ID, CLIENT_SECRET);
			httpGet.addHeader(DEVICE_COMMAND, READ_FORM_COM_PORT);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			mStatusCode = httpResponse.getStatusLine().getStatusCode();
			// Get the response
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent()));
			String deviceState = bufferedReader.readLine();
			bufferedReader.close();
			setStateDevice(deviceState);
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
		return mStatusCode;
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

	private void setStateDevice(String deviceState) {
		String states = Integer.toBinaryString(Integer.parseInt(deviceState));
		while (states.length() != 8) {
			states = "0" + states;
		}
		int[] values = new int[states.length()];
		for (int i = 0; i < 8; i++) {
			values[i] = Character.digit(states.charAt(i), 8);
			mList.get(7 - i).setState(values[i] == 1);
		}
	}

}
