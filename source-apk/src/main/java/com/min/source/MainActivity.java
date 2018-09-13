package com.min.source;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.JsonObject;
import com.min.source.util.L;
import com.min.source.util.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast("this is sample jiagu");
            }
        });
        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentName = new ComponentName("com.cgw360.cheguo.test", "com.cheguo.framework.base.common.webview.WebViewActivity");
                Intent intent = new Intent();
                intent.setComponent(componentName);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("url", "http://10.10.12.170:8080");
                intent.putExtra("WEBVIEW_PARAMS", jsonObject.toString());
                L.d(jsonObject.toString());
                MainActivity.this.startActivity(intent);
            }
        });
    }

}
