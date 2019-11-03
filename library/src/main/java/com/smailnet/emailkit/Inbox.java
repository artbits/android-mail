package com.smailnet.emailkit;

import com.smailnet.emailkit.function.ExtraImpl;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Inbox类，只要对邮箱中收件箱进行操作和新邮件监听
 */
public final class Inbox extends BasisFolderImpl implements ExtraImpl {

    private EmailKit.Config config;
    private String folderName;

    Inbox(EmailKit.Config config, String folderName) {
        super(config, folderName);
        this.config = config;
        this.folderName = folderName;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    /**
     * 设置新邮件消息监听，但不保证所有邮箱都支持该功能
     * @param onMsgListener 新邮件消息监听回调
     */
    public void setMsgListener(EmailKit.OnMsgListener onMsgListener) {
        //每8分钟运行一次该线程
        ObjectManager.getListenerThreadService()
                .scheduleAtFixedRate(() -> EmailCore.monitorNewMsg(config, new EmailKit.OnMsgListener() {
                    @Override
                    public void onMsg(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> onMsgListener.onMsg(msgList));
                    }

                    @Override
                    public void onError(String errMsg) {
                        ObjectManager.getHandler().post(() -> onMsgListener.onError(errMsg));
                    }
                }),0 , 8*60, TimeUnit.SECONDS);
    }

    /**
     * 获取邮件搜索器
     * @return  Search对象
     */
    public Search useSearcher() {
        return new Search(config);
    }

}
