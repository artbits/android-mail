package com.smailnet.eamilkit;

import com.smailnet.eamilkit.function.BasisImpl;

import java.util.List;

public class BasisFolderImpl implements BasisImpl {

    private EmailKit.Config config;
    private String folderName;

    BasisFolderImpl(EmailKit.Config config, String folderName) {
        this.config = config;
        this.folderName = folderName;
    }

    @Override
    public void receive(EmailKit.GetReceiveCallback getReceiveCallback) {
        ObjectManager.getSingleThreadService()
                .execute(() -> EmailCore.receive(config, folderName, new EmailKit.GetReceiveCallback() {
                    @Override
                    public void receiving(Message msg, int index, int total) {
                        ObjectManager.getHandler().post(() -> getReceiveCallback.receiving(msg, index, total));
                    }

                    @Override
                    public void onFinish(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getReceiveCallback.onFinish(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getReceiveCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void sync(long[] localUIDArray, EmailKit.GetSyncCallback getSyncCallback) {
        ObjectManager.getSingleThreadService()
                .execute(() -> EmailCore.sync(config, folderName, localUIDArray, new EmailKit.GetSyncCallback() {
                    @Override
                    public void onSuccess(List<Message> newMsgList, long[] deletedUID) {
                        ObjectManager.getHandler().post(() -> getSyncCallback.onSuccess(newMsgList, deletedUID));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getSyncCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void load(long lastUID, EmailKit.GetLoadCallback getLoadCallback) {
        ObjectManager.getSingleThreadService()
                .execute(() -> EmailCore.load(config, folderName, lastUID, new EmailKit.GetLoadCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getLoadCallback.onSuccess(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getLoadCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void getMsgCount(EmailKit.GetCountCallback getCountCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.getMsgCount(config, folderName, new EmailKit.GetCountCallback() {
                    @Override
                    public void onSuccess(int total, int unread) {
                        ObjectManager.getHandler().post(() -> getCountCallback.onSuccess(total, unread));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getCountCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void getUIDList(EmailKit.GetUIDListCallback getUIDListCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.getUIDList(config, folderName, new EmailKit.GetUIDListCallback() {
                    @Override
                    public void onSuccess(long[] uidList) {
                        ObjectManager.getHandler().post(() -> getUIDListCallback.onSuccess(uidList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getUIDListCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void getMsg(long uid, EmailKit.GetMsgCallback getMsgCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.getMsg(config, folderName, uid, new EmailKit.GetMsgCallback() {
                    @Override
                    public void onSuccess(Message msg) {
                        ObjectManager.getHandler().post(() -> getMsgCallback.onSuccess(msg));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getMsgCallback.onFailure(errMsg));
                    }
                }));
    }

    @Override
    public void getMsgList(long[] uidList, EmailKit.GetMsgListCallback getMsgListCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.getMsgList(config, folderName, uidList, new EmailKit.GetMsgListCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getMsgListCallback.onSuccess(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getMsgListCallback.onFailure(errMsg));
                    }
                }));
    }
}
