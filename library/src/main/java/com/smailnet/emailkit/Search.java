package com.smailnet.emailkit;

import java.util.List;

/**
 * Search类用于对邮箱的邮件进行搜索
 */
public final class Search {

    private EmailKit.Config config;

    Search(EmailKit.Config config) {
        this.config = config;
    }

    /**
     * 通过邮件主题搜索
     * @param subject   主题
     * @param getSearchCallback 搜索结果回调
     */
    public void searchSubject(String subject, EmailKit.GetSearchCallback getSearchCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.searchSubject(config, subject, new EmailKit.GetSearchCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onSuccess(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 通过发件人昵称搜索
     * @param nickname  发件人昵称
     * @param getSearchCallback 搜索结果回调
     */
    public void searchSender(String nickname, EmailKit.GetSearchCallback getSearchCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.searchSender(config, nickname, new EmailKit.GetSearchCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onSuccess(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 通过收件人昵称搜索
     * @param nickname  收件人昵称
     * @param getSearchCallback 搜索结果回调
     */
    public void searchRecipient(String nickname, EmailKit.GetSearchCallback getSearchCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.searchRecipient(config, nickname, new EmailKit.GetSearchCallback() {
                    @Override
                    public void onSuccess(List<Message> msgList) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onSuccess(msgList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getSearchCallback.onFailure(errMsg));
                    }
                }));
    }
}
