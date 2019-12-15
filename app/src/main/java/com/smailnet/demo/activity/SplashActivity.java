package com.smailnet.demo.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.smailnet.demo.EmailApplication;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.microkv.MicroKV;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);

        new Handler().postDelayed(() -> {
            MicroKV kv = MicroKV.customize("config", true);
            if (kv.containsKV("account")) {
                EmailKit.Config config = new EmailKit.Config()
                        .setAccount(kv.getString("account"))
                        .setPassword(kv.getString("password"))
                        .setSMTP(kv.getString("smtp_host"), kv.getInt("smtp_port"), kv.getBoolean("smtp_ssl"))
                        .setIMAP(kv.getString("imap_host"), kv.getInt("imap_port"), kv.getBoolean("imap_ssl"));
                EmailApplication.setConfig(config);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, ConfigActivity.class));
            }
            finish();
        }, 1000);
    }
}
