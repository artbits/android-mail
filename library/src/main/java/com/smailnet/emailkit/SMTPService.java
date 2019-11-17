package com.smailnet.emailkit;

/**
 * SMTP服务类，主要负责发送邮件
 */
public final class SMTPService {

    private EmailKit.Config config;

    SMTPService(EmailKit.Config config) {
        this.config = config;
    }

    /**
     * 发送邮件
     * @param draft Draft对象
     * @param getSendCallback   发送结果回调
     */
    public void send(Draft draft, EmailKit.GetSendCallback getSendCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.send(config, draft, new EmailKit.GetSendCallback() {
                    @Override
                    public void onSuccess() {
                        ObjectManager.getHandler().post(getSendCallback::onSuccess);
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getSendCallback.onFailure(errMsg));
                    }
                }));
    }

}
