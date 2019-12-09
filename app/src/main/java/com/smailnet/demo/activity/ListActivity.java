package com.smailnet.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.EmailApplication;
import com.smailnet.demo.IActivity;
import com.smailnet.demo.R;
import com.smailnet.demo.adapter.MsgAdapter;
import com.smailnet.demo.adapter.item.MsgItem;
import com.smailnet.demo.controls.Controls;
import com.smailnet.demo.LocalMsg;
import com.smailnet.demo.Utils;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.emailkit.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListActivity extends BaseActivity implements IActivity {

    private String folderName;
    private MsgAdapter adapter;

    private long lastUID;
    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        Looper.myQueue().addIdleHandler(() -> {
            initData();
            return false;
        });
    }

    @Override
    public void initView() {
        folderName = getIntent().getStringExtra("folderName");
        Controls.getTitleBar().display(this, folderName);

        SmartRefreshLayout smartRefreshLayout = findViewById(R.id.activity_list_msg_sr);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData(refreshLayout);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(refreshLayout);
            }
        });

        adapter = new MsgAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.activity_list_msg_rv);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            long uid = ((MsgItem) Objects.requireNonNull(adapter.getItem(position))).getUID();
            Intent intent = new Intent(this, WatchActivity.class)
                    .putExtra("folderName", folderName)
                    .putExtra("uid", uid);
            startActivity(intent);
        });
    }

    @Override
    public void initData() {
        List<LocalMsg> localMsgList = Utils.getLocalMsgList(folderName);
        setNewDataListItem(localMsgList);
        isEmpty = (localMsgList.size() == 0);
        lastUID = isEmpty ? -1 : localMsgList.get(localMsgList.size()-1).getUID();
    }

    /**
     * 刷新
     * @param refreshLayout
     */
    private void refreshData(RefreshLayout refreshLayout) {
        if (isEmpty) {
            EmailKit.useIMAPService(EmailApplication.getConfig())
                    .getFolder(folderName)
                    .load(lastUID, new EmailKit.GetLoadCallback() {
                        @Override
                        public void onSuccess(List<Message> msgList) {
                            Utils.saveLocalMsgList(folderName, msgList);
                            List<LocalMsg> localMsgList = Utils.getLocalMsgList(folderName);
                            setNewDataListItem(localMsgList);
                            isEmpty = false;
                            lastUID = (msgList.size()!= 0)? msgList.get(msgList.size()-1).getUID() : lastUID;
                            refreshLayout.finishRefresh();
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Controls.toast(errMsg);
                            refreshLayout.finishRefresh();
                        }
                    });
        } else {
            EmailKit.useIMAPService(EmailApplication.getConfig())
                    .getFolder(folderName)
                    .sync(Utils.getLocalUIDArray(folderName), new EmailKit.GetSyncCallback() {
                        @Override
                        public void onSuccess(List<Message> newMsgList, long[] deletedUID) {
                            Utils.saveOrDelete(folderName, newMsgList, deletedUID);
                            List<LocalMsg> localMsgList = Utils.getLocalMsgList(folderName);
                            setNewDataListItem(localMsgList);
                            refreshLayout.finishRefresh();
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Controls.toast(errMsg);
                            refreshLayout.finishRefresh();
                        }
                    });
        }
    }

    /**
     * 加载
     * @param refreshLayout
     */
    private void loadData(RefreshLayout refreshLayout) {
        EmailKit.useIMAPService(EmailApplication.getConfig())
                .getFolder(folderName)
                .load(lastUID, new EmailKit.GetLoadCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        Utils.saveLocalMsgList(folderName, msgList);
                        addDataListItem(msgList);
                        lastUID = (msgList.size()!= 0)? msgList.get(msgList.size()-1).getUID() : lastUID;
                        refreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        Controls.toast(errMsg);
                        refreshLayout.finishLoadMore();
                    }
                });
    }

    /**
     * 设置item新数据
     * @param localMsgList
     */
    private void setNewDataListItem(List<LocalMsg> localMsgList) {
        List<MsgItem> items = new ArrayList<>();
        for (LocalMsg msg : localMsgList) {
            MsgItem item = new MsgItem()
                    .setUID(msg.getUID())
                    .setSubject(TextUtils.isEmpty(msg.getSubject())? "（无主题）" : msg.getSubject())
                    .setSenderNickname(msg.getSenderNickname())
                    .setDate(msg.getDate());
            items.add(item);
        }
        adapter.setNewData(items);
    }

    /**
     * 追加item新数据
     * @param msgList
     */
    public void addDataListItem(List<Message> msgList) {
        List<MsgItem> items = new ArrayList<>();
        for (Message msg : msgList) {
            MsgItem item = new MsgItem()
                    .setUID(msg.getUID())
                    .setSubject(TextUtils.isEmpty(msg.getSubject())? "（无主题）" : msg.getSubject())
                    .setSenderNickname(msg.getSender().getNickname())
                    .setDate(msg.getSentDate().getText());
            items.add(item);
        }
        adapter.addData(items);
    }

}
