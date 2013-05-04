package com.android.smartpic.fragment;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.smartpic.R;
import com.android.smartpic.model.PicModel;

public class EditItemFragment extends SherlockFragment {

	public static final String PRIVIOS_NAME = "privios";
	public static final String MODEL = "model";
	public static final String KEY = "key";

	private EditText mEditText;
	private PicModel mModel;
	private String mPriviosName;
	private String mKey;

	public static EditItemFragment newInstance(String priviosName, String key,
			PicModel model) {
		EditItemFragment frag = new EditItemFragment();
		Bundle arg = new Bundle();
		arg.putString(PRIVIOS_NAME, priviosName);
		arg.putString(KEY, key);
		arg.putParcelable(MODEL, model);
		frag.setArguments(arg);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_edit, null);
		mModel = this.getArguments().getParcelable(MODEL);
		mPriviosName = this.getArguments().getString(PRIVIOS_NAME);
		mKey = this.getArguments().getString(KEY);
		mEditText = (EditText) view.findViewById(R.id.editItem);
		mEditText.setHint(mPriviosName);

		return view;
	}

	@Override
	public void onDestroyView() {
		String newName = mEditText.getText().toString();
		if (!newName.equals(mPriviosName) && newName.length() >= 1) {
			mModel.setName(newName);
			Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE)
					.edit();
			edit.putString(mKey + mKey, newName);
			edit.commit();
		}
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		super.onDestroyView();
	}

}
