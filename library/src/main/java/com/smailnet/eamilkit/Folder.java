package com.smailnet.eamilkit;

import com.smailnet.eamilkit.function.ExtraImpl;

/**
 * Folder类，通过文件夹的名称来获取对应的文件夹，从而进行文件夹的操作
 */
public final class Folder extends BasisFolderImpl implements ExtraImpl {

    private EmailKit.Config config;
    private String folderName;

    Folder(EmailKit.Config config, String folderName) {
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

}
