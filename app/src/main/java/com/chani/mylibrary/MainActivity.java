package com.chani.mylibrary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.viewpager2.widget.ViewPager2;

import com.chani.mylibrary.adapters.PageAdapter;
import com.chani.mylibrary.data.BookDatabase.Book;
import com.chani.mylibrary.data.BookmarkPage;
import com.chani.mylibrary.data.HistoryPage;
import com.chani.mylibrary.data.NewPage;
import com.chani.mylibrary.data.PageInfo;
import com.chani.mylibrary.data.SearchPage;
import com.chani.mylibrary.databinding.ActivityMainBinding;
import com.chani.mylibrary.utils.AppLog;
import com.chani.mylibrary.utils.BookStoreClient;
import com.chani.mylibrary.utils.SearchHistory;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.chani.mylibrary.data.PageInfo.PageType;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private SearchView mSearchView;

    private AlertDialog mLoadingDialog;
    private SearchRecentSuggestions mBookSearchHistory;
    private InputMethodManager mInputMethodManager;
    private boolean mIsSoftKeyboardShowing;
    private CompositeDisposable mDisposable;
    private BookStoreClient.IBookStore mBookStore;
    private List<PageInfo> mPageInfoList;
    private boolean mIsSearchAvailable;
    private int mCurrSearchPage;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = mBinding.getRoot().getRootView().getHeight() - mBinding.getRoot().getHeight();
            mIsSoftKeyboardShowing = (heightDiff > 100);
        });
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);
        createDialog();

        mBookSearchHistory = new SearchRecentSuggestions(MainActivity.this,
                SearchHistory.AUTHORITY, SearchHistory.MODE);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (mIsSoftKeyboardShowing)
                    mInputMethodManager.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);

                mSearchView.clearFocus();
            }
        });

        mDisposable = new CompositeDisposable();

        mPageInfoList = new ArrayList<>();
        mPageInfoList.add(registerPageInfo(PageType.NEW));
        mPageInfoList.add(registerPageInfo(PageType.SEARCH));
        mPageInfoList.add(registerPageInfo(PageType.BOOKMARK));
        mPageInfoList.add(registerPageInfo(PageType.HISTORY));

        mBookStore = new BookStoreClient().getBookStore(this);
        mDisposable.add(mBookStore.getNewBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookDatabase -> {
                    AppLog.d("newBooks = " + bookDatabase.getTotal());
                    int page = PageType.NEW.ordinal();
                    mPageInfoList.get(page).setBookList(bookDatabase.getBooks());
                    mBinding.pager.setAdapter(new PageAdapter(mPageInfoList));

                    new TabLayoutMediator(mBinding.tab, mBinding.pager, (tab, position) -> {
                        tab.setText(mPageInfoList.get(position).getName());
                    }).attach();
                }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            AppLog.d("onNewIntent, query = " + query);
            if (query != null) {
                if (mSearchView != null) {
                    mSearchView.setIconified(false);
                    mSearchView.setQuery(query, true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action, menu);
        MenuItem item = menu.findItem(R.id.searchItem);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) item.getActionView();
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                mCurrSearchPage = 1;
                if (!query.isEmpty()) {
                    mSearchView.clearFocus();
                    mBookSearchHistory.saveRecentQuery(query, null);

                    int page = PageType.SEARCH.ordinal();
                    if (mBinding.pager.getCurrentItem() != page) {
                        mBinding.pager.setCurrentItem(page, true);
                    }

                    PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                    if (adapter != null) {
                        adapter.clearBooks(page);
                    }

                    if (!mLoadingDialog.isShowing())
                        mLoadingDialog.show();

                    mIsSearchAvailable = true;
                    searchBooks();
                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
        int position = mBinding.pager.getCurrentItem();
        if (adapter != null) {
            switch (item.getItemId()) {
                case R.id.sortTitleAsc:
                    adapter.sortBy(position, PageAdapter.SORT_BY_TITLE_ASC);
                    break;
                case R.id.sortTitleDsc:
                    adapter.sortBy(position, PageAdapter.SORT_BY_TITLE_DSC);
                    break;
                case R.id.sortPriceAsc:
                    adapter.sortBy(position, PageAdapter.SORT_BY_PRICE_ASC);
                    break;
                case R.id.sortPriceDsc:
                    adapter.sortBy(position, PageAdapter.SORT_BY_PRICE_DSC);
                    break;
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (data.hasExtra(LibraryConst.KEY_ISBN13)) {
            String isbn13 = data.getStringExtra(LibraryConst.KEY_ISBN13);
            boolean isBookmarked = data.getBooleanExtra(LibraryConst.KEY_BOOKMARK, false);

            int pos = mBinding.pager.getCurrentItem();
            List<Book> bookList = mPageInfoList.get(pos).getBookList();
            if (bookList != null) {
                for (Book book : bookList) {
                    if (book.getIsbn13().equals(isbn13)) {
                        book.setBookmarked(isBookmarked);

                        PageInfo pageInfo = mPageInfoList.get(PageType.BOOKMARK.ordinal());
                        List<Book> list = pageInfo.getBookList();
                        if (isBookmarked) {
                            if (list == null || !list.contains(book)) {
                                pageInfo.addBook(book);
                            }
                        } else {
                            if (list != null && list.contains(book)) {
                                pageInfo.removeBook(book);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookSearchHistory.clearHistory();
        mLoadingDialog.dismiss();
        mDisposable.clear();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_loading);
        mLoadingDialog = builder.create();
        Window window = mLoadingDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private PageInfo registerPageInfo(PageType type) {
        PageInfo pageInfo = null;
        switch (type) {
            case NEW:
                pageInfo = new NewPage();
                break;
            case SEARCH:
                pageInfo = new SearchPage();
                pageInfo.setOnScrollListener(() -> {
                    if (mIsSearchAvailable) {
                        mCurrSearchPage++;
                        if (!mLoadingDialog.isShowing()) mLoadingDialog.show();
                        searchBooks();
                    }
                });
                break;
            case BOOKMARK:
                pageInfo = new BookmarkPage();
                break;
            case HISTORY:
                pageInfo = new HistoryPage();
                break;
        }
        pageInfo.setOnItemClickListener(this::startBookDetailActivity);

        PageInfo target = pageInfo;
        mDisposable.add(pageInfo.getBookObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(books -> {
                    int pos = 0;
                    if (target instanceof SearchPage) {
                        pos = 1;
                    } else if (target instanceof BookmarkPage) {
                        pos = 2;
                    } else if (target instanceof HistoryPage) {
                        pos = 3;
                    }

                    PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                    if (adapter != null) {
                        adapter.notifyItemChanged(pos);
                    }
                }));

        return pageInfo;
    }

    private void searchBooks() {
        mDisposable.add(mBookStore.search(mQuery, mCurrSearchPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((bookDatabase, throwable) -> {
                    AppLog.d("total = " + bookDatabase.getTotal());
                    if (mLoadingDialog.isShowing()) mLoadingDialog.dismiss();
                    mIsSearchAvailable = bookDatabase.getTotal() != 0;

                    PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                    if (adapter != null) {
                        adapter.addBooks(PageType.SEARCH.ordinal(), bookDatabase.getBooks());
                    }
                }));
    }

    private void startBookDetailActivity(View view, Book book) {
        boolean isAdded = false;
        List<Book> history = mPageInfoList.get(PageType.HISTORY.ordinal()).getBookList();
        if (history != null) {
            for (Book b : history) {
                if (b.getIsbn13().equals(book.getIsbn13())) {
                    isAdded = true;
                    break;
                }
            }
        }

        if (!isAdded) {
            mPageInfoList.get(PageType.HISTORY.ordinal()).addBook(book);
        }

        mDisposable.add(mBookStore.getDetails(book.getIsbn13())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookDetails -> {
                    Pair<View, String> pair = Pair.create(view, view.getTransitionName());
                    ActivityOptionsCompat optionsCompat
                            = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pair);

                    Intent i = new Intent(MainActivity.this, BookDetailActivity.class);
                    i.putExtra(LibraryConst.KEY_THUMBNAIL, bookDetails);
                    i.putExtra(LibraryConst.KEY_BOOKMARK, book.isBookmarked());
                    startActivity(i, optionsCompat.toBundle());
                }));
    }
}
