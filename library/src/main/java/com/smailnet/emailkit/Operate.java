package com.smailnet.emailkit;

/**
 * Operate类用于对邮箱的邮件进行操作
 */
public final class Operate {

    private String folderName;
    private EmailKit.Config config;

    Operate(String folderName, EmailKit.Config config) {
        this.folderName = folderName;
        this.config = config;
    }

    /**
     * 移动邮件消息到指定文件夹
     * @param targetFolderName  目标文件夹名称
     * @param uid   该封邮件的uid
     * @param getOperateCallback 移动结果回调
     */
    public void moveMsg(String targetFolderName, long uid, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.moveMsg(config, folderName, targetFolderName, uid, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 星标邮件消息
     * @param uid   该封邮件的uid
     * @param star  若选择星标则设置为true，否则为false
     * @param getOperateCallback 星标结果回调
     */
    public void starMsg(long uid, boolean star, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.starMsg(config, folderName, uid, star, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 标记消息已读或未读
     * @param uid
     * @param read
     * @param getOperateCallback
     */
    public void readMsg(long uid, boolean read, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.readMsg(config, folderName, uid, read, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 删除邮件消息
     * @param uid   该封邮件的uid
     * @param getOperateCallback 删除结果回调
     */
    public void deleteMsg(long uid, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.deleteMsg(config, folderName, uid, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 批量移动邮件消息到指定文件夹
     * @param targetFolderName  目标文件夹名称
     * @param uidList   多封邮件的uid
     * @param getOperateCallback 移动结果回调
     */
    public void moveMsgList(String targetFolderName, long[] uidList, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.moveMsgList(config, folderName, targetFolderName, uidList, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 批量星标邮件消息
     * @param uidList   多封邮件的uid
     * @param star  若选择星标则设置为true，否则为false
     * @param getOperateCallback 星标结果回调
     */
    public void starMsgList(long[] uidList, boolean star, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.starMsgList(config, folderName, uidList, star, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 批量标记消息已读或未读
     * @param uidList
     * @param read
     * @param getOperateCallback
     */
    public void readMsgList(long[] uidList, boolean read, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.readMsgList(config, folderName, uidList, read, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 批量删除邮件消息
     * @param uidList   多封邮件的uid
     * @param getOperateCallback 删除结果回调
     */
    public void deleteMsgList(long[] uidList, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.deleteMsgList(config, folderName, uidList, new EmailKit.GetOperateCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getOperateCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getOperateCallback.onFailure(errMsg));
                    }
                }));
    }

}
