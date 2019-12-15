package com.smailnet.demo;

import com.smailnet.demo.table.LocalMsg;
import com.smailnet.emailkit.Message;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据缓存和读取的操作工具类
 */
public class Utils {

    /**
     * 保存消息
     * @param folderName
     * @param msgList
     */
    public static void saveLocalMsgList(String folderName, List<Message> msgList) {
        List<LocalMsg> localMsgList = new ArrayList<>();
        for (Message msg : msgList) {
            String recipientAddress = "", recipientNickname = "";
            List<Message.Recipients.To> toList = msg.getRecipients().getToList();
            if (toList != null && toList.size() > 0) {
                recipientAddress = toList.get(0).getAddress();
                recipientNickname = toList.get(0).getNickname();
            }
            LocalMsg localMsg = new LocalMsg()
                    .setUID(msg.getUID())
                    .setRead(msg.getFlags().isRead())
                    .setSubject(msg.getSubject())
                    .setSenderNickname(msg.getSender().getNickname())
                    .setSenderAddress(msg.getSender().getAddress())
                    .setRecipientAddress(recipientAddress)
                    .setRecipientNickname(recipientNickname)
                    .setDate(msg.getSentDate().getText())
                    .setFolderName(folderName);
            localMsgList.add(localMsg);
        }
        LitePal.saveAll(localMsgList);
    }

    /**
     * 获取本地数据库文件夹中的本地消息
     * @param folderName
     * @return
     */
    public static List<LocalMsg> getLocalMsgList(String folderName) {
        List<LocalMsg> localMsgList = LitePal.where("folderName = ?", folderName).find(LocalMsg.class);
        Collections.sort(localMsgList);
        return localMsgList;
    }

    /**
     * 保存或删除本地消息
     * @param folderName
     * @param msgList
     * @param uidList
     */
    public static void saveOrDelete(String folderName, List<Message> msgList, long[] uidList) {
        List<LocalMsg> localMsgList = LitePal.where("folderName = ?", folderName).find(LocalMsg.class);
        for (long uid : uidList) {
            for (LocalMsg localMsg : localMsgList) {
                if (uid == localMsg.getUID()) {
                    localMsg.delete();
                    localMsgList.remove(localMsg);
                    break;
                }
            }
        }
        saveLocalMsgList(folderName, msgList);
    }

    /**
     * 获取本地消息
     * @param folderName
     * @param uid
     */
    public static LocalMsg getLocalMsg(String folderName, long uid) {
        List<LocalMsg> localMsgList = LitePal
                .where("folderName = ? and uid = ?", folderName, String.valueOf(uid))
                .find(LocalMsg.class, true);
        return localMsgList.get(0);
    }

    /**
     * 获取本地全部UID
     * @param folderName
     * @return
     */
    public static long[] getLocalUIDArray(String folderName) {
        List<LocalMsg> localMsgList = LitePal.where("folderName = ?", folderName).find(LocalMsg.class);
        Collections.sort(localMsgList);
        long[] longs = new long[localMsgList.size()];
        for (int i = 0, size = localMsgList.size(); i < size; i++) {
            longs[i] = localMsgList.get(i).getUID();
        }
        return longs;
    }

}
