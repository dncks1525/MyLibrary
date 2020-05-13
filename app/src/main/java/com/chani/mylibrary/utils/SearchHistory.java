package com.chani.mylibrary.utils;

import android.content.SearchRecentSuggestionsProvider;

public class SearchHistory extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.chani.SearchHistory";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchHistory() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
