package com.github.artbits.androidmail;

import android.app.Application;

import com.github.artbits.quickio.api.DB;
import com.github.artbits.quickio.core.Config;
import com.github.artbits.quickio.core.QuickIO;

public class App extends Application {

    public static DB db;


    @Override
    public void onCreate() {
        super.onCreate();
        String dbName = "store";
        String basePath = getExternalFilesDir(null).getAbsolutePath();
        db = QuickIO.usingDB(Config.of(c -> c.path(basePath).name(dbName)));
    }

}
