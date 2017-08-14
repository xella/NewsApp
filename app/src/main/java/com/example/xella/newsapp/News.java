package com.example.xella.newsapp;

public class News {

    private String mHeadline;
    private String mBody;
    private String mAuthor;
    private String mSection;
    private String mDate;
    private String mUrl;

    public News(String headline, String body, String author, String section, String date, String url) {
        mHeadline = headline;
        mBody = body;
        mAuthor = author;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getBody() {
        return mBody;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
