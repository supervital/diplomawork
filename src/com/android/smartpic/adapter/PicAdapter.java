package com.android.smartpic.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.smartpic.R;
import com.android.smartpic.model.PicModel;

public class PicAdapter extends BaseAdapter {

	public interface onToggleButtonClick {
		public void setDeviceState(int position, boolean state,
				ToggleButton button);
	}

	private onToggleButtonClick mToggleButtonClick;
	private Context mContext;
	private ArrayList<PicModel> mPicModel;

	public PicAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mPicModel == null)
			return 0;
		return mPicModel.size();
	}

	@Override
	public Object getItem(int position) {
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
				mToggleButtonClick = (onToggleButtonClick) mContext;
				mToggleButtonClick.setDeviceState(position,
						mPicModel.get(position).isState(), holder.itemButton);
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
