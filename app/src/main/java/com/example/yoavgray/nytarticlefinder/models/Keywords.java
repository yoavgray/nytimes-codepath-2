package com.example.yoavgray.nytarticlefinder.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Keywords implements Parcelable {
    String rank;
    String is_major;
    String name;
    String value;

    protected Keywords(Parcel in) {
        rank = in.readString();
        is_major = in.readString();
        name = in.readString();
        value = in.readString();
    }

    public static final Creator<Keywords> CREATOR = new Creator<Keywords>() {
        @Override
        public Keywords createFromParcel(Parcel in) {
            return new Keywords(in);
        }

        @Override
        public Keywords[] newArray(int size) {
            return new Keywords[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rank);
        dest.writeString(is_major);
        dest.writeString(name);
        dest.writeString(value);
    }
}
