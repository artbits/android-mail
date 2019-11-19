package com.smailnet.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.EmailApplication;
import com.smailnet.demo.IActivity;
import com.smailnet.demo.LocalMsg;
import com.smailnet.demo.Utils;
import com.smailnet.demo.R;
import com.smailnet.demo.controls.Controls;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.emailkit.Message;
import com.smailnet.microkv.MicroKV;

public class WatchActivity extends BaseActivity implements IActivity {

    private String folderName;
    private long uid;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        initView();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initView() {
        Controls.getTitleBar().display(this, "");

        folderName = getIntent().getStringExtra("folderName");
        uid = getIntent().getLongExtra("uid", -1);

        LocalMsg localMsg = Utils.getLocalMsg(folderName, uid);
        MicroKV kv = MicroKV.defaultMicroKV();

        ((TextView) findViewById(R.id.activity_watch_subject_tv)).setText(localMsg.getSubject());
        ((TextView) findViewById(R.id.activity_watch_sender_nickname_tv)).setText(localMsg.getSenderNickname());
        ((TextView) findViewById(R.id.activity_watch_sender_address_tv)).setText(localMsg.getSenderAddress());
        ((TextView) findViewById(R.id.activity_watch_recipient_nickname_tv)).setText("我");
        ((TextView) findViewById(R.id.activity_watch_recipient_address_tv)).setText(kv.getString("account"));
        ((TextView) findViewById(R.id.activity_watch_date_tv)).setText(localMsg.getDate());

        webView = findViewById(R.id.activity_watch_content_wv);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void initData() {
        EmailKit.useIMAPService(EmailApplication.getConfig())
                .getFolder(folderName)
                .getMsg(uid, new EmailKit.GetMsgCallback() {
                    @Override
                    public void onSuccess(Message msg) {
                        String s = msg.getContent().getMainBody();
                        webView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        Controls.toast(errMsg);
                    }
                });
    }

}
