package com.example.xella.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String GUARDIAN_URL = "http://content.guardianapis.com/search?api-key=test&show-fields=headline%2Cbyline%2CtrailText&page-size=20&q=learning";

    private static final int NEWS_LOADER_ID = 1;

    private static final String LOG_TAG = NewsActivity.class.getName();

    private NewsAdapter mNewsAdapter;
    private ListView mNewsListView;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final ArrayList<News> newsResult = new ArrayList<>();

        mNewsListView = (ListView) findViewById(R.id.news_list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        mNewsListView.setEmptyView(mEmptyStateTextView);

        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(R.string.no_data);

        // Set up the adapter for the ListView
        mNewsAdapter = new NewsAdapter(NewsActivity.this, newsResult);
        mNewsListView.setAdapter(mNewsAdapter);

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = mNewsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    // Create an instance of a Loader if there is no previous one
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        mEmptyStateTextView.setVisibility(View.GONE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("order-by", orderBy);

        Log.v(LOG_TAG, "Result url: " + uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    // Populate UI with the data obtained from HTTP query
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        mNewsAdapter.clear();

        if (newsList != null && !newsList.isEmpty()) {
            mNewsAdapter.addAll(newsList);
        }
    }

    // Clear data on reset
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mNewsAdapter.clear();
    }

    /**
     * Method that checks whether there is a network connection
     * @return boolean that is true if there is a network connection
     */
    private boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
