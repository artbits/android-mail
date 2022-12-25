package com.github.artbits.androidmail.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.artbits.androidmail.R;
import com.github.artbits.androidmail.databinding.ActivityDetailsBinding;
import com.github.artbits.androidmail.store.Message;
import com.github.artbits.androidmail.store.UserInfo;
import com.github.artbits.androidmail.Utils;
import com.github.artbits.mailkit.MailKit;

import org.litepal.LitePal;

public class DetailsActivity extends BaseActivity {

    private ActivityDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = setContentView(this, R.layout.activity_details);
        init();
    }


    @Override
    protected void onDestroy() {
        binding.webView.destroy();
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        long uid = getIntent().getLongExtra("uid", -1);
        String folderName = getIntent().getStringExtra("folderName");
        Message message = LitePal.where("folderName = ? and uid = ?", folderName, String.valueOf(uid)).findFirst(Message.class);

        setToolbar(binding.toolbar, "", true);
        binding.subjectText.setText(TextUtils.isEmpty(message.subject) ? "（无主题）" : message.subject);
        binding.fromNicknameText.setText(message.fromNickname);
        binding.fromAddressText.setText(message.fromAddress);
        binding.toNicknameText.setText(message.toNickname);
        binding.toAddressText.setText(message.toAddress);
        binding.dateText.setText(Utils.getDate(message.sentDate));

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setInitialScale(25);
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100 || message.content != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        if (message.content != null) {
            String content = message.content;
            String type = message.type;
            binding.webView.loadDataWithBaseURL(null, adaptScreen(content, type), "text/html", "utf-8", null);
        } else {
            UserInfo userInfo = LitePal.findFirst(UserInfo.class);
            if (userInfo == null) {
                return;
            }
            MailKit.IMAP imap = new MailKit.IMAP(userInfo.toConfig());
            MailKit.IMAP.Folder folder = imap.getFolder(folderName);
            folder.getMsg(uid, msg -> {
                if (msg.mainBody != null) {
                    message.content = msg.mainBody.content;
                    message.type = msg.mainBody.type;
                    message.save();
                    binding.webView.loadDataWithBaseURL(null, adaptScreen(message.content, message.type), "text/html", "utf-8", null);
                }
            }, e -> Utils.toast(this, e.getMessage()));
        }
    }


    private static String adaptScreen(String s, String type) {
        if (type.equals("text/html")) {
            return "<html>\n" +
                    "<head>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                    "</head>\n" +
                    "<body>\n" + s + "</body>\n" +
                    "</html>";
        } else {
            return "<html>\n" +
                    "<head>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<font size=\"3\">" + s + "</font>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

}