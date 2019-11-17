package com.smailnet.emailkit;

import java.util.List;

/**
 * Folder类，通过文件夹的名称来获取对应的文件夹，从而进行文件夹的操作
 */
public class Folder {

    private EmailKit.Config config;
    private String folderName;

    Folder(EmailKit.Config config, String folderName) {
        this.config = config;
        this.folderName = folderName;
    }

    /**
     * 读取邮箱中全部邮件
     * @param getReceiveCallback    读取结果回调
     */
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

    /**
     * 同步邮件
     * @param localUIDArray 本地已缓存的全部邮件uid
     * @param getSyncCallback   同步结果回调
     */
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

    /**
     * 加载邮件
     * @param lastUID   本地已缓存的邮件中数值最小的uid
     * @param getLoadCallback   加载结果回调
     */
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

    /**
     * 获取邮件数量
     * @param getCountCallback  获取数量结果回调
     */
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

    /**
     * 获取全部的uid
     * @param getUIDListCallback    获取全部uid结果回调
     */
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

    /**
     * 获取该uid对应的邮件消息
     * @param uid   该封邮件的uid
     * @param getMsgCallback    获取消息结果回调
     */
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

    /**
     * 获取一组uid对应的多封邮件消息
     * @param uidList   多封邮件的uid
     * @param getMsgListCallback    获取消息结果回调
     */
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

    /**
     * 使用操作邮件消息功能
     * @return
     */
    public Operate operator() {
        return new Operate(folderName, config);
    }

}
