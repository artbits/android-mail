package com.smailnet.demo.table;

import android.support.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite数据库的数据表字段
 */
public class LocalMsg extends LitePalSupport implements Comparable<LocalMsg> {

    private long uid;
    private boolean read;
    private boolean cached;
    private String subject;
    private String senderAddress;
    private String senderNickname;
    private String recipientAddress;
    private String recipientNickname;
    private String date;
    private String folderName;
    private String text;
    private String type;
    private List<LocalFile> localFileList = new ArrayList<>();

    public long getUID() {
        return uid;
    }

    public LocalMsg setUID(long uid) {
        this.uid = uid;
        return this;
    }

    public boolean isRead() {
        return read;
    }

    public LocalMsg setRead(boolean read) {
        this.read = read;
        return this;
    }

    public boolean isCached() {
        return cached;
    }

    public LocalMsg setCached(boolean cached) {
        this.cached = cached;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public LocalMsg setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public LocalMsg setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public LocalMsg setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
        return this;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public LocalMsg setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
        return this;
    }

    public String getRecipientNickname() {
        return recipientNickname;
    }

    public LocalMsg setRecipientNickname(String recipientNickname) {
        this.recipientNickname = recipientNickname;
        return this;
    }

    public String getDate() {
        return date;
    }

    public LocalMsg setDate(String date) {
        this.date = date;
        return this;
    }

    public String getFolderName() {
        return folderName;
    }

    public LocalMsg setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public String getText() {
        return text;
    }

    public LocalMsg setText(String text) {
        this.text = text;
        return this;
    }

    public String getType() {
        return type;
    }

    public LocalMsg setType(String type) {
        this.type = type;
        return this;
    }

    public List<LocalFile> getLocalFileList() {
        return localFileList;
    }

    public LocalMsg setLocalFileList(List<LocalFile> localFileList) {
        this.localFileList = localFileList;
        return this;
    }

    @Override
    public int compareTo(@NonNull LocalMsg localMsg) {
        return (int) (localMsg.getUID() - this.uid);
    }

}
