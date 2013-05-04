package com.android.smartpic.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.smartpic.R;
import com.android.smartpic.adapter.PicAdapter;
import com.android.smartpic.adapter.PicLoader;
import com.android.smartpic.model.PicModel;

public class MainFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<ArrayList<PicModel>> {

	public interface OnContextMenu {
		public void item(int position, String defaultName, PicAdapter adapter,
				PicModel model);
	}

	private OnContextMenu mOnContextMenu;
	private ListView mItemList;
	private ProgressBar mProgressDialog;
	private PicAdapter mAdapter;
	private ArrayList<PicModel> mList;

	public static MainFragment newInstance() {
		MainFragment frag = new MainFragment();
		return frag;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getLoaderManager().initLoader(1, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, null);
		this.setRetainInstance(true);
		mItemList = (ListView) view.findViewById(R.id.controllPanel);
		mProgressDialog = (ProgressBar) view.findViewById(R.id.progressBar);
		showLoadingIndicator(true);
		mAdapter = new PicAdapter(getActivity(), null);
		mItemList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mItemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				mOnContextMenu = (OnContextMenu) getActivity();
				mOnContextMenu.item(position, mList.get(position).getName(),
						mAdapter, mList.get(position));
			}
		});
		mItemList.setAdapter(mAdapter);
		return view;
	}

	@Override
	public Loader<ArrayList<PicModel>> onCreateLoader(int arg0, Bundle arg1) {
		return new PicLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<PicModel>> loader,
			ArrayList<PicModel> list) {
		mList = list;
		mAdapter.setModel(list);
		showLoadingIndicator(false);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<PicModel>> loader) {
		mAdapter.setModel(null);
		showLoadingIndicator(false);
	}

	public void showLoadingIndicator(boolean contentLoaded) {
		if (contentLoaded) {
			mItemList.setVisibility(View.INVISIBLE);
			mProgressDialog.setVisibility(View.VISIBLE);
		} else {
			mItemList.setVisibility(View.VISIBLE);
			mProgressDialog.setVisibility(View.GONE);
		}
	}

}
