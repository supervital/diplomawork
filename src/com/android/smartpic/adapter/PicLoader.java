package com.android.smartpic.adapter;

import java.util.ArrayList;

import com.android.smartpic.R;
import com.android.smartpic.model.PicModel;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

public class PicLoader extends AsyncTaskLoader<ArrayList<PicModel>> {

	public static final String DEFAULT_NAME = "Default device";
	public static final String NUM_DEVICE = "num_dev";

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
		for (int i = 0; i < getNumberDevice(); i++) {
			PicModel model = new PicModel();
			model.setName(getNameFromSharedPreferences(names[i]));
			model.setState(getStateFromSharedPreferences(names[i]));
			list.add(model);
		}
		return list;
	}

	private boolean getStateFromSharedPreferences(String key) {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(key, false);
	}

	private String getNameFromSharedPreferences(String key) {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString(key, DEFAULT_NAME);
	}

	private int getNumberDevice() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
				NUM_DEVICE, 0);
	}

	@Override
	public void deliverResult(ArrayList<PicModel> data) {
		list = data;
		super.deliverResult(data);
	}

}
