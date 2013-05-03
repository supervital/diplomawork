package com.android.smartpic.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.smartpic.R;
import com.android.smartpic.adapter.PicAdapter;
import com.android.smartpic.adapter.PicLoader;
import com.android.smartpic.adapter.PicAdapter.onToggleButtonClick;
import com.android.smartpic.app.Constans;
import com.android.smartpic.client.SmartPICClient;
import com.android.smartpic.client.SmartPICClient.ClientListener;
import com.android.smartpic.fragment.AboutFragment;
import com.android.smartpic.fragment.NoInternetConnectionDialog;
import com.android.smartpic.model.PicModel;

public class MainActivity extends SherlockFragmentActivity implements
		onToggleButtonClick, LoaderManager.LoaderCallbacks<ArrayList<PicModel>> {

	private ListView mItemList;
	private Context mContext = this;
	private String[] mNamesArray;
	private ProgressBar mProgressDialog;
	private PicAdapter mAdapter;

	public static String NO_INTERNET_CONNECTION = "no_internet_connection";
	public static String ABOUT = "about";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNamesArray = getResources().getStringArray(R.array.device_name);
		mAdapter = new PicAdapter(mContext);
		mItemList = (ListView) findViewById(R.id.controllPanel);
		mItemList.setEmptyView(findViewById(R.id.emptyTextView));
		mItemList.addHeaderView(getLayoutInflater().inflate(
				R.layout.list_header, null));
		mItemList.setAdapter(mAdapter);
		mProgressDialog = (ProgressBar) findViewById(R.id.progressBar);

		checkInternetState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			item.setVisible(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			mItemList.setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.frgmCont, AboutFragment.newInstance(), ABOUT)
					.addToBackStack(NO_INTERNET_CONNECTION).commit();
			return true;

		case android.R.id.home:
			showContent();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().findFragmentByTag(ABOUT) != null) {
			showContent();
			return;
		} else if (getSupportFragmentManager().findFragmentByTag(
				NO_INTERNET_CONNECTION) != null) {
			finish();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void setDeviceState(final int position, final boolean state,
			final ToggleButton button) {
		showLoadingIndicator(true);

		int picState;
		if (state)
			picState = 1;
		picState = 0;

		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put(Constans.KEY_DEVICE, position);
		params.put(Constans.KEY_STATE, picState);

		SmartPICClient client = new SmartPICClient(Constans.URL, params);
		client.setClientListener(new ClientListener() {

			@Override
			public void taskSuccessful() {
				Editor editor = getPreferences(MODE_PRIVATE).edit();
				editor.putBoolean(mNamesArray[position], state);
				editor.commit();
				showLoadingIndicator(false);
			}

			@Override
			public void taskFailed() {
				showLoadingIndicator(false);
				button.setChecked(false);
				Toast.makeText(mContext, getString(R.string.msg_server_fail),
						Toast.LENGTH_SHORT).show();
			}
		});
		client.execute();
	}

	@Override
	public Loader<ArrayList<PicModel>> onCreateLoader(int arg0, Bundle arg1) {
		return new PicLoader(mContext);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<PicModel>> loader,
			ArrayList<PicModel> list) {
		if (list.size() == 8)
			mItemList.removeHeaderView(getLayoutInflater().inflate(
					R.layout.list_header, null));
		mAdapter.setModel(list);
		showLoadingIndicator(false);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<PicModel>> arg0) {
		mAdapter.setModel(null);
		showLoadingIndicator(false);
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.retrieveButton:
			checkInternetState();
			break;

		default:
			break;
		}
	}

	private void checkInternetState() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			if (getSupportFragmentManager().findFragmentByTag(
					NO_INTERNET_CONNECTION) != null)
				getSupportFragmentManager().popBackStack();

			showLoadingIndicator(true);
			getSupportLoaderManager().initLoader(1, null, this);

		} else {
			if (getSupportFragmentManager().findFragmentByTag(
					NO_INTERNET_CONNECTION) == null)
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.frgmCont,
								NoInternetConnectionDialog.newInstance(),
								NO_INTERNET_CONNECTION)
						.addToBackStack(NO_INTERNET_CONNECTION).commit();
		}
	}

	private void showLoadingIndicator(boolean contentLoaded) {
		if (contentLoaded) {
			mItemList.setVisibility(View.INVISIBLE);
			mProgressDialog.setVisibility(View.VISIBLE);
		} else {
			mItemList.setVisibility(View.VISIBLE);
			mProgressDialog.setVisibility(View.GONE);
		}
	}

	private void showContent() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportFragmentManager().popBackStack();
		invalidateOptionsMenu();
		mItemList.setVisibility(View.VISIBLE);
	}
}
