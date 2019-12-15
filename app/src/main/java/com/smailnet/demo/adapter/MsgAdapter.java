package com.smailnet.demo.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smailnet.demo.R;
import com.smailnet.demo.adapter.item.MsgItem;

import java.util.List;

public class MsgAdapter extends BaseQuickAdapter<MsgItem, BaseViewHolder> {

    public MsgAdapter(@Nullable List<MsgItem> data) {
        super(R.layout.item_message, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgItem item) {
        helper.setText(R.id.item_message_nickname, item.getSenderNickname())
                .setText(R.id.item_message_subject, item.getSubject())
                .setText(R.id.item_message_date, item.getDate());
        ((TextView)helper.getView(R.id.item_message_nickname))
                .getPaint().setFakeBoldText(!item.isRead());
        ((TextView)helper.getView(R.id.item_message_subject))
                .getPaint().setFakeBoldText(!item.isRead());
        ((TextView)helper.getView(R.id.item_message_date))
                .getPaint().setFakeBoldText(!item.isRead());
    }

}
