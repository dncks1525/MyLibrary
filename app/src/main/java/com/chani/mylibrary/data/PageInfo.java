package com.chani.mylibrary.data;

import com.chani.mylibrary.adapters.PageAdapter;
import com.chani.mylibrary.data.BookDatabase.Book;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public abstract class PageInfo {
    protected String name;
    protected PageType type;
    protected List<Book> bookList;
    protected PublishSubject<List<Book>> bookObservable;
    protected PageAdapter.OnScrollListener onScrollListener;
    protected PageAdapter.OnItemClickListener onItemClickListener;

    public PageInfo(PageType type) {
        this.type = type;
        this.name = type.name();
    }

    public String getName() {
        return name;
    }

    public PageType getType() {
        return type;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> list) {
        bookList = list;
        getBookObservable().onNext(bookList);
    }

    public void addBook(Book book) {
        if (bookList == null)
            bookList = new ArrayList<>();
        bookList.add(book);
        getBookObservable().onNext(bookList);
    }

    public void addBook(List<Book> bookList) {
        if (this.bookList == null)
            this.bookList = new ArrayList<>();
        this.bookList.addAll(bookList);
        getBookObservable().onNext(bookList);
    }

    public void removeBook(Book book) {
        if (bookList != null) {
            bookList.remove(book);
            getBookObservable().onNext(bookList);
        }
    }

    public void clearBook() {
        if (bookList != null) {
            bookList.clear();
            getBookObservable().onNext(bookList);
        }
    }

    public PublishSubject<List<Book>> getBookObservable() {
        if (bookObservable == null)
            bookObservable = PublishSubject.create();
        return bookObservable;
    }

    public PageAdapter.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(PageAdapter.OnScrollListener listener) {
        onScrollListener = listener;
    }

    public PageAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(PageAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public enum PageType {
        NEW,
        SEARCH,
        BOOKMARK,
        HISTORY,
    }
}
