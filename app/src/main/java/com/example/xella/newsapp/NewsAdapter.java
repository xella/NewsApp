package com.example.xella.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context, 0, newsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        News currentNews = (News) getItem(position);

        TextView newsHeadline = (TextView) listItemView.findViewById(R.id.news_title);
        newsHeadline.setText(currentNews.getHeadline());

        TextView newsBody = (TextView) listItemView.findViewById(R.id.news_body);
        newsBody.setText(currentNews.getBody());

        TextView newsAuthor = (TextView) listItemView.findViewById(R.id.news_author);
        newsAuthor.setText(currentNews.getAuthor());

        TextView newsSection = (TextView) listItemView.findViewById(R.id.news_section);
        newsSection.setText(currentNews.getSection());

        TextView newsDate = (TextView) listItemView.findViewById(R.id.news_date);
        newsDate.setText(currentNews.getDate());

        return listItemView;
    }
}
