package com.jangletech.qoogol.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;


public class QoogolApp extends Application {

    private static QoogolApp instance;
    public static SimpleCache simpleCache;
    private LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
    private ExoDatabaseProvider exoDatabaseProvider;
    private long exoPlayerCacheSize = 90 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getFilesDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
        }

        //PDFBoxResourceLoader.init(this);
    }

    public static QoogolApp getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.isNetworkConnected();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
