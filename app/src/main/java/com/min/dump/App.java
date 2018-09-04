package com.min.dump;

import android.app.Application;

import com.min.dump.util.RootCmd;
import com.min.dump.util.Utils;


/**
 * Created by minych on 18-9-4.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.putString(this, Constants.KEY_SELECT_CLASS, "");
        Utils.putString(this, Constants.KEY_SELECT_APPLICATION, "");
    }

}
