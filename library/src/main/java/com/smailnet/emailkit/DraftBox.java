package com.smailnet.emailkit;

/**
 * DraftBox类，主要是对邮箱的草稿箱进行操作，但不保证该类
 * 对所有邮箱都适配
 */
public final class DraftBox extends Folder {

    private EmailKit.Config config;

    DraftBox(EmailKit.Config config, String folderName) {
        super(config, folderName);
        this.config = config;
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
