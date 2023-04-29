package com.github.artbits.androidmail.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.github.artbits.androidmail.App;
import com.github.artbits.androidmail.store.UserInfo;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfo userInfo = App.db.collection(UserInfo.class).findFirst();
        Class<?> cls = (userInfo != null) ? MainActivity.class : ConfigActivity.class;
        startActivity(new Intent(this, cls));
        finish();
    }

}
