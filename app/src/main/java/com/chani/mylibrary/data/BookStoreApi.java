package com.chani.mylibrary.data;

import android.content.Context;

import com.chani.mylibrary.data.BookData.BookDetail;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class BookStoreApi {
    private IBookRepo mBookStore;

    public IBookRepo getBookRepo(Context ctx) {
        if (mBookStore == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(new Cache(ctx.getCacheDir(), 10 * 1024 * 1024))
                    .build();

            mBookStore = new Retrofit.Builder()
                    .baseUrl("https://api.itbook.store/1.0/")
                    .client(client)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(IBookRepo.class);
        }
        return mBookStore;
    }

    public interface IBookRepo {
        @GET("new")
        Single<BookData> getNewBooks();

        @GET("books/{isbn13}")
        Single<BookDetail> getBookDetail(@Path("isbn13") String isbn13);

        @GET("search/{query}/{page}")
        Single<BookData> search(@Path("query") String query, @Path("page") int page);
    }
}
