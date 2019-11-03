package com.smailnet.emailkit;

/**
 * DraftBox类，主要是对邮箱的草稿箱进行操作，但不保证该类
 * 对所有邮箱都适配
 */
public final class DraftBox extends BasisFolderImpl {

    private EmailKit.Config config;
    private String folderName;

    DraftBox(EmailKit.Config config, String folderName) {
        super(config, folderName);
        this.config = config;
        this.folderName = folderName;
    }

    /**
     * 删除该封草稿邮件消息
     * @param uid   该封草稿邮件消息的uid
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
     * 批量删除草稿邮件消息
     * @param uidList   多封草稿邮件的uid
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

    /**
     * 保存该封草稿邮件消息到邮箱的草稿箱
     * @param draft Draft对象
     * @param getOperateCallback 保存结果回调
     */
    public void saveMsg(Draft draft, EmailKit.GetOperateCallback getOperateCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.saveToDraft(config, draft, new EmailKit.GetOperateCallback() {
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
