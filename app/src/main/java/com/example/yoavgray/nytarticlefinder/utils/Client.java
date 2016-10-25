package com.example.yoavgray.nytarticlefinder.utils;

import java.util.HashMap;
import java.util.HashSet;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Client {
    private static final String INCLUDED_CATEGORIES = "includedCategories";
    private static final String CATEGORIES_LIST = "categoriesList";
    private static final String SORT_ID = "sort";
    private static final String QUERY_KEY = "q";
    private static final String PAGE_KEY = "page";
    private static final String BEGIN_DATE = "begin_date";
    private static final String END_DATE = "end_date";
    private static final String NEWS_DESK = "fq";
    private static final String API_KEY = "58b8ef6b492349d4b3a3c2968d411aa6";
    private static final String API_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private OkHttpClient client;

    public Client() {
        this.client = new OkHttpClient();
    }

    private String getApiUrl() {
        return API_URL;
    }

    public String getApiKey() {
        return API_KEY;
    }

    public void getArticles(HashMap<String,String> queryParams,
                            HashSet<String> includedCategoriesHashSet,
                            int page,
                            Callback callback) {
        String url = buildQueryParamsUrl(queryParams, includedCategoriesHashSet, page);

        Request request = new Request.Builder()
                .url(url)
                .build();

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(callback);
    }

    /**
     * Build query params according to filters that were saved by the user
     * @return a full URL, ready to be delivered
     */
    private String buildQueryParamsUrl(HashMap<String,String> queryParamsHashMap,
                                       HashSet<String> includedCategoriesHashSet,
                                       int page) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL).newBuilder();
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
            urlBuilder.addQueryParameter(NEWS_DESK, newsDeskParam.toString());
        }

        return urlBuilder.build().toString();
    }
}
