package com.smailnet.demo.activity;

import android.content.Intent;
import android.os.Bundle;

import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.IActivity;
import com.smailnet.demo.LocalMsg;
import com.smailnet.demo.R;
import com.smailnet.demo.controls.Controls;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.microkv.MicroKV;

import org.litepal.LitePal;

public class SettingsActivity extends BaseActivity implements IActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    @Override
    public void initView() {
        Controls.getTitleBar().display(this, "调试");

        findViewById(R.id.activity_settings_db_btn)
                .setOnClickListener(v -> {
                    LitePal.deleteAll(LocalMsg.class);
                    Controls.toast("已清除");
                });

        findViewById(R.id.activity_settings_sp_btn)
                .setOnClickListener(v -> {
                    MicroKV.defaultMicroKV().removeKV("folder_name");
                    Controls.toast("已清除");
                });

        findViewById(R.id.activity_settings_logout_btn)
                .setOnClickListener(v -> {
                    LitePal.deleteAll(LocalMsg.class);
                    MicroKV.defaultMicroKV().clear();
                    EmailKit.destroy();
                    Intent intent = new Intent(this, ConfigActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
    }

    @Override
    public void initData() {

    }

}
