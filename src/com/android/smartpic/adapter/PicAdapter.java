package com.android.smartpic.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.smartpic.R;
import com.android.smartpic.model.PicModel;

public class PicAdapter extends ArrayAdapter<PicModel> {

	public interface ToggleButtonClick {
		public void setDeviceState(int position, int value, boolean state,
				ToggleButton button, ArrayList<PicModel> list);
	}

	private ToggleButtonClick mToggleButtonClick;
	private Context mContext;
	private ArrayList<PicModel> mPicModel;

	public PicAdapter(Context context, ArrayList<PicModel> objects) {
		super(context, R.layout.list_item, objects);
		mContext = context;
		mPicModel = objects;
	}

	@Override
	public int getCount() {
		if (mPicModel == null)
			return 0;
		return mPicModel.size();
	}

	@Override
	public PicModel getItem(int position) {
		if (mPicModel == null)
			return null;
		return mPicModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(mContext);
			convertView = layoutInflator.inflate(R.layout.list_item, null);
			final ViewHolder holder = new ViewHolder();

			holder.itemName = (TextView) convertView
					.findViewById(R.id.itemDescription);

			holder.itemButton = (ToggleButton) convertView
					.findViewById(R.id.itmeToggleButton);
			holder.itemButton
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							PicModel model = (PicModel) holder.itemButton
									.getTag();
							model.setState(buttonView.isChecked());
						}
					});

			convertView.setTag(holder);
			holder.itemButton.setTag(mPicModel.get(position));
		} else {
			((ViewHolder) convertView.getTag()).itemButton.setTag(mPicModel
					.get(position));
		}
		final ViewHolder holder = (ViewHolder) convertView.getTag();

		// Set the name
		holder.itemName.setText(mPicModel.get(position).getName());

		// Set toggleButton state
		holder.itemButton.setChecked(mPicModel.get(position).isState());
		holder.itemButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mToggleButtonClick = (ToggleButtonClick) mContext;
				mToggleButtonClick.setDeviceState(position, mPicModel.get(position)
						.getDeviceValue(), mPicModel.get(position).isState(),
						holder.itemButton, mPicModel);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		protected TextView itemName;
		protected ToggleButton itemButton;
	}

	public void setModel(ArrayList<PicModel> list) {
		mPicModel = list;
		notifyDataSetChanged();
	}
}
