package com.example.yoavgray.nytarticlefinder.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    public static final String ARTICLE_API_KEY = "58b8ef6b492349d4b3a3c2968d411aa6";
    public final static String NYTIMES_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

    OkHttpClient client = new OkHttpClient();

    @BindView(R.id.app_search_bar) Toolbar appSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupToolBar();
        client = new OkHttpClient();

        //TODO: remember last search
        loadArticles();
    }

    private void loadArticles() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYTIMES_URL).newBuilder();
//        urlBuilder.addQueryParameter("rsz", "8");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    Gson gson = new GsonBuilder().create();
                    JsonArray jsonDocs = gson.fromJson(response.body().string(), JsonObject.class)
                            .getAsJsonObject("response").getAsJsonArray("docs");

                    Article[] results = gson.fromJson(jsonDocs, Article[].class);
                }
            }
        });
    }

    private void setupToolBar() {
        setSupportActionBar(appSearchBar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Enable the Up button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                // User wrote a query in the search bar
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
