package com.example.android.booksbooksandmorebooks;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by tom.mills-mock on 20/07/2017.
 */

public class BookLoader extends AsyncTaskLoader {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();
    public String Keywords;
    /**
     * Query URL
     */
    private String Url;

    public BookLoader(Context context, String url, String keywords) {
        super(context);
        Url = url;
        Keywords = keywords;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        if (Url == null || Keywords == null)
            return null;

        return QueryUtils.fetchBookData(Url + Keywords);
    }
}
