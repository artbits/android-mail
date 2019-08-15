package com.smailnet.eamil;

import com.sun.mail.imap.IMAPFolder;

import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

class UIDHandle {

    /**
     * uid列表校对
     * @param original
     * @param messages
     * @param imapFolder
     * @return
     * @throws MessagingException
     */
    static int proofreading(long[] original, Message[] messages, IMAPFolder imapFolder) throws MessagingException {
        //求两个数组的长度
        int originalLength = original.length - 1;
        int length = messages.length - 1;

        //判断是否其中一个数组为空
        if (-1 == originalLength) {
            return SyncType.SYNC_ALL;
        } else if (-1 == length) {
            return SyncType.DELETE_ALL;
        }

        //两个数组都不为空时
        MimeMessage mimeMessage = (MimeMessage) messages[length];
        long lastUid = imapFolder.getUID(mimeMessage);
        long originalLastUid = original[originalLength];
        long referenceUId = -1;

        if (originalLength <= length) {
            mimeMessage = (MimeMessage) messages[originalLength];
            referenceUId = imapFolder.getUID(mimeMessage);
        }

        //判断同步的类型
        if (originalLastUid == lastUid && originalLength == length) {
            return SyncType.NO_SYNC;
        } else if (originalLastUid >= lastUid && originalLength > length) {
            return SyncType.JUST_DELETE;
        } else if (originalLastUid < lastUid && originalLength <= length && originalLastUid == referenceUId) {
            return SyncType.JUST_ADD;
        } else {
            return SyncType.ADD_And_DELETE;
        }
    }

    /**
     * 插入uid
     * @param uidList
     * @param value
     * @return
     */
    static long[] insertUid(long[] uidList, long value) {
        int length = uidList.length + 1;
        int index = uidList.length;
        uidList = Arrays.copyOf(uidList, length);
        uidList[index] = value;
        return uidList;
    }

    /**
     * 删除uid
     * @param arrays
     * @param value
     * @return
     */
    static long[] deleteUid(long[] arrays, long value) {
        long[] longs = new long[]{};
        for (long i : arrays) {
            if (i != value) {
                longs = insertUid(longs, value);
            }
        }
        return longs;
    }

    /**
     * 二分法查找某一个uid
     * @param uidList
     * @param key
     * @return
     */
    static boolean binarySearch(long[] uidList, long key) {
        for (int min = 0, max = uidList.length - 1, mid; min <= max; ) {
            mid = (min + max)/2;
            if (uidList[mid] > key) {
                max = mid - 1;
            } else if(uidList[mid] < key) {
                min = mid + 1;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * UID的同步类型
     */
    interface SyncType {
        int SYNC_ALL = 0;           //下载全部邮件到客户端
        int DELETE_ALL = 1;         //删除客户端的全部邮件
        int NO_SYNC = 2;            //无需同步
        int JUST_DELETE = 3;        //只需删除客户端邮件
        int JUST_ADD = 4;           //只需下载新邮件到客户端
        int ADD_And_DELETE = 5;     //既需要下载新邮件，又要删除客户端的邮件
    }

}
