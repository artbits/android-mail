package com.smailnet.emailkit.function;

import com.smailnet.emailkit.EmailKit;

public interface ExtraImpl {
    /**
     * 移动邮件消息到指定文件夹
     * @param targetFolderName  目标文件夹名称
     * @param uid   该封邮件的uid
     * @param getOperateCallback 移动结果回调
     */
    void moveMsg(String targetFolderName, long uid, EmailKit.GetOperateCallback getOperateCallback);

    /**
     * 星标邮件消息
     * @param uid   该封邮件的uid
     * @param star  若选择星标则设置为true，否则为false
     * @param getOperateCallback 星标结果回调
     */
    void starMsg(long uid, boolean star, EmailKit.GetOperateCallback getOperateCallback);

    /**
     * 删除邮件消息
     * @param uid   该封邮件的uid
     * @param getOperateCallback 删除结果回调
     */
    void deleteMsg(long uid, EmailKit.GetOperateCallback getOperateCallback);

    /**
     * 批量移动邮件消息到指定文件夹
     * @param targetFolderName  目标文件夹名称
     * @param uidList   多封邮件的uid
     * @param getOperateMsgCallback 移动结果回调
     */
    void moveMsgList(String targetFolderName, long[] uidList, EmailKit.GetOperateCallback getOperateMsgCallback);

    /**
     * 批量星标邮件消息
     * @param uidList   多封邮件的uid
     * @param star  若选择星标则设置为true，否则为false
     * @param getOperateCallback 星标结果回调
     */
    void starMsgList(long[] uidList, boolean star, EmailKit.GetOperateCallback getOperateCallback);

    /**
     * 批量删除邮件消息
     * @param uidList   多封邮件的uid
     * @param getOperateCallback 删除结果回调
     */
    void deleteMsgList(long[] uidList, EmailKit.GetOperateCallback getOperateCallback);
}
