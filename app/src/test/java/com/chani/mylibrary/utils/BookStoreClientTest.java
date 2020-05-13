package com.chani.mylibrary.utils;

import com.chani.mylibrary.data.BookDatabase.Book;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class BookStoreClientTest {
    private Object mObj = new Object();
    private CompositeDisposable mDisposable;

    @Before
    public void setup() {
        mDisposable = new CompositeDisposable();
    }

    @After
    public void dispose() {
        assertThat(mDisposable.size(), greaterThanOrEqualTo(2));
        if (mDisposable.size() > 0) {
            mDisposable.clear();
        }
    }

    @Test
    public void retrieveTest() throws InterruptedException {
//        BookStoreClient.IBookStore store = new BookStoreClient().getBookStore();
//        mDisposable.add(store.getNewBooks()
//                .subscribeOn(Schedulers.io())
//                .subscribe((bookDatabase, throwable) -> {
//                    int total = bookDatabase.getTotal();
//                    List<Book> bookList = bookDatabase.getBooks();
//                    if (total > 0) {
//                        assertThat(bookList, is(notNullValue()));
//                        assertThat(total, is(bookList.size()));
//
//                        Book book = bookList.get(0);
//                        assertThat(book.getTitle(), is(notNullValue()));
//                        assertThat(book.getSubtitle(), is(notNullValue()));
//                        assertThat(book.getImage(), is(notNullValue()));
//                        assertThat(book.getPrice(), is(notNullValue()));
//                        assertThat(book.getUrl(), is(notNullValue()));
//                        assertThat(book.getIsbn13(), is(notNullValue()));
//
//                        mDisposable.add(store.getDetails(bookList.get(0).getIsbn13())
//                                .subscribeOn(Schedulers.newThread())
//                                .subscribe((bookDetails, throwable1) -> {
//                                    assertThat(bookDetails.getIsbn13(), is(book.getIsbn13()));
//
//                                    assertThat(bookDetails.getAuthors(), is(notNullValue()));
//                                    assertThat(bookDetails.getDesc(), is(notNullValue()));
//                                    assertThat(bookDetails.getError(), is(notNullValue()));
//                                    assertThat(bookDetails.getImage(), is(notNullValue()));
//                                    assertThat(bookDetails.getIsbn10(), is(notNullValue()));
//                                    assertThat(bookDetails.getIsbn13(), is(notNullValue()));
//                                    assertThat(bookDetails.getLanguage(), is(notNullValue()));
//                                    assertThat(bookDetails.getPages(), is(notNullValue()));
//                                    assertThat(bookDetails.getPrice(), is(notNullValue()));
//                                    assertThat(bookDetails.getPublisher(), is(notNullValue()));
//                                    assertThat(bookDetails.getRating(), is(notNullValue()));
//                                    assertThat(bookDetails.getSubtitle(), is(notNullValue()));
//                                    assertThat(bookDetails.getTitle(), is(notNullValue()));
//                                    assertThat(bookDetails.getUrl(), is(notNullValue()));
//                                    assertThat(bookDetails.getYear(), is(notNullValue()));
//
//                                    synchronized (mObj) {
//                                        mObj.notify();
//                                    }
//                                }));
//
//                    } else {
//                        assertThat(bookList, is(nullValue()));
//                        assertThat(bookDatabase.getError(), greaterThan(0));
//                    }
//                }));
//
//        synchronized (mObj) {
//            mObj.wait();
//        }
    }
}