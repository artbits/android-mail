package com.smailnet.emailkit;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * IMAP服务，主要是读取邮箱中的文件夹
 */
public final class IMAPService {

    private EmailKit.Config config;

    IMAPService(EmailKit.Config config) {
        this.config = config;
    }

    /**
     * 获取收件箱
     * @return  Inbox对象
     */
    public Inbox getInbox() {
        return new Inbox(config, "INBOX");
    }

    /**
     * 获取草稿箱
     * @return  DraftBox对象
     */
    public DraftBox getDraftBox() {
        return new DraftBox(config, "Drafts");
    }

    /**
     * 获取指定文件夹
     * @param folderName    该文件夹的名称
     * @return  Folder对象
     */
    public Folder getFolder(String folderName) {
        return new Folder(config, folderName);
    }

    /**
     * 通过回调的方式获取默认的文件夹列表
     * @param getFolderListCallback 获取结果回调
     */
    public void getDefaultFolderList(EmailKit.GetFolderListCallback getFolderListCallback) {
        ObjectManager.getMultiThreadService()
                .execute(() -> EmailCore.getDefaultFolderList(config, new EmailKit.GetFolderListCallback() {
                    @Override
                    public void onSuccess(List<String> folderList) {
                        ObjectManager.getHandler().post(() -> getFolderListCallback.onSuccess(folderList));
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        ObjectManager.getHandler().post(() -> getFolderListCallback.onFailure(errMsg));
                    }
                }));
    }

    /**
     * 通过返回值的方式获取默认的文件夹列表
     * @return  文件夹列表
     */
    public List<String> getDefaultFolderList() {
        try {
            Future<List<String>> future = ObjectManager.getMultiThreadService()
                    .submit(() -> EmailCore.getDefaultFolderList(config));
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
