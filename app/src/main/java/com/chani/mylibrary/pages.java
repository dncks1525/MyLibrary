package com.chani.mylibrary;

import com.chani.mylibrary.data.PageItem;

class pages {
    public static class NewPage extends PageItem {
        public NewPage(PageType type) {
            super(PageType.NEW);
        }
    }

    public static class SearchPage extends PageItem {
        public SearchPage(PageType type) {
            super(PageType.SEARCH);
        }
    }

    public static class BookmarkPage extends PageItem {
        public BookmarkPage(PageType type) {
            super(PageType.BOOKMARK);
        }
    }

    public static class HistoryPage extends PageItem {
        public HistoryPage(PageType type) {
            super(PageType.HISTORY);
        }
    }
}
