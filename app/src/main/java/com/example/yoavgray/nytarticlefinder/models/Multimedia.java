package com.example.yoavgray.nytarticlefinder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Multimedia implements Parcelable{
    Integer width;
    Integer height;
    String url;
    String subtype;
    //Legacy legacy;
    String type;

    protected Multimedia(Parcel in) {
        width   = in.readInt();
        height  = in.readInt();
        url     = in.readString();
        subtype = in.readString();
        //legacy  = in.readParcelable(Legacy.class.getClassLoader());
        type    = in.readString();
    }

    public static final Creator<Multimedia> CREATOR = new Creator<Multimedia>() {
        @Override
        public Multimedia createFromParcel(Parcel in) {
            return new Multimedia(in);
        }

        @Override
        public Multimedia[] newArray(int size) {
            return new Multimedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
        dest.writeString(subtype);
        //dest.writeParcelable(legacy, 0);
        dest.writeString(type);
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getType() {
        return type;
    }
}
