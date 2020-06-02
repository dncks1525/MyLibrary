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
import com.chani.mylibrary.data.BookData.Book;
import com.chani.mylibrary.data.BookData.BookDetail;
import com.chani.mylibrary.data.BookStoreApi;
import com.chani.mylibrary.data.PageItem;
import com.chani.mylibrary.databinding.ActivityMainBinding;
import com.chani.mylibrary.pages.BookmarkPage;
import com.chani.mylibrary.pages.NewPage;
import com.chani.mylibrary.pages.SearchPage;
import com.chani.mylibrary.utils.AppLog;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.chani.mylibrary.data.PageItem.PageType;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private SearchView mSearchView;

    private AlertDialog mLoadingDialog;
    private SearchRecentSuggestions mSearchSuggestions;
    private InputMethodManager mInputMethodManager;
    private boolean mIsSoftKeyboardShowing;

    private CompositeDisposable mDisposable;
    private BookStoreApi.IBookRepo mBookRepo;
    private List<PageItem> mPageItemList;
    private int mSearchPage;
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

        mDisposable = new CompositeDisposable();
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchSuggestions = new SearchRecentSuggestions(MainActivity.this,
                SearchHistoryProvider.AUTHORITY, SearchHistoryProvider.MODE);

        mBookRepo = new BookStoreApi().getBookRepo(this);

        mPageItemList = Arrays.asList(
                registerPage(PageType.NEW),
                registerPage(PageType.SEARCH),
                registerPage(PageType.BOOKMARK),
                registerPage(PageType.HISTORY)
        );
        mBinding.pager.setAdapter(new PageAdapter(mPageItemList));
        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (mIsSoftKeyboardShowing)
                    mInputMethodManager.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);

                if (mSearchView != null)
                    mSearchView.clearFocus();
            }
        });
        new TabLayoutMediator(mBinding.tab, mBinding.pager, (tab, position) -> {
            tab.setText(mPageItemList.get(position).getType().name());
        }).attach();

        requestNew();
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
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.searchItem);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    mSearchView.clearFocus();
                    mSearchSuggestions.saveRecentQuery(query, null);
                    if (mBinding.pager.getCurrentItem() != PageType.SEARCH.ordinal()) {
                        mBinding.pager.setCurrentItem(PageType.SEARCH.ordinal(), true);
                    }

                    mQuery = query;
                    mSearchPage = 0;
                    mPageItemList.get(PageType.SEARCH.ordinal()).removeAll();
                    requestSearch(mQuery, mSearchPage);

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
        int request;
        switch (item.getItemId()) {
            case R.id.sortTitleAsc:
                request = PageAdapter.REQUEST_SORT_BY_TITLE_ASC;
                break;
            case R.id.sortTitleDsc:
                request = PageAdapter.REQUEST_SORT_BY_TITLE_DSC;
                break;
            case R.id.sortPriceAsc:
                request = PageAdapter.REQUEST_SORT_BY_PRICE_ASC;
                break;
            case R.id.sortPriceDsc:
                request = PageAdapter.REQUEST_SORT_BY_PRICE_DSC;
                break;
            default:
                request = -1;
        }

        if (request > 0) {
            PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
            if (adapter != null) {
                adapter.request(mBinding.pager.getCurrentItem(), request);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (data.hasExtra(MyLibraryConst.KEY_ISBN13)) {
            String isbn13 = data.getStringExtra(MyLibraryConst.KEY_ISBN13);
            boolean isBookmarked = data.getBooleanExtra(MyLibraryConst.KEY_BOOKMARK, false);

            int pos = mBinding.pager.getCurrentItem();
            List<Book> bookList = mPageItemList.get(pos).getBookList();
            if (bookList != null) {
                for (Book book : bookList) {
                    if (book.getIsbn13().equals(isbn13)) {
                        book.setBookmarked(isBookmarked);

                        PageItem pageItem = mPageItemList.get(PageType.BOOKMARK.ordinal());
                        List<Book> list = pageItem.getBookList();
                        if (isBookmarked) {
                            if (list == null || !list.contains(book)) {
                                pageItem.add(book);
                            }
                        } else {
                            if (list != null && list.contains(book)) {
                                pageItem.remove(book);
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
        mSearchSuggestions.clearHistory();
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

    private PageItem registerPage(PageType type) {
        switch (type) {
            case NEW:
                PageItem newPage = new NewPage(PageType.NEW);
                mDisposable.add(newPage.getBookObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(books -> {
                            AppLog.d("NewPage size = " + books.size());
                            PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                            if (adapter != null) {
                                adapter.notifyItemChanged(PageType.NEW.ordinal());
                            }
                        }));
                newPage.setOnItemClickListener(this::requestDetail);
                return newPage;
            case SEARCH:
                PageItem searchPage = new SearchPage(PageType.SEARCH);
                mDisposable.add(searchPage.getBookObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(books -> {
                            AppLog.d("SearchPage size = " + books.size());
                            PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                            if (mSearchPage > 0) {
                                if (adapter != null)
                                    adapter.request(PageType.SEARCH.ordinal(), PageAdapter.REQUEST_LOAD_MORE);
                            } else {
                                if (adapter != null)
                                    adapter.notifyItemChanged(PageType.SEARCH.ordinal());
                            }
                        }));
                searchPage.setOnItemClickListener(this::requestDetail);
                searchPage.setOnScrollListener(() -> {
                    if (mSearchPage >= 0) {
                        requestSearch(mQuery, ++mSearchPage);
                    }
                });
                return searchPage;
            case BOOKMARK:
                PageItem bookmarkPage = new BookmarkPage(PageType.BOOKMARK);
                mDisposable.add(bookmarkPage.getBookObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(books -> {
                            AppLog.d("BookmarkPage size = " + books.size());
                            PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                            if (adapter != null) {
                                adapter.notifyItemChanged(PageType.BOOKMARK.ordinal());
                            }
                        }));
                bookmarkPage.setOnItemClickListener(this::requestDetail);
                return bookmarkPage;
            case HISTORY:
                PageItem historyPage = new pages.HistoryPage(PageType.HISTORY);
                mDisposable.add(historyPage.getBookObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(books -> {
                            AppLog.d("HistoryPage size = " + books.size());
                            PageAdapter adapter = (PageAdapter) mBinding.pager.getAdapter();
                            if (adapter != null) {
                                adapter.notifyItemChanged(PageType.HISTORY.ordinal());
                            }
                        }));
                historyPage.setOnItemClickListener(this::requestDetail);
                return historyPage;
        }

        return null;
    }

    private void requestNew() {
        mDisposable.add(mBookRepo.getNewBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookData -> {
                    List<Book> books = bookData.getBooks();
                    if (books.size() > 0) {
                        mPageItemList.get(PageType.NEW.ordinal()).addAll(books);
                    }
                }));
    }

    private void requestSearch(String query, int page) {
        if (!mLoadingDialog.isShowing()) mLoadingDialog.show();
        mDisposable.add(mBookRepo.search(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookData -> {
                    if (mLoadingDialog.isShowing()) mLoadingDialog.dismiss();

                    List<Book> books = bookData.getBooks();
                    if (books.size() > 0) {
                        mPageItemList.get(PageType.SEARCH.ordinal()).addAll(books);
                    } else {
                        mSearchPage = -1;
                    }
                }));
    }

    private void requestDetail(View view, Book book) {
        PageItem historyPage = mPageItemList.get(PageType.HISTORY.ordinal());
        List<Book> history = historyPage.getBookList();
        if (history == null) {
            historyPage.add(book);
        } else {
            boolean isExisted = false;
            for (Book b : history) {
                if (b.getIsbn13().equals(book.getIsbn13())) {
                    isExisted = true;
                    break;
                }
            }

            if (!isExisted) {
                historyPage.add(book);
            }
        }

        mDisposable.add(mBookRepo.getBookDetail(book.getIsbn13())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookDetail -> {
                    startBookDetailActivity(view, bookDetail, book.isBookmarked());
                }));
    }

    private void startBookDetailActivity(View view, BookDetail bookDetail, boolean isBookmarked) {
        Pair<View, String> pair = Pair.create(view, view.getTransitionName());
        ActivityOptionsCompat optionsCompat
                = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pair);

        Intent i = new Intent(MainActivity.this, BookDetailActivity.class);
        i.putExtra(MyLibraryConst.KEY_THUMBNAIL, bookDetail);
        i.putExtra(MyLibraryConst.KEY_BOOKMARK, isBookmarked);
        startActivity(i, optionsCompat.toBundle());
    }
}
