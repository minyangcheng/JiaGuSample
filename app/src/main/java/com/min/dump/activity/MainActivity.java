package com.min.dump.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.min.dump.Constants;
import com.min.dump.R;
import com.min.dump.util.RootCmd;
import com.min.dump.util.Utils;

public class MainActivity extends AppCompatActivity {

    private EditText mClassEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displaySelectedApplication();
        mClassEt = findViewById(R.id.et_class);
        mClassEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Utils.putString(MainActivity.this, Constants.KEY_SELECT_CLASS, s.toString());
            }
        });
        String className = Utils.getString(this, Constants.KEY_SELECT_CLASS, "");
        mClassEt.setText(className);
    }

    public void onClick(View view) {
        startActivityForResult(new Intent(this, ApplicationListActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displaySelectedApplication();
    }

    private void displaySelectedApplication() {
        String packageName = Utils.getString(this, Constants.KEY_SELECT_APPLICATION);
        if (TextUtils.isEmpty(packageName)) return;
        View layout = findViewById(R.id.app);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            ((ImageView) layout.findViewById(R.id.app_icon)).setImageDrawable(info.applicationInfo.loadIcon(pm));
            ((TextView) layout.findViewById(R.id.app_name)).setText(info.applicationInfo.loadLabel(pm));
            ((TextView) layout.findViewById(R.id.package_name)).setText(info.packageName);
            ((TextView) layout.findViewById(R.id.version_name)).setText(info.versionName);
            ((TextView) layout.findViewById(R.id.version_code)).setText(String.valueOf(info.versionCode));
            layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "请选择应用", Toast.LENGTH_SHORT).show();
            Utils.putString(this, Constants.KEY_SELECT_APPLICATION, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RootCmd.execRootCmdSilent("chmod 777 /data/data/com.min.dump/shared_prefs/" + Constants.CONFIG_PRE_NAME + ".xml");
    }

    @Override
    protected void onPause() {
        super.onPause();
        RootCmd.execRootCmdSilent("chmod 777 /data/data/com.min.dump/shared_prefs/" + Constants.CONFIG_PRE_NAME + ".xml");
    }
}
