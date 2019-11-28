package com.smailnet.demo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.EmailApplication;
import com.smailnet.demo.IActivity;
import com.smailnet.demo.LocalMsg;
import com.smailnet.demo.R;
import com.smailnet.demo.Utils;
import com.smailnet.demo.adapter.AttachmentAdapter;
import com.smailnet.demo.adapter.item.AttachmentItem;
import com.smailnet.demo.controls.Controls;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.emailkit.Message;
import com.smailnet.microkv.MicroKV;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                        String text = msg.getContent().getMainBody().getText();
                        webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                        setAttachmentList(msg.getContent().getAttachmentList());
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        Controls.toast(errMsg);
                    }
                });
    }

    /**
     * 设置附件
     * @param attachmentList
     */
    private void setAttachmentList(List<Message.Content.Attachment> attachmentList) {
        //初始化附件列表
        AttachmentAdapter adapter = new AttachmentAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.activity_watch_attachment_rv);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //附件列表的item点击事件
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            AttachmentItem item = (AttachmentItem)adapter1.getItem(position);
            Message.Content.Attachment attachment = item.getAttachment();
            File file = attachment.getFile();
            if (file.exists()) {
                openFile(file, attachment.getType());
            } else {
                adapter.setData(position, item.setPoint("加载中..."));
                attachment.download(file1 -> {
                    adapter.setData(position, item.setPoint(""));
                    openFile(file1, attachment.getType());
                });
            }
        });

        //加载附件内容
        List<AttachmentItem> itemList = new ArrayList<>();
        for (Message.Content.Attachment attachment : attachmentList) {
            double size = ((double) attachment.getSize()) / (1024.0 * 1024.0);
            size = ((double)Math.round(size*1000))/1000;
            AttachmentItem item = new AttachmentItem()
                    .setFilename(attachment.getFilename())
                    .setSize(size + " M")
                    .setAttachment(attachment);
            itemList.add(item);
        }
        adapter.setNewData(itemList);
    }

    /**
     * 打开文件
     * @param file
     * @param fileType
     */
    private void openFile(File file, String fileType) {
        Intent intent = new Intent().setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),fileType);
        startActivity(intent);
    }

}
