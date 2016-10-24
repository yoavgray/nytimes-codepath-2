package com.example.yoavgray.nytarticlefinder.models;

import org.parceler.Parcel;

@Parcel
public class Keywords {
    String rank;
    String is_major;
    String name;
    String value;

    public Keywords() {}

    public Keywords(String rank, String is_major, String name, String value) {
        this.rank = rank;
        this.is_major = is_major;
        this.name = name;
        this.value = value;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getIsMajor() {
        return is_major;
    }

    public void setIsMajor(String is_major) {
        this.is_major = is_major;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
