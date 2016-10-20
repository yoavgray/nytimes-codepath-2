package com.example.yoavgray.nytarticlefinder.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.adapters.ArticleAdapter;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.example.yoavgray.nytarticlefinder.utils.SpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {
    public static final String ARTICLE_API_KEY = "58b8ef6b492349d4b3a3c2968d411aa6";
    public final static String NYTIMES_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    public String query;

    List<Article> articles;
    ArticleAdapter adapter;
    OkHttpClient client;

    @BindView(R.id.app_search_bar) Toolbar appSearchBar;
    @BindView(R.id.articles_recycler_view) RecyclerView articlesRecyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_activity_search) RelativeLayout searchActivityLayout;
    @BindString(R.string.retry_caps) String retryString;
    @BindString(R.string.load_articles_error) String loadArticlesErrorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setCalligraphy();

        client = new OkHttpClient();
        articles = new ArrayList<>();
        // Setup Layouts
        //TODO: add a progress bar when loading articles
        //TODO: which toolbar am i using?!?!
        setupToolBar();
        setRecyclerView();
        setupSwipeRefreshLayout();

        checkConnectivity();
        //TODO: remember last search
        loadArticles(query);
    }

    /*
        Set Calligraphy fonts for the activity!
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Calligraffiti.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    /**
     * This method sets the Toolbar on start
     */
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

    /**
     * This method sets the SwipeRefreshLayout on start
     */
    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadArticles(query);
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * This method sets the RecyclerView on start
     */
    private void setRecyclerView() {
        adapter = new ArticleAdapter(this, articles);
        articlesRecyclerView.setAdapter(adapter);
        // Change number of columns when changing screen orientation
        StaggeredGridLayoutManager gridLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        } else {
            gridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        }
        // Attach the layout manager to the recycler view
        articlesRecyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                SpacesItemDecoration(16);
        articlesRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * This method checks connectivity and renders a Snackbar if there's a problem
     */
    private void checkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        if (!isConnected) {
            Snackbar
                .make(searchActivityLayout,
                        loadArticlesErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadArticles(query);
                    }
                })
            .setActionTextColor(Color.RED).show();
        }
    }

    /**
     * This method is used to load articles on start, refresh and retries if internet connection
     * was not available
     */
    private void loadArticles(String query) {
        // Clear screen while searching and pop the progress bar to show
        articles.clear();
        adapter.notifyDataSetChanged();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYTIMES_URL).newBuilder();
        urlBuilder.addQueryParameter("api-key", ARTICLE_API_KEY);
        if (query == null || query.equals("")) {
            urlBuilder.addQueryParameter("q", "new york times");
        } else {
            urlBuilder.addQueryParameter("q", query);
        }
        urlBuilder.addQueryParameter("page", "1");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                checkConnectivity();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    Gson gson = new GsonBuilder().create();
                    JsonArray jsonDocs = gson.fromJson(response.body().string(), JsonObject.class)
                            .getAsJsonObject("response").getAsJsonArray("docs");
                    Article[] articlesArray = gson.fromJson(jsonDocs, Article[].class);

                    // Should not assign a new reference, but act on this local reference of the data
                    articles.addAll(new ArrayList<>(Arrays.asList(articlesArray)));
                    // Cannot change Views state outside of the UI thread
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        customizeSubmitButton(searchView);
        setSearchViewListener(searchView);

        return true;
    }

    /**
     * This method sets a listener for when submiting a text query or changing text
     * in the search field //TODO: add suggestions if have time
     * @param searchView
     */
    private void setSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadArticles(query);
                // Must return true if we want to consume the event!
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * This method customizes the icon of the submit button
     * @param searchView
     */
    private void customizeSubmitButton(SearchView searchView) {
        // Set the submit button to be a custom button
        try {
            Field searchField =SearchView.class.getDeclaredField("mGoButton");
            searchField.setAccessible(true);
            ImageView submitButton = (ImageView) searchField.get(searchView);
            submitButton.setImageResource(R.drawable.ic_submit);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
