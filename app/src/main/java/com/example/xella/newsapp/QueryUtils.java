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
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String Log_TAG = QueryUtils.class.getSimpleName();

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
            urlConnection.setReadTimeout((10000));
            urlConnection.setConnectTimeout(20000);
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

                if (currentNews.has("fields")) {
                    JSONObject currentNewsFields = currentNews.getJSONObject("fields");

                    // Extract the value for the key called "headline"
                    String headline = "N/A";
                    if (currentNewsFields.has("headline")) {
                        headline = currentNewsFields.getString("headline");
                    }

                    // Extract the value for the key called "byline"
                    String author = "N/A";
                    if (currentNewsFields.has("byline")) {
                        author = currentNewsFields.getString("byline");
                    }

                    // Extract the value for the key called "body"
                    String body = "N/A";
                    if (currentNewsFields.has("trailText")) {
                        body = currentNewsFields.getString("trailText");
                    }

                    News news = new News(headline, body, author, section, url);
                    newsList.add(news);
                }
            }
        } catch (JSONException e) {
            Log.e(Log_TAG, "Problem while extracting JSON results");
        }
        return newsList;
    }
}
