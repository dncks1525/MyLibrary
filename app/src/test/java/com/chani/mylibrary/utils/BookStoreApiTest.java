package com.chani.mylibrary.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class BookStoreApiTest {
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

    }
}