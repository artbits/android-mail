package com.smailnet.demo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smailnet.demo.R;
import com.smailnet.demo.adapter.item.AttachmentItem;

import java.util.List;

public class AttachmentAdapter extends BaseQuickAdapter<AttachmentItem, BaseViewHolder> {

    public AttachmentAdapter(@Nullable List<AttachmentItem> data) {
        super(R.layout.item_attachment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttachmentItem item) {
        helper.setText(R.id.item_attachment_filename, item.getFilename())
                .setText(R.id.item_attachment_size, item.getSize())
                .setText(R.id.item_attachment_point, item.getPoint())
                .addOnClickListener(R.id.item_attachment_rl);
    }

}
