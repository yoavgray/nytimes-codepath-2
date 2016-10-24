package com.example.yoavgray.nytarticlefinder.models;

import org.parceler.Parcel;

@Parcel
public class Byline {
    Person[] person;

    public Byline() {}

    public Person[] getPerson() {
        return person;
    }

    public void setPerson(Person[] person) {
        this.person = person;
    }
}
