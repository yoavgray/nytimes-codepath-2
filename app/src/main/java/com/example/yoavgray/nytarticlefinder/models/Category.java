package com.example.yoavgray.nytarticlefinder.models;

import org.parceler.Parcel;

@Parcel
public class Category {
    String name;
    boolean isIncluded;

    public Category() {}

    public Category(String name) {
        this.name = name;
        this.isIncluded = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncluded() {
        return isIncluded;
    }

    public void setIncluded(boolean included) {
        isIncluded = included;
    }
}
