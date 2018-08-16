package com.min.source.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.min.source.App;

/**
 * Created by minych on 18-8-16.
 */

public class Utils {

    public static void toast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(App.getApplication(), msg, Toast.LENGTH_SHORT)
                .show();
    }

}
