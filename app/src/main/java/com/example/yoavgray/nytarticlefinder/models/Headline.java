package com.example.yoavgray.nytarticlefinder.models;

import org.parceler.Parcel;

@Parcel
public class Headline {
    String main;

    public Headline() {

    }

    public Headline(String main) {
        this.main = main;
    }

    public String getHeadline() {
        return main;
    }
}
