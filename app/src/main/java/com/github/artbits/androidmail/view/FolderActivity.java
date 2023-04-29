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
import com.github.artbits.androidmail.App;
import com.github.artbits.androidmail.R;
import com.github.artbits.androidmail.Utils;
import com.github.artbits.androidmail.databinding.ActivityFolderBinding;
import com.github.artbits.androidmail.store.Message;
import com.github.artbits.androidmail.store.UserInfo;
import com.github.artbits.mailkit.MailKit;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        UserInfo userInfo = App.db.collection(UserInfo.class).findFirst();
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
        return App.db.collection(Message.class).find(m -> Objects.equals(folderName, m.folderName), opt -> opt.sort("uid", -1));
    }


    private long[] getLocalUIDArray(String folderName) {
        List<Message> messages = App.db.collection(Message.class).find(m -> Objects.equals(folderName, m.folderName));
        long[] longs = new long[messages.size()];
        for (int i = 0, size = messages.size(); i < size; i++) {
            longs[i] = messages.get(i).uid;
        }
        return longs;
    }


    private void delMessages(String folderName, List<Long> uidList) {
        Map<Long, Boolean> map = uidList.stream().collect(Collectors.toMap(uid -> uid, uid -> true));
        App.db.collection(Message.class).delete(m -> {
            boolean b1 = Objects.equals(folderName, m.folderName);
            boolean b2 = Boolean.TRUE.equals(map.getOrDefault(m.uid, false));
            return b1 && b2;
        });
    }


    private List<Message> saveMessages(String folderName, List<MailKit.Msg> msgList) {
        List<Message> messages = msgList.stream().map(msg -> Message.of(m -> {
            m.folderName = folderName;
            m.uid = msg.uid;
            m.sentDate = msg.sentDate;
            m.subject = msg.subject;
            m.fromAddress = msg.from.address;
            m.fromNickname = msg.from.nickname;
            m.toAddress = msg.toList.get(0).address;
            m.toNickname = msg.toList.get(0).nickname;
        })).collect(Collectors.toList());
        App.db.collection(Message.class).save(messages);
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