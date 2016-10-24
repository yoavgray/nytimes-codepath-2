package com.example.yoavgray.nytarticlefinder.activities;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.adapters.ArticleAdapter;
import com.example.yoavgray.nytarticlefinder.adapters.CategoryAdapter;
import com.example.yoavgray.nytarticlefinder.fragments.FilterFragment;
import com.example.yoavgray.nytarticlefinder.fragments.SortFragment;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.example.yoavgray.nytarticlefinder.models.Category;
import com.example.yoavgray.nytarticlefinder.utils.EndlessRecyclerViewScrollListener;
import com.example.yoavgray.nytarticlefinder.utils.ItemClickSupport;
import com.example.yoavgray.nytarticlefinder.utils.SpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity implements SortFragment.SortParamSelectedListener {
    public static final String ARTICLE_API_KEY = "58b8ef6b492349d4b3a3c2968d411aa6";
    public final static String NYTIMES_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String INCLUDED_CATEGORIES = "includedCategories";
    private static final String CATEGORIES_LIST = "categoriesList";
    private static final String SORT_ID = "sortId";
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
    OkHttpClient client;

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

        queryParamsHashMap = new HashMap<>();
        queryParamsHashMap.put("api-key",ARTICLE_API_KEY);

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
        client = new OkHttpClient();
        articles = new ArrayList<>();
        // Setup Layouts
        //TODO: add a progress bar when loading articles
        //TODO: which toolbar am i using?!?!
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadArticles(0);
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
    private void setArticlesRecyclerView() {
        articleAdapter = new ArticleAdapter(this, articles);
        articlesRecyclerView.setAdapter(articleAdapter);
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
                SpacesItemDecoration(10);
        articlesRecyclerView.addItemDecoration(itemDecoration);

        articlesRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadArticles(page);
            }
        });
    }

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
        ItemClickSupport.addTo(articlesRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Toast.makeText(SearchActivity.this, "Open Article!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ItemClickSupport.addTo(categoryRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
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
                }
        );
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
                        loadArticles(0);
                    }
                })
            .setActionTextColor(Color.RED).show();
        }
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

        String url = buildQueryParamsUrl(page);

        Request request = new Request.Builder()
                .url(url)
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                checkConnectivity();
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
                            articleAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Build query params according to filters that were saved by the user
     * @return a full URL, ready to be delivered
     */
    private String buildQueryParamsUrl(int page) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(NYTIMES_URL).newBuilder();
        for (String key : queryParamsHashMap.keySet()) {
            urlBuilder.addQueryParameter(key, queryParamsHashMap.get(key));
        }
        urlBuilder.addQueryParameter(PAGE_KEY, String.valueOf(page));
        if (includedCategoriesHashSet.size() > 0) {
            StringBuilder newsDeskParam = new StringBuilder("news_desk:(");
            for (String setEntry : includedCategoriesHashSet) {
                newsDeskParam.append('"').append(setEntry).append('"').append(" ");
            }
            // For the last white space
            newsDeskParam.setLength(newsDeskParam.length() - 1);
            newsDeskParam.append(")");
            urlBuilder.addQueryParameter("fq", newsDeskParam.toString());
        }

        if (beginDate != null && !beginDate.isEmpty()) {
            urlBuilder.addQueryParameter(BEGIN_DATE, beginDate);
        }

        if (endDate != null && !endDate.isEmpty()) {
            urlBuilder.addQueryParameter(END_DATE, endDate);
        }

        return urlBuilder.build().toString();
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
        setSearchViewListener(searchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                launchSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchSortDialog() {
        FragmentManager fm = getFragmentManager();
        SortFragment sortFragment = SortFragment.newInstance(sortId);
        sortFragment.show(fm, "fragment_sort");
    }


    @Override
    public void onSortParamSelected(int sortId) {
        String sortOrder = sortId == 0 ? "newest" : "oldest";
        queryParamsHashMap.put("sort", sortOrder);
        // Reload articles with new sort order
        loadArticles(0);
    }

    @OnClick(R.id.fab_filter)
    public void launchFilterDialog() {
        FragmentManager fm = getFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance(beginDate, endDate);
        filterFragment.show(fm, "fragment_sort");
    }
}
