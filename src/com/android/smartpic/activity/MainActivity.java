package com.android.smartpic.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.smartpic.R;
import com.android.smartpic.adapter.PicAdapter;
import com.android.smartpic.adapter.PicAdapter.ToggleButtonClick;
import com.android.smartpic.client.SmartPICClient;
import com.android.smartpic.client.SmartPICClient.ClientListener;
import com.android.smartpic.fragment.AboutFragment;
import com.android.smartpic.fragment.EditItemFragment;
import com.android.smartpic.fragment.MainFragment;
import com.android.smartpic.fragment.MainFragment.OnContextMenu;
import com.android.smartpic.fragment.NoInternetConnectionDialog;
import com.android.smartpic.model.PicModel;

public class MainActivity extends SherlockFragmentActivity implements
		ToggleButtonClick, OnContextMenu {

	public static final String ABOUT = "about";
	public static final String NO_INTERNET_CONNECTION = "no_internet";
	public static final String MAIN = "main";
	public static final String EDIT = "edit";

	private Context mContext = this;
	private String[] mNamesArray;
	private SherlockFragment mContent;
	private ActionMode mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);
		mNamesArray = getResources().getStringArray(R.array.device_name);
		if (savedInstanceState == null) {
			loadContent();
		}
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
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container_frame, AboutFragment.newInstance(),
							ABOUT).addToBackStack(ABOUT).commit();
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
		} else if (mActionMode != null
				&& getSupportFragmentManager().findFragmentByTag(EDIT) != null) {
			getSupportFragmentManager().popBackStack();
		}
		super.onBackPressed();
	}

	@Override
	public void setDeviceState(final int position, final int value,
			final boolean state, final ToggleButton button,
			ArrayList<PicModel> list) {
		blockUI(true);

		SmartPICClient client = new SmartPICClient(
				SmartPICClient.WRITE_TO_COM_PORT, value, list);
		client.setClientListener(new ClientListener() {

			@Override
			public void taskSuccessful() {
				Editor editor = getPreferences(MODE_PRIVATE).edit();
				editor.putBoolean(mNamesArray[position] + mNamesArray[position], state);
				editor.commit();
				blockUI(false);
			}

			@Override
			public void taskFailed() {
				button.setChecked(!state);
				Toast.makeText(mContext, getString(R.string.msg_server_fail),
						Toast.LENGTH_LONG).show();
				blockUI(false);
			}
		});
		client.execute();
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.retrieveButton:
			loadContent();
			break;

		default:
			break;
		}
	}

	private void loadContent() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		String tag;
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			mContent = MainFragment.newInstance();
			tag = MAIN;
		} else {
			mContent = NoInternetConnectionDialog.newInstance();
			tag = NO_INTERNET_CONNECTION;
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container_frame, mContent, tag).commit();
	}

	private void showContent() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportFragmentManager().popBackStack();
		invalidateOptionsMenu();
	}

	@Override
	public void item(int position, String defaultName, PicAdapter adapter,
			PicModel model) {
		if (mActionMode == null) {
			mActionMode = startActionMode(new ContextMenu(defaultName,
					mNamesArray[position], adapter, model));
		} else {
			mActionMode.finish();
		}

	}

	private class ContextMenu implements ActionMode.Callback {

		private String editAbleText;
		private String key;
		private PicAdapter adapter;
		private PicModel model;

		public ContextMenu(String editAbleText, String key, PicAdapter adapter,
				PicModel model) {
			super();
			this.editAbleText = editAbleText;
			this.key = key;
			this.adapter = adapter;
			this.model = model;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.context_menu, menu);
			mode.setTitle(editAbleText);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_rename:
				mode.setTitle("");
				item.setVisible(false);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(
								R.id.container_frame,
								EditItemFragment.newInstance(editAbleText, key,
										model), EDIT).addToBackStack(EDIT)
						.commit();
				break;
			default:
				break;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			if (getSupportFragmentManager().findFragmentByTag(EDIT) != null)
				getSupportFragmentManager().popBackStack();
			adapter.notifyDataSetChanged();
		}

	}

	private void blockUI(boolean block) {
		if ((MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN) == null) {
			return;
		}
		((MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN))
				.showLoadingIndicator(block);
	}
}
