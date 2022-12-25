package com.github.artbits.androidmail.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.github.artbits.androidmail.R;
import com.github.artbits.androidmail.databinding.ActivityFolderBinding;
import com.github.artbits.androidmail.store.Message;
import com.github.artbits.androidmail.store.UserInfo;
import com.github.artbits.androidmail.Utils;
import com.github.artbits.mailkit.MailKit;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderActivity extends BaseActivity {

    private ActivityFolderBinding binding;
    private MessageAdapter adapter;
    private String folderName;

    private long minUID;
    private boolean isEmpty;

    private MailKit.IMAP.Folder folder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = setContentView(this, R.layout.activity_folder);
        init();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        folderName = getIntent().getStringExtra("folderName");
        setToolbar(binding.toolbar, folderName, true);

        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData(refreshLayout);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(refreshLayout);
            }
        });

        adapter = new MessageAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.msgRecyclerView.setLayoutManager(linearLayoutManager);
        binding.msgRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Message message = (Message) adapter.getItem(position);
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra("folderName", folderName)
                    .putExtra("uid", message.uid);
            startActivity(intent);
        });

        List<Message> messages = getMessage(folderName);
        adapter.setNewData(messages);
        isEmpty = (messages.size() == 0);
        minUID = (isEmpty) ? -1 : messages.get(messages.size()-1).uid;

        UserInfo userInfo = LitePal.findFirst(UserInfo.class);
        if (userInfo != null) {
            MailKit.Config config = userInfo.toConfig();
            MailKit.IMAP imap = new MailKit.IMAP(config);
            folder = imap.getFolder(folderName);
        }

        binding.refreshLayout.autoRefresh();
    }


    private void refreshData(RefreshLayout refreshLayout) {
        if (folder == null) return;

        if (isEmpty) {
            folder.load(minUID, msgList -> {
                saveMessages(folderName, msgList);
                List<Message> messages = getMessage(folderName);
                adapter.setNewData(messages);
                isEmpty = false;
                minUID = (msgList.size() == 0) ? minUID : msgList.get(msgList.size()-1).uid;
                refreshLayout.finishRefresh();
            }, e -> {
                Utils.toast(this, e.getMessage());
                refreshLayout.finishRefresh();
            });
        } else {
            long[] localUIDArray = getLocalUIDArray(folderName);
            folder.sync(localUIDArray, (newMsgList, delUIDArray) -> {
                saveMessages(folderName, newMsgList);
                delMessages(folderName, delUIDArray);
                List<Message> messages = getMessage(folderName);
                adapter.setNewData(messages);
                refreshLayout.finishRefresh();
            }, e -> {
                Utils.toast(this, e.getMessage());
                refreshLayout.finishRefresh();
            });
        }
    }


    private void loadData(RefreshLayout refreshLayout) {
        if (folder == null) return;

        folder.load(minUID, msgList -> {
            List<Message> messages = saveMessages(folderName, msgList);
            adapter.addData(messages);
            minUID = (msgList.size() == 0) ? minUID : msgList.get(msgList.size()-1).uid;
            refreshLayout.finishLoadMore();
        }, e -> {
            Utils.toast(this, e.getMessage());
            refreshLayout.finishLoadMore();
        });
    }


    private List<Message> getMessage(String folderName) {
        List<Message> messages = LitePal.where("folderName = ?", folderName).find(Message.class);
        Collections.sort(messages);
        return messages;
    }


    private long[] getLocalUIDArray(String folderName) {
        List<Message> messages = LitePal.where("folderName = ?", folderName).find(Message.class);
        Collections.sort(messages);
        long[] longs = new long[messages.size()];
        for (int i = 0, size = messages.size(); i < size; i++) {
            longs[i] = messages.get(i).uid;
        }
        return longs;
    }


    private void delMessages(String folderName, List<Long> uidList) {
        Map<Long, Message> messageMap = new HashMap<>();
        List<Message> messages = LitePal.where("folderName = ?", folderName).find(Message.class);
        messages.forEach(message -> messageMap.put(message.uid, message));
        uidList.forEach(uid -> {
            Message message = messageMap.get(uid);
            if (message != null) {
                message.delete();
            }
        });
    }


    private List<Message> saveMessages(String folderName, List<MailKit.Msg> msgList) {
        List<Message> messages = new ArrayList<>();
        msgList.forEach(msg -> messages.add(new Message(m -> {
            m.folderName = folderName;
            m.uid = msg.uid;
            m.sentDate = msg.sentDate;
            m.subject = msg.subject;
            m.fromAddress = msg.from.address;
            m.fromNickname = msg.from.nickname;
            m.toAddress = msg.toList.get(0).address;
            m.toNickname = msg.toList.get(0).nickname;
        })));
        LitePal.saveAll(messages);
        return messages;
    }


    private static class MessageAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {

        public MessageAdapter(@Nullable List<Message> data) {
            super(R.layout.item_message, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, Message message) {
            holder.setText(R.id.nickname, message.fromNickname)
                    .setText(R.id.subject, TextUtils.isEmpty(message.subject) ? "（无主题）" : message.subject)
                    .setText(R.id.date, Utils.getDate(message.sentDate));
        }

    }

}