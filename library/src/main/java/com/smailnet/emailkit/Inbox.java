package com.smailnet.emailkit;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Inbox类，只要对邮箱中收件箱进行操作和新邮件监听
 */
public final class Inbox extends Folder {

    private EmailKit.Config config;

    Inbox(EmailKit.Config config, String folderName) {
        super(config, folderName);
        this.config = config;
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
    public Search searcher() {
        return new Search(config);
    }

}
