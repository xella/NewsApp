package com.example.xella.newsapp;

/**
 * Created by maria on 07/08/17.
 */

public class News {

    private String mHeadline;
    private String mBody;
    private String mImageResourceID;
    private String mAuthor;
    private String mSection;
    private String mUrl;

    public News(String headline, String body, String imageResourceID, String author, String section, String url) {
        mHeadline = headline;
        mBody = body;
        mImageResourceID = imageResourceID;
        mAuthor = author;
        mSection = section;
        mUrl = url;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getBody() {
        return mBody;
    }

    public String getImageResourceID() {
        return mImageResourceID;
    }

    public String getAuthor() {
        return  mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }
}
