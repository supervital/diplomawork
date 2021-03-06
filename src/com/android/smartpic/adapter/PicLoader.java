package com.android.smartpic.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.smartpic.R;
import com.android.smartpic.model.PicModel;

public class PicLoader extends AsyncTaskLoader<ArrayList<PicModel>> {

	public static final String NUM_DEVICE = "num_dev";
	public static final int[] deviseValues = { 1, 2, 4, 8, 16, 32, 64, 128 };

	private Context mContext;
	private ArrayList<PicModel> list;

	public PicLoader(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onStartLoading() {
		if (list != null) {
			deliverResult(list);
		}
		if (takeContentChanged() || list == null) {
			forceLoad();
		}
	}

	@Override
	public ArrayList<PicModel> loadInBackground() {
		ArrayList<PicModel> list = new ArrayList<PicModel>();
		String[] names = mContext.getResources().getStringArray(
				R.array.device_name);
		for (int i = 0; i < names.length; i++) {
			PicModel model = new PicModel();
			model.setName(getNameFromSharedPreferences(names[i]));
			model.setState(getStateFromSharedPreferences(names[i] + names[i]));
			model.setDeviceValue(deviseValues[i]);
			list.add(model);
		}
		return list;
	}

	private boolean getStateFromSharedPreferences(String key) {
		return ((SherlockFragmentActivity) mContext).getPreferences(
				Context.MODE_PRIVATE).getBoolean(key, false);
	}

	private String getNameFromSharedPreferences(String key) {
		return ((SherlockFragmentActivity) mContext).getPreferences(
				Context.MODE_PRIVATE).getString(key,
				mContext.getResources().getString(R.string.device) + key);
	}

	@Override
	public void deliverResult(ArrayList<PicModel> data) {
		list = data;
		super.deliverResult(data);
	}

}
