package com.example.yoavgray.nytarticlefinder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Headline implements Parcelable {
    String main;

    protected Headline(Parcel in) {
        main = in.readString();
    }

    public static final Creator<Headline> CREATOR = new Creator<Headline>() {
        @Override
        public Headline createFromParcel(Parcel in) {
            return new Headline(in);
        }

        @Override
        public Headline[] newArray(int size) {
            return new Headline[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(main);
    }

    public String getHeadline() {
        return main;
    }
}
