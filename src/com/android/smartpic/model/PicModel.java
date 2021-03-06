package com.android.smartpic.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PicModel implements Parcelable {

	private String name;
	private boolean state;
	private int deviceValue;

	public PicModel() {
	}

	public PicModel(Parcel source) {
		name = source.readString();
		state = source.readByte() == 1;
		deviceValue = source.readInt();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getDeviceValue() {
		return deviceValue;
	}

	public void setDeviceValue(int deviceValue) {
		this.deviceValue = deviceValue;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeByte((byte) (state ? 1 : 0));
		dest.writeInt(deviceValue);
	}

	public class MyCreator implements Parcelable.Creator<PicModel> {
		public PicModel createFromParcel(Parcel source) {
			return new PicModel(source);
		}

		public PicModel[] newArray(int size) {
			return new PicModel[size];
		}
	}

}
