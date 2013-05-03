package com.android.smartpic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.smartpic.R;

public class AboutFragment extends SherlockFragment {

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.setMenuVisibility(false);
		return inflater.inflate(R.layout.activity_about, null);
	}

}
