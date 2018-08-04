/**
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smailnet.eamil;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即
 * 可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 1.0
 */
public class EmailSendConfig {

    protected Properties properties;
    protected Session session;
    protected Message message;
    protected MimeMultipart multipart;

    protected String account, password, host;

    public EmailSendConfig(){
        this.properties = new Properties();
    }

    /**
     * 设置邮件服务器
     *
     * @param host
     * @return
     */
    public EmailSendConfig setHost(String host){
        this.host = host;
        this.properties.put("mail.smtp.host", host);
        return this;
    }

    /**
     * 设置端口
     *
     * @param post
     * @return
     */
    public EmailSendConfig setPost(int post){
        this.properties.put("mail.smtp.post", String.valueOf(post));
        return this;
    }

    /**
     * 是否开启验证
     *
     * @param auth
     * @return
     */
    public EmailSendConfig setAuth(boolean auth){
        this.properties.put("mail.smtp.auth", auth);
        return this;
    }

    /**
     * 设置发件人的邮箱地址
     *
     * @param account
     * @return
     */
    public EmailSendConfig setAccount(String account){
        this.account = account;
        return this;
    }

    /**
     * 设置发件人的邮箱密码
     *
     * @param password
     * @return
     */
    public EmailSendConfig setPassword(String password){
        this.password = password;
        return this;
    }

    /**
     * 保存发件服务器配置
     *
     * @return
     */
    public EmailSendConfig sava(){
        this.session = Session.getInstance(properties);
        this.message = new MimeMessage(session);
        this.multipart = new MimeMultipart("mixed");
        return this;
    }

}
