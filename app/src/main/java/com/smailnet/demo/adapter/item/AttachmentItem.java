package com.smailnet.demo.adapter.item;

import com.smailnet.demo.table.LocalFile;
import com.smailnet.emailkit.Message;

public class AttachmentItem {

    private String filename;
    private String size;
    private String point;
    private Message.Content.Attachment attachment;
    private LocalFile localFile;

    public String getFilename() {
        return filename;
    }

    public AttachmentItem setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getSize() {
        return size;
    }

    public AttachmentItem setSize(String size) {
        this.size = size;
        return this;
    }

    public String getPoint() {
        return point;
    }

    public AttachmentItem setPoint(String point) {
        this.point = point;
        return this;
    }

    public Message.Content.Attachment getAttachment() {
        return attachment;
    }

    public AttachmentItem setAttachment(Message.Content.Attachment attachment) {
        this.attachment = attachment;
        return this;
    }

    public LocalFile getLocalFile() {
        return localFile;
    }

    public AttachmentItem setLocalFile(LocalFile localFile) {
        this.localFile = localFile;
        return this;
    }
}
