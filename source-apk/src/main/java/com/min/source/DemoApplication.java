package com.min.source;

import android.app.Application;
import android.util.Log;

public class DemoApplication extends Application {

    private static Application context;

    public DemoApplication() {
        super();
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SourceApk", "Application onCreate");
    }

    public static Application getApplication() {
        return context;
    }

}