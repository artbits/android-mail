package com.smailnet.emailkit.function;

import com.smailnet.emailkit.EmailKit;

public interface BasisImpl {
    /**
     * 读取邮箱中全部邮件
     * @param getReceiveCallback    读取结果回调
     */
    void receive(EmailKit.GetReceiveCallback getReceiveCallback);

    /**
     * 同步邮件
     * @param localUIDArray 本地已缓存的全部邮件uid
     * @param getSyncCallback   同步结果回调
     */
    void sync(long[] localUIDArray, EmailKit.GetSyncCallback getSyncCallback);

    /**
     * 加载邮件
     * @param lastUID   本地已缓存的邮件中数值最小的uid
     * @param getLoadCallback   加载结果回调
     */
    void load(long lastUID, EmailKit.GetLoadCallback getLoadCallback);

    /**
     * 获取邮件数量
     * @param getCountCallback  获取数量结果回调
     */
    void getMsgCount(EmailKit.GetCountCallback getCountCallback);

    /**
     * 获取全部的uid
     * @param getUIDListCallback    获取全部uid结果回调
     */
    void getUIDList(EmailKit.GetUIDListCallback getUIDListCallback);

    /**
     * 获取该uid对应的邮件消息
     * @param uid   该封邮件的uid
     * @param getMsgCallback    获取消息结果回调
     */
    void getMsg(long uid, EmailKit.GetMsgCallback getMsgCallback);

    /**
     * 获取一组uid对应的多封邮件消息
     * @param uidList   多封邮件的uid
     * @param getMsgListCallback    获取消息结果回调
     */
    void getMsgList(long[] uidList, EmailKit.GetMsgListCallback getMsgListCallback);
}
