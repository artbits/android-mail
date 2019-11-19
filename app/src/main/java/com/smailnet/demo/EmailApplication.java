package com.smailnet.demo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.smailnet.emailkit.EmailKit;
import com.smailnet.microkv.MicroKV;

import org.litepal.LitePal;

public class EmailApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static EmailKit.Config config;

    @Override
    public void onCreate() {
        super.onCreate();
        EmailKit.initialize(this);
        MicroKV.initialize(this);
        LitePal.initialize(this);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static void setConfig(EmailKit.Config config) {
        EmailApplication.config = config;
    }

    public static EmailKit.Config getConfig() {
        return config;
    }

}
