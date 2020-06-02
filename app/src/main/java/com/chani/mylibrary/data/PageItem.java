package com.chani.mylibrary.data;

import android.view.View;

import com.chani.mylibrary.data.BookData.Book;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public abstract class PageItem {
    protected PageType type;
    protected List<Book> bookList;
    protected PublishSubject<List<Book>> bookObservable;
    protected OnScrollListener onScrollListener;
    protected OnItemClickListener onItemClickListener;

    public PageItem(PageType type) {
        this.type = type;
    }

    public PageType getType() {
        return type;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void add(Book book) {
        if (bookList == null) bookList = new ArrayList<>();
        bookList.add(book);
        getBookObservable().onNext(bookList);
    }

    public void addAll(List<Book> books) {
        if (bookList == null) bookList = new ArrayList<>();
        bookList.addAll(books);
        getBookObservable().onNext(bookList);
    }

    public void remove(Book book) {
        if (bookList != null) {
            bookList.remove(book);
            getBookObservable().onNext(bookList);
        }
    }

    public void removeAll() {
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

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @FunctionalInterface
    public interface OnScrollListener {
        void onLoadMoreIfExists();
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(View view, Book book);
    }

    public enum PageType {NEW, SEARCH, BOOKMARK, HISTORY}
}
