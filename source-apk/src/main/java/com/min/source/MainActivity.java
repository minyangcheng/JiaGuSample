package com.min.source;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
    }

}
