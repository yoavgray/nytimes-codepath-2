package com.example.yoavgray.nytarticlefinder.models;

import android.support.annotation.Nullable;

import org.parceler.Parcel;

@Parcel
public class Article {
    String web_url;
    String snippet;
    String lead_paragraph;
    String print_page;
    String source;
    Multimedia[] multimedia;
    Headline headline;
    Keywords[] keywords;
    //Byline[] byline;
    String pub_date;
    String document_type;
    String news_desk;
    String section_name;
    String subsection_name;
    String type_of_material;
    String _id;

    public Article() {
    }

    public Article(String web_url,
                   String snippet,
                   String lead_paragraph,
                   String print_page,
                   String source,
                   Multimedia[] multimedia,
                   Headline headline,
                   Keywords[] keywords,
                   String pub_date,
                   String document_type,
                   String news_desk,
                   String section_name,
                   String subsection_name,
                   String type_of_material,
                   String _id) {
        this.web_url = web_url;
        this.snippet = snippet;
        this.lead_paragraph = lead_paragraph;
        this.print_page = print_page;
        this.source = source;
        this.multimedia = multimedia;
        this.headline = headline;
        this.keywords = keywords;
        this.pub_date = pub_date;
        this.document_type = document_type;
        this.news_desk = news_desk;
        this.section_name = section_name;
        this.subsection_name = subsection_name;
        this.type_of_material = type_of_material;
        this._id = _id;
    }

    public String getWebUrl() {
        return web_url;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getLeadParagraph() {
        return lead_paragraph;
    }

    public String getPrintPage() {
        return print_page;
    }

    public String getSrc() {
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

    public String getPubDate() {
        return pub_date;
    }

    public String getDocumentType() {
        return document_type;
    }

    public String getNewsDesk() {
        return news_desk;
    }

    public String getSectionName() {
        return section_name;
    }

    public String getSubsectionName() {
        return subsection_name;
    }

    public String getTypeOfMaterial() {
        return type_of_material;
    }

    public String getId() {
        return _id;
    }

    @Nullable public String getThumbnailUrl() {
        String url = null;
        for (Multimedia multimedia : getMultimedia()) {
            if (multimedia.getSubtype().equals("thumbnail")) {
                url = multimedia.getUrl();
            }
        }
        return url;
    }
}
