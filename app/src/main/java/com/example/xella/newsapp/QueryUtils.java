package com.example.xella.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryUtils {

    private static final String Log_TAG = QueryUtils.class.getSimpleName();

    private static final int READ_TIMEOUT = 10000; /* milliseconds */

    private static final int CONNECT_TIMEOUT = 20000;

    private QueryUtils() {
    }

    public static List<News> fetchNewsEntryData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(Log_TAG, "Problem while making the HTTP request.", e);
        }

        List<News> newsList = extractFeatureFromJson(jsonResponse);

        return newsList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(Log_TAG, "Problem while building the URL");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(Log_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(Log_TAG, "Problem while retrieving the JSON results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        Log.v(Log_TAG, newsJSON);
        List<News> newsList = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            Log.v(Log_TAG, "baseJsonResponse: " + baseJsonResponse);
            JSONObject jsonResponse = baseJsonResponse.getJSONObject("response");
            Log.v(Log_TAG, "jsonResponse: " + jsonResponse);
            JSONArray newsArray = jsonResponse.getJSONArray("results");
            Log.v(Log_TAG, "newsArray: " + newsArray);

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);
                Log.v(Log_TAG, "currentNews: " + currentNews);
                // Extract the value for the key called "sectionName"
                String section = "N/A";
                if (currentNews.has("sectionName")) {
                    section = currentNews.getString("sectionName");
                    Log.v(Log_TAG, "section: " + section);
                }

                // Extract the value for the key called "webUrl"
                String url = "";
                if (currentNews.has("webUrl")) {
                    url = currentNews.getString("webUrl");
                    Log.v(Log_TAG, "url: " + url);
                }

                // Extract the value for the key called "webPublicationDate"
                String dateUnformatted;
                String datePublished;
                if (currentNews.has("webPublicationDate")) {
                    dateUnformatted = currentNews.getString("webPublicationDate");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        Date date = format.parse(dateUnformatted);
                        datePublished = (String) android.text.format.DateFormat.format("dd" + " " + "MMM"+ " " + "yyyy" + ", " + "HH:mm", date);
                    } catch (ParseException e) {
                        Log.e(Log_TAG, "An exception was encountered while trying to parse a date. " + e);
                        datePublished = "";
                    }
                } else {
                    datePublished = "";
                }

                String headline = "N/A";
                String author = "N/A";
                String body = "N/A";


                if (currentNews.has("fields")) {
                    JSONObject currentNewsFields = currentNews.getJSONObject("fields");

                    // Extract the value for the key called "headline"
                    if (currentNewsFields.has("headline")) {
                        headline = currentNewsFields.getString("headline");
                    }

                    // Extract the value for the key called "byline"
                    if (currentNewsFields.has("byline")) {
                        author = currentNewsFields.getString("byline");
                    }

                    // Extract the value for the key called "body"
                    if (currentNewsFields.has("trailText")) {
                        body = currentNewsFields.getString("trailText");
                    }
                }

                News news = new News(headline, body, author, section, datePublished, url);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(Log_TAG, "Problem while extracting JSON results");
        }
        return newsList;
    }
}
