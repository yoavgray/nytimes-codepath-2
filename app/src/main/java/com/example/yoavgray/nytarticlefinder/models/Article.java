package com.example.yoavgray.nytarticlefinder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable{
    String web_url;
    String snippet;
    String lead_paragraph;
    String print_page;
    String source;
    Multimedia[] multimedia;
    Headline headline;
    Keywords[] keywords;
    String pub_date;
    String document_type;
    String news_desk;
    String section_name;
    String subsection_name;
    String type_of_material;
    String _id;

    protected Article(Parcel in) {
        web_url             = in.readString();
        snippet             = in.readString();
        lead_paragraph      = in.readString();
        print_page          = in.readString();
        source              = in.readString();
        multimedia          = in.createTypedArray(Multimedia.CREATOR);
        headline            = in.readParcelable(Headline.class.getClassLoader());
        keywords            = in.createTypedArray(Keywords.CREATOR);
        pub_date            = in.readString();
        document_type       = in.readString();
        news_desk           = in.readString();
        section_name        = in.readString();
        subsection_name     = in.readString();
        type_of_material    = in.readString();
        _id                 = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(web_url);
        dest.writeString(snippet);
        dest.writeString(lead_paragraph);
        dest.writeString(print_page);
        dest.writeString(source);
        dest.writeParcelableArray(multimedia, 0);
        dest.writeParcelable(headline, 0);
        dest.writeParcelableArray(keywords, 0);
        dest.writeString(pub_date);
        dest.writeString(document_type);
        dest.writeString(news_desk);
        dest.writeString(section_name);
        dest.writeString(subsection_name);
        dest.writeString(type_of_material);
        dest.writeString(_id);
    }

    public String getWeb_url() {
        return web_url;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getLead_paragraph() {
        return lead_paragraph;
    }

    public String getPrint_page() {
        return print_page;
    }

    public String getSource() {
        return source;
    }

    public Multimedia[] getMultimedia() {
        return multimedia;
    }

    public Headline getHeadline() {
        return headline;
    }

    public Keywords[] getKeywords() {
        return keywords;
    }

    public String getPub_date() {
        return pub_date;
    }

    public String getDocument_type() {
        return document_type;
    }

    public String getNews_desk() {
        return news_desk;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getSubsection_name() {
        return subsection_name;
    }

    public String getType_of_material() {
        return type_of_material;
    }

    public String get_id() {
        return _id;
    }
}
