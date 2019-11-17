package com.smailnet.emailkit;

import com.sun.mail.imap.IMAPFolder;

import java.util.Arrays;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * 操作UID的核心算法类
 */
class UIDHandle {

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

        if (msgList.length == 0)
            return uidArray;

        //判断排序方式
        boolean isSmallToLarge = folder.getUID(msgList[0]) < folder.getUID(msgList[msgList.length-1]);  //由小到大排列

        if (isSmallToLarge && lastUID < 0) {
            for (int i = msgList.length-1, count = 1; i >= 0 && count <= 20; --i, ++count)
                uidArray = Basis.insertUID(uidArray, folder.getUID(msgList[i]));
        } else if (!isSmallToLarge && lastUID < 0) {
            int count = 1;
            for (Message message : msgList) {
                uidArray = Basis.insertUID(uidArray, folder.getUID(message));
                if (++count == 20) break;
            }
        } else if (isSmallToLarge) {
            int index = Basis.searchIndex(folder, msgList, lastUID);
            for (int i = index, count = 1; i >= 0 && count <= 20; --i, ++count)
                uidArray = Basis.insertUID(uidArray, folder.getUID(msgList[i]));
        } else {
            int index = Basis.searchIndex(folder, msgList, lastUID);
            for (int i = index, length = msgList.length, count = 1; i < length && count <= 20; ++i, ++count)
                uidArray = Basis.insertUID(uidArray, folder.getUID(msgList[i]));
        }

        return uidArray;
    }

    /**
     * 对本地已存在的uid进行同步的算法
     * @param folder
     * @param localUIDArray
     * @return
     */
    static HashMap<String, long[]> syncUIDArray(IMAPFolder folder, long[] localUIDArray) throws MessagingException {
        //获取message数组
        Message[] msgList = folder.getMessages();

        //获取本地消息和服务器消息的数组长度
        int localLength = localUIDArray.length;
        int netLength = msgList.length;

        //初始化需要返回的map
        HashMap<String, long[]> map = new HashMap<>();
        long[] newArray = new long[0];
        long[] delArray = new long[0];

        //如果本地一封邮件都没有，则框架什么也做，不会拉取新数据
        if (localLength == 0) {
            map.put("new", newArray);
            map.put("del", delArray);
            return map;
        }

        //服务器没有一封邮件（服务器上的邮件已经被全部删除），则把本地已存储的邮件已删除
        if (netLength == 0) {
            map.put("new", newArray);
            map.put("del", localUIDArray);
            return map;
        }

        //排序本地的uid，由小到大
        Arrays.sort(localUIDArray);
        //判断服务端的message数组的排序方式
        boolean isSmallToLarge = folder.getUID(msgList[0]) < folder.getUID(msgList[netLength-1]);     //由小到大排列

        if (isSmallToLarge) {
            //服务端的消息已全部同步到本地
            if (localLength == netLength && localUIDArray[localLength-1] == folder.getUID(msgList[netLength-1])) {
                map.put("new", newArray);
                map.put("del", delArray);
                return map;
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
                //设置map
                map.put("new", newArray);
                map.put("del", delArray);
                return map;
            }
        } else {
            //服务端的消息已全部同步到本地
            if (localLength == netLength && localUIDArray[localLength-1] == folder.getUID(msgList[0])) {
                map.put("new", newArray);
                map.put("del", delArray);
                return map;
            } else {
                //判断邮件服务器是否有新邮件
                long uid;
                long localMaxUID = localUIDArray[localLength-1];
                for (int i = 0; (uid = folder.getUID(msgList[i])) > localMaxUID; i++) {
                    newArray = Basis.insertUID(newArray, uid);
                }
                //判断邮件服务器是否有已删除的邮件
                for (int i = localLength-1; i >= 0; --i) {
                    long localUID = localUIDArray[i];
                    if (Basis.binarySearch(folder, msgList, localUID) < 0) {
                        delArray = Basis.insertUID(delArray, localUID);
                    }
                }
                //设置map
                map.put("new", newArray);
                map.put("del", delArray);
                return map;
            }
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
         * 查找下标
         *
         * @param folder
         * @param uid
         * @return
         * @throws MessagingException
         */
        private static int searchIndex(IMAPFolder folder, Message[] msgList, long uid) throws MessagingException {
            //查找该uid在message数组中的下标，若该uid不存在则返回刚好比它大的值的下标
            for (int min = 0, max = msgList.length - 1, mid; min <= max; ) {
                mid = (min + max) / 2;
                if (folder.getUID(msgList[mid]) > uid) {
                    if (mid - 1 >= min && folder.getUID(msgList[mid - 1]) < uid)
                        return mid - 1;
                    max = mid - 1;
                } else if (folder.getUID(msgList[mid]) < uid) {
                    if (mid + 1 <= max && folder.getUID(msgList[mid + 1]) > uid)
                        return mid + 1;
                    min = mid + 1;
                } else {
                    return mid - 1;
                }
            }
            return -1;
        }

    }

}
