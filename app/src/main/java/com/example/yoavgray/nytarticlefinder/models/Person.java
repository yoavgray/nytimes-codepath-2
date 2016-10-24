package com.example.yoavgray.nytarticlefinder.models;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcel;

@Parcel
public class Person {
    String firstname;
    String middlename;
    String lastname;
    Integer rank;
    String role;
    String organization;

    public Person() {}

    public Person(String firstname, String middlename, String lastname, Integer rank, String role, String organization) {
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.rank = rank;
        this.role = role;
        this.organization = organization;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        firstname = StringUtils.capitalize(firstname);
        middlename = StringUtils.capitalize(middlename);
        lastname = StringUtils.capitalize(lastname);
        fullName.append(firstname).append(" ");
        if (middlename != null) fullName.append(middlename).append(" ");
        fullName.append(lastname);
        return StringUtils.capitalize(fullName.toString().toLowerCase());
    }
}
