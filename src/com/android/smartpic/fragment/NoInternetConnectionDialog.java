package com.android.smartpic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.smartpic.R;

public class NoInternetConnectionDialog extends SherlockFragment {

	public static NoInternetConnectionDialog newInstance() {
		return new NoInternetConnectionDialog();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_no_internet, null);
	}

}
