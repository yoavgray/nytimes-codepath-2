package com.example.yoavgray.nytarticlefinder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Legacy implements Parcelable {
    String thumbnailheight;
    String thumbnailwidth;
    String thumbnail;

    protected Legacy(Parcel in) {
        thumbnailheight = in.readString();
        thumbnailwidth = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Legacy> CREATOR = new Creator<Legacy>() {
        @Override
        public Legacy createFromParcel(Parcel in) {
            return new Legacy(in);
        }

        @Override
        public Legacy[] newArray(int size) {
            return new Legacy[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnailheight);
        dest.writeString(thumbnailwidth);
        dest.writeString(thumbnail);
    }
}