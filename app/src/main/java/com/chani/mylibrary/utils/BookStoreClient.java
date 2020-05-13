package com.chani.mylibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import com.chani.mylibrary.data.BookDatabase;

import java.io.IOException;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class BookStoreClient {
    public static final String BASE_URL = "https://api.itbook.store/1.0/";

    private IBookStore mBookStore;

    public IBookStore getBookStore(Context ctx) {
        if (mBookStore == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(new Cache(ctx.getCacheDir(), 10 * 1024 * 1024))
                    // TODO: 2020-05-10
//                    .addInterceptor(chain -> {
//                        Request request = chain.request();
//                        return chain.proceed((hasNetwork(ctx))
//                                ? request.newBuilder()
//                                        .header("Cache-Control", "public, max-age =" + 5)
//                                        .build()
//                                : request.newBuilder()
//                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
//                                        .build());
//                    })
                    .build();

            mBookStore = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(IBookStore.class);
        }
        return mBookStore;
    }

    private boolean hasNetwork(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService((Context.CONNECTIVITY_SERVICE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return (capabilities != null) &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null) && networkInfo.isConnected();
        }
    }

    public interface IBookStore {
        @GET("new")
        Single<BookDatabase> getNewBooks();

        @GET("books/{isbn13}")
        Single<BookDatabase.BookDetails> getDetails(@Path("isbn13") String isbn13);

        @GET("search/{query}/{page}")
        Single<BookDatabase> search(@Path("query") String query, @Path("page") int page);
    }
}
