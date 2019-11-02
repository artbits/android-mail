package com.smailnet.eamil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

class ObjectManager {

    //Application的Context对象
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    //全局Handler
    private static Handler handler;
    //Session对象
    private static Session session;
    //Transport对象
    private static Transport transport;
    //IMAPStore对象
    private static IMAPStore store;
    //全局配置对象
    private static EmailKit.Config globalConfig;
    //保存附件的目录路径
    private static String directory;
    //多线程线程池服务
    private static ExecutorService multiThreadService;
    //单线程线程池服务
    private static ExecutorService singleThreadService;
    //消息监听线程池服务
    private static ScheduledExecutorService listenerThreadService;

    /**
     * 设置Context
     * @param context
     */
    static void setContext(Context context) {
        ObjectManager.context = context;

    }

    /**
     * 获取全局Handler
     * @return
     */
    static Handler getHandler() {
        if (ObjectManager.handler == null) {
            ObjectManager.handler = new Handler(Looper.getMainLooper());
        }
        return ObjectManager.handler;
    }

    /**
     * 获取Context
     * @return 返回一个Context对象
     */
    static Context getContext(){
        return context;
    }

    /**
     * 获取全局变量
     * @return
     */
    static EmailKit.Config getGlobalConfig() {
        if (ObjectManager.globalConfig == null) {
            ObjectManager.globalConfig = new EmailKit.Config();
        }
        return ObjectManager.globalConfig;
    }

    /**
     * 设置Session
     * @param session
     */
    static void setSession(Session session) {
        ObjectManager.session = session;
    }

    /**
     * 获取Session
     * @return
     */
    static Session getSession() {
        return session;
    }

    /**
     * 设置Transport
     * @param transport
     */
    static void setTransport(Transport transport) {
        ObjectManager.transport = transport;
    }

    /**
     * 获取Transport
     * @return
     */
    static Transport getTransport() {
        return transport;
    }

    /**
     * 设置IMAPStore
     * @param store
     */
    static void setStore(IMAPStore store) {
        ObjectManager.store = store;
    }

    /**
     * 获取IMAPStore
     * @return
     */
    static IMAPStore getStore() {
        return store;
    }

    /**
     * 销毁对象
     * @throws MessagingException
     */
    static void destroy() throws MessagingException {
        if (transport != null && transport.isConnected()) {
            transport.close();
        }
        if (store != null && store.isConnected()) {
            store.close();
        }
        if (multiThreadService != null && !multiThreadService.isShutdown()) {
            multiThreadService.shutdownNow();
        }
        if (singleThreadService != null && !singleThreadService.isShutdown()) {
            singleThreadService.shutdownNow();
        }
        if (listenerThreadService != null && !listenerThreadService.isShutdown()) {
            listenerThreadService.shutdownNow();
        }
    }

    /**
     * 获取目录路径
     * @return
     */
    static String getDirectory() {
        return directory;
    }

    /**
     * 设置目录路径
     * @param directory
     */
    static void setDirectory(String directory) {
        if (directory == null) {
            ObjectManager.directory = Objects.requireNonNull(context.getExternalFilesDir(""))
                    .getAbsolutePath() + "/attachments/";
            File file = new File(ObjectManager.directory);
            file.mkdir();
        } else if (directory.lastIndexOf('/') == directory.length()-1) {
            ObjectManager.directory = directory;
        } else {
            ObjectManager.directory = directory + "/";
        }
    }

    /**
     * 获取多线程线程池服务
     * @return
     */
    static ExecutorService getMultiThreadService() {
        if (ObjectManager.multiThreadService == null) {
            ObjectManager.multiThreadService = Executors.newFixedThreadPool(3);
        }
        return ObjectManager.multiThreadService;
    }

    /**
     * 获取单线程线程池服务
     * @return
     */
    static ExecutorService getSingleThreadService() {
        if (ObjectManager.singleThreadService == null) {
            ObjectManager.singleThreadService = Executors.newSingleThreadExecutor();
        }
        return ObjectManager.singleThreadService;
    }

    /**
     * 获取监听线程池服务
     * @return
     */
    static ScheduledExecutorService getListenerThreadService() {
        if (ObjectManager.listenerThreadService == null) {
            ObjectManager.listenerThreadService = Executors.newScheduledThreadPool(1);
        }
        return ObjectManager.listenerThreadService;
    }
}
