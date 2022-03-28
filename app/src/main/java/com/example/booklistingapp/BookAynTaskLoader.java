package com.example.booklistingapp;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class BookAynTaskLoader extends AsyncTaskLoader {
    private String mUrl;

    public BookAynTaskLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url; //taking the url
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        //calling the fetch book data method
        if (mUrl.length() == 0 || mUrl == null) {
            return null;
        }
        return  Utils.fetchBookData(mUrl);
    }
}
