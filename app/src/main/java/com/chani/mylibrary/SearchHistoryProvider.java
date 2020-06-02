package com.chani.mylibrary;

import android.content.SearchRecentSuggestionsProvider;

public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.chani.mylibrary.SearchHistoryProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchHistoryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
