package com.min.source;

import android.app.Application;

import com.min.source.util.L;

public class App extends Application {

    private static Application context;

    public App() {
        super();
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d("App onCreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.d("App onTerminate");
    }

    public static Application getApplication() {
        return context;
    }

}