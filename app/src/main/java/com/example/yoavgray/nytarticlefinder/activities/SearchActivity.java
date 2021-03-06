package com.example.yoavgray.nytarticlefinder.activities;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.adapters.ArticleAdapter;
import com.example.yoavgray.nytarticlefinder.adapters.CategoryAdapter;
import com.example.yoavgray.nytarticlefinder.fragments.FilterFragment;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.example.yoavgray.nytarticlefinder.models.Category;
import com.example.yoavgray.nytarticlefinder.utils.Client;
import com.example.yoavgray.nytarticlefinder.utils.EndlessRecyclerViewScrollListener;
import com.example.yoavgray.nytarticlefinder.utils.ItemClickSupport;
import com.example.yoavgray.nytarticlefinder.utils.SpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity implements FilterFragment.PrefParamsSelectedListener {
    private static final String INCLUDED_CATEGORIES = "includedCategories";
    private static final String CATEGORIES_LIST = "categoriesList";
    private static final String SORT_ID = "sort";
    private static final String QUERY_KEY = "q";
    private final static String PAGE_KEY = "page";
    private final static String BEGIN_DATE = "begin_date";
    private final static String END_DATE = "end_date";
    private final static String NEWS_DESK = "fq";

    // Filter Atrributes
    int sortId;
    String beginDate, endDate;
    HashMap<String,String> queryParamsHashMap;
    HashSet<String> includedCategoriesHashSet = new HashSet<>();
    // Adapters, Lists and Networking
    List<Category> categories;
    List<Article> articles;
    CategoryAdapter categoriesAdapter;
    ArticleAdapter articleAdapter;
    Client client;

    @BindView(R.id.app_search_bar) Toolbar appSearchBar;
    @BindView(R.id.articles_recycler_view) RecyclerView articlesRecyclerView;
    @BindView(R.id.category_recycler_view) RecyclerView categoryRecyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_activity_search) RelativeLayout searchActivityLayout;
    @BindView(R.id.fab_filter) FloatingActionButton filterFab;
    @BindString(R.string.retry_caps) String retryString;
    @BindString(R.string.load_articles_error) String loadArticlesErrorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setCalligraphy();
        setNetwork();

        if (savedInstanceState != null) {
            sortId = savedInstanceState.getInt(SORT_ID);
            beginDate = savedInstanceState.getString(BEGIN_DATE);
            endDate = savedInstanceState.getString(END_DATE);
            includedCategoriesHashSet = Parcels.unwrap(savedInstanceState.getParcelable(INCLUDED_CATEGORIES));
            categories = Parcels.unwrap(savedInstanceState.getParcelable(CATEGORIES_LIST));
        } else {
            sortId = 0;
            beginDate = null;
            endDate = null;
        }
        articles = new ArrayList<>();
        // Setup Layouts
        setupToolBar();
        setCategoriesRecyclerView();
        setArticlesRecyclerView();
        setRecyclerViewsListeners();
        setupSwipeRefreshLayout();

        checkConnectivity();
        //TODO: remember last search
        loadArticles(0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(INCLUDED_CATEGORIES, Parcels.wrap(includedCategoriesHashSet));
        savedInstanceState.putParcelable(CATEGORIES_LIST, Parcels.wrap(categories));
        savedInstanceState.putInt(SORT_ID, sortId);
        savedInstanceState.putString(BEGIN_DATE, beginDate);
        savedInstanceState.putString(END_DATE, endDate);
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
     * Set networking params
     */
    private void setNetwork() {
        client = new Client();
        queryParamsHashMap = new HashMap<>();
        queryParamsHashMap.put("api-key",client.getApiKey());
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
            actionBar.setTitle(getString(R.string.app_name));
        }
    }

    /**
     * This method sets the SwipeRefreshLayout on start
     */
    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> loadArticles(0));
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * This method sets the Articles RecyclerView on start
     */
    private void setArticlesRecyclerView() {
        articleAdapter = new ArticleAdapter(this, articles);
        articlesRecyclerView.setAdapter(articleAdapter);
        // Change number of columns when changing screen orientation
        StaggeredGridLayoutManager gridLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        }
        // Attach the layout manager to the recycler view
        articlesRecyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                SpacesItemDecoration(10);
        articlesRecyclerView.addItemDecoration(itemDecoration);

        articlesRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadArticles(page);
            }
        });
    }

    /**
     * This method sets the Categories RecyclerView on start
     */
    private void setCategoriesRecyclerView() {
        if (categories == null) {
            String[] categoriesArray = {"Adventure Sports", "Cars", "Culture", "Fashion", "Food", "Foreign", "Health", "Jobs", "Movies", "Sports", "Technology"};
            categories = new ArrayList<>();
            for (String c : categoriesArray) {
                categories.add(new Category(c));
            }
        }
        categoriesAdapter = new CategoryAdapter(this, categories);
        categoryRecyclerView.setAdapter(categoriesAdapter);
        categoryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);
        categoryRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * This method sets the RecyclerViews listeners with the help of the fabulous ItemClickSupport!
     */
    private void setRecyclerViewsListeners() {
        // Open the clicked article with a Chrome Custom Tab
        ItemClickSupport.addTo(articlesRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
                    String url = articles.get(position).getWebUrl();
                    CustomTabsIntent customTabsIntent = buildCustomTabsIntent(url);
                    // and launch the desired Url with CustomTabsIntent.launchUrl()
                    customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(url));
                }
        );

        // Maintain the list of news desk categories when a user clicks on a category
        ItemClickSupport.addTo(categoryRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    boolean isIncluded = categories.get(position).isIncluded();
                    categories.get(position).setIncluded(!isIncluded);
                    categoriesAdapter.notifyItemChanged(position);
                    isIncluded = !isIncluded;
                    String categoryName = categories.get(position).getName();
                    if (isIncluded) {
                        includedCategoriesHashSet.add(categoryName);
                    } else {
                        includedCategoriesHashSet.remove(categoryName);
                    }
                    loadArticles(0);
                }
        );
    }

    /**
     * This method adds support to open an article in a Chrome custom tab
     * @param url the url to open the tab in
     * @return a CustomTabIntent
     */
    private CustomTabsIntent buildCustomTabsIntent(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // Set toolbar color and set custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(SearchActivity.this, R.color.colorPrimaryDark));
        builder.addDefaultShareMenuItem();
        // Build a PendingIntent for when a user clicks on the share icon
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        int requestCode = 100;
        PendingIntent pendingIntent = PendingIntent.getActivity(SearchActivity.this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Map the bitmap, text, and pending intent to this icon
        // Set tint to be true so it matches the toolbar color
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        return builder.build();
    }


    /**
     * This method checks connectivity and renders a Snackbar if there's a problem
     */
    private void checkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
        if (!isConnected || !isOnline()) {
            Snackbar
                .make(searchActivityLayout,
                        loadArticlesErrorString,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(retryString, v -> loadArticles(0))
            .setActionTextColor(Color.RED).show();
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e)
            { e.printStackTrace(); }
        return false;
    }

    /**
     * This method is used to load articles on start, refresh and retries if internet connection
     * was not available
     */
    private void loadArticles(int page) {
        // Clear screen while searching and pop the progress bar to show
        if (page == 0) {
            articles.clear();
            articleAdapter.notifyDataSetChanged();
        }

        // Get a handler that can be used to post to the main thread
        client.getArticles(queryParamsHashMap, includedCategoriesHashSet, page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                checkConnectivity();
                SearchActivity.this.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    checkConnectivity();
                    throw new IOException("Unexpected code " + response);
                } else {
                    Gson gson = new GsonBuilder().create();
                    JsonArray jsonDocs = gson.fromJson(response.body().string(), JsonObject.class)
                            .getAsJsonObject("response").getAsJsonArray("docs");
                    Article[] articlesArray = gson.fromJson(jsonDocs, Article[].class);
                    // Must close the response body!
                    response.body().close();
                    // Should not assign a new reference, but act on this local reference of the data
                    articles.addAll(new ArrayList<>(Arrays.asList(articlesArray)));
                    // Cannot change Views state outside of the UI thread
                    SearchActivity.this.runOnUiThread(() -> {
                        articleAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
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
                queryParamsHashMap.put(QUERY_KEY, query);
                loadArticles(0);
                // Must return true if we want to consume the event!
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (StringUtils.isEmpty(newText)) {
                    queryParamsHashMap.remove(QUERY_KEY);
                    loadArticles(0);
                }
                return true;
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
            Field searchField = SearchView.class.getDeclaredField("mGoButton");
            searchField.setAccessible(true);
            ImageView submitButton = (ImageView) searchField.get(searchView);
            submitButton.setImageResource(R.drawable.ic_submit);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method customized the X button to load fresh articles with no queries
     * @param searchView
     */
    private void customizeCloseButton(SearchView searchView) {
        Field searchField = null;
        try {
            searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView closeButton = (ImageView) searchField.get(searchView);
            closeButton.setOnClickListener(view -> {
                queryParamsHashMap.remove(QUERY_KEY);
                loadArticles(0);
                searchView.setQuery("", false);
                searchView.clearFocus();
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
        searchView.setSubmitButtonEnabled(true);

        customizeSubmitButton(searchView);
        customizeCloseButton(searchView);
        setSearchViewListener(searchView);

        return true;
    }

    @OnClick(R.id.fab_filter)
    public void launchFilterDialog() {
        FragmentManager fm = getFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance(beginDate, endDate, sortId);
        filterFragment.show(fm, "fragment_filter");
    }

    @Override
    public void onPrefParamsSaved(String beginDate, String endDate, int sortId) {
        if (beginDate != null && !beginDate.isEmpty()) {
            queryParamsHashMap.put(BEGIN_DATE, beginDate);
            this.beginDate = beginDate;
        }

        if (endDate != null && !endDate.isEmpty()) {
            queryParamsHashMap.put(END_DATE, endDate);
            this.endDate = endDate;
        }

        queryParamsHashMap.put(SORT_ID, sortId == 0 ? "newest" : "oldest");
        this.sortId = sortId;
        loadArticles(0);
    }
}
