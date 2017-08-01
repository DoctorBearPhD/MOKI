package com.example.ian.mobile_oki;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * When I eventually implement Dagger 2, this class will be necessary.
 * Provides Context to those classes that could not otherwise obtain access.
 * <p>
 * Created by Ian on 7/31/2017.
 */

public class OkiApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
