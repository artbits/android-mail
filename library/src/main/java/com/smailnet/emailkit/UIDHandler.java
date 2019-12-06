package com.smailnet.emailkit;

import com.sun.mail.imap.IMAPFolder;

import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * 操作UID的核心算法类
 */
class UIDHandler {

    /**
     * 加载下一组uid的算法
     * @param folder
     * @param lastUID
     * @return
     * @throws MessagingException
     */
    static long[] nextUIDArray(IMAPFolder folder, long lastUID) throws MessagingException {
        Message[] msgList = folder.getMessages();
        long[] uidArray = new long[0];
        if (msgList.length == 0){
            return uidArray;
        } else if (lastUID < 0) {
            for (int i = msgList.length-1, count = 1; i >= 0 && count <= 20; --i, ++count)
                uidArray = Basis.insertUID(uidArray, folder.getUID(msgList[i]));
            return uidArray;
        } else {
            int index = Basis.searchIndex(folder, msgList, lastUID);
            for (int i = index, count = 1; i >= 0 && count <= 20; --i, ++count)
                uidArray = Basis.insertUID(uidArray, folder.getUID(msgList[i]));
            return uidArray;
        }
    }

    /**
     * 对本地已存在的uid进行同步的算法
     * @param folder
     * @param localUIDArray
     * @return
     */
    static Result syncUIDArray(IMAPFolder folder, long[] localUIDArray) throws MessagingException {
        //获取message数组
        Message[] msgList = folder.getMessages();

        //获取本地消息和服务器消息的数组长度
        int localLength = localUIDArray.length;
        int netLength = msgList.length;

        //初始化数组
        long[] newArray = new long[0];
        long[] delArray = new long[0];

        //如果本地一封邮件都没有，则框架什么也做，不会拉取新数据
        if (localLength == 0) {
            return new Result(newArray, delArray);
        }

        //服务器没有一封邮件（服务器上的邮件已经被全部删除），则把本地已存储的邮件已删除
        if (netLength == 0) {
            return new Result(newArray, localUIDArray);
        }

        //排序本地的uid，由小到大
        Arrays.sort(localUIDArray);

        //服务端的消息已全部同步到本地
        if (localLength == netLength && localUIDArray[localLength-1] == folder.getUID(msgList[netLength-1])) {
            return new Result(newArray, delArray);
        } else {
            //判断邮件服务器是否有新邮件
            long uid;
            long localMaxUID = localUIDArray[localLength-1];
            for (int i = netLength-1; (uid = folder.getUID(msgList[i])) > localMaxUID; --i) {
                newArray = Basis.insertUID(newArray, uid);
            }
            //判断邮件服务器是否有已删除的邮件
            for (long localUID : localUIDArray) {
                if (Basis.binarySearch(folder, msgList, localUID) < 0) {
                    delArray = Basis.insertUID(delArray, localUID);
                }
            }
            return new Result(newArray, delArray);
        }
    }

    /**
     * 基础功能
     */
    private static class Basis {

        /**
         * 插入uid
         *
         * @param srcArray
         * @param value
         * @return
         */
        private static long[] insertUID(long[] srcArray, long value) {
            int srcLength = srcArray.length;
            long[] destArrays = new long[srcLength+1];
            System.arraycopy(srcArray, 0, destArrays, 0, srcLength);
            destArrays[srcLength] = value;
            return destArrays;
        }

        /**
         * 删除uid
         *
         * @param srcArray
         * @param value
         * @return
         */
        private static long[] deleteUID(long[] srcArray, long value) {
            int delPost = Arrays.binarySearch(srcArray, value);
            int srcLength = srcArray.length;
            long[] destArray = new long[srcLength-1];
            if (delPost > 0) {
                System.arraycopy(srcArray, 0, destArray, 0, delPost);
                System.arraycopy(srcArray, delPost+1, destArray, delPost, srcLength-delPost-1);
                return destArray;
            } else if (delPost == 0){
                System.arraycopy(srcArray, 1, destArray, 0, srcLength-1);
                return destArray;
            } else {
                return srcArray;
            }
        }

        /**
         * 折半查找
         * @param folder
         * @param msgList
         * @param uid
         * @return
         * @throws MessagingException
         */
        private static int binarySearch(IMAPFolder folder, Message[] msgList, long uid) throws MessagingException {
            for (int min = 0, max = msgList.length - 1, mid; min <= max; ) {
                mid = (min + max) / 2;
                if (folder.getUID(msgList[mid]) > uid) {
                    max = mid - 1;
                } else if (folder.getUID(msgList[mid]) < uid) {
                    min = mid + 1;
                } else {
                    return mid;
                }
            }
            return -1;
        }

        /**
         * 算法功能：在元素值递增的数组中查找刚比目标uid小的uid的下标
         * uid表 = [1, 2, 3, 4, 5, 6, 8, 9, 10]
         * 下标值: [0, 1, 2, 3, 4, 5, 6, 7, 8 ]
         *
         * 假设目标uid = 11，刚比目标uid小的uid = 10，该uid的index = 8
         * 假设目标uid = 10，刚比目标uid小的uid = 9，该uid的index = 7
         * 假设目标uid = 7，刚比目标uid小的uid = 6，该uid的index = 5
         * 假设目标uid = 0，刚比目标uid小的uid不存在，返回值 = -1
         *
         * @param folder
         * @param msgList
         * @param uid
         * @return
         * @throws MessagingException
         */
        private static int searchIndex(IMAPFolder folder, Message[] msgList, long uid) throws MessagingException {
            for (int low = 0, high = msgList.length - 1, last = high, mid; low <= high; ) {
                mid = (low + high) / 2;
                if (folder.getUID(msgList[mid]) > uid) {
                    high = mid - 1;
                    if (high >= 0 && folder.getUID(msgList[high]) < uid)
                        return high;
                } else if (folder.getUID(msgList[mid]) < uid) {
                    low = mid + 1;
                    if (low == last && folder.getUID(msgList[low]) < uid)
                        return low;
                } else {
                    return mid - 1;
                }
            }
            return -1;
        }

    }

    /**
     * 返回结果类
     */
    static class Result {

        private long[] newArray;
        private long[] delArray;

        Result(long[] newArray, long[] delArray) {
            this.newArray = newArray;
            this.delArray = delArray;
        }

        long[] getNewArray() {
            return newArray;
        }

        long[] getDelArray() {
            return delArray;
        }

    }

}
