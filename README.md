# Email for Android
[![](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.google.cn/)
[![](https://jitpack.io/v/mailhu/email.svg)](https://jitpack.io/#mailhu/email)

Email for Android是基于JavaMail封装的电子邮件框架，简化了开发者在Android客户端中编写发送电子邮件的的代码，同时还支持读取邮箱中的邮件。把它集成到你的Android项目中，你只需简单配置邮件服务器的参数，调用一些简易的API，即可完成你所需的功能，所见即所得。

* 相关阅读：
  + [《中国第一封电子邮件》](https://baike.baidu.com/item/%E4%B8%AD%E5%9B%BD%E7%AC%AC%E4%B8%80%E5%B0%81%E7%94%B5%E5%AD%90%E9%82%AE%E4%BB%B6)
  + [《SMTP百度百科》](https://baike.baidu.com/item/SMTP)
  + [《IMAP百度百科》](https://baike.baidu.com/item/imap)
  + [《POP3百度百科》](https://baike.baidu.com/item/POP3)

如果你想阅读2.x版本的文档请点击 [这里](https://github.com/mailhu/email/blob/master/old_doc.md)

# 安装引入
步骤一、将JitPack存储库添加到根目录的build.gradle中：
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
步骤二、在项目的app模块下的build.gradle里加：
```gradle
dependencies {
    implementation 'com.github.mailhu:email:3.0.0'
}
```
注：因为该库内部使用了Java 8新特性，如果你的项目依赖该库在构建时失败，出现如下错误：
```
Invoke-customs are only supported starting with Android O (--min-api 26)
```
在你的项目app.gradle文件中添加如下代码：
```gradle
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

# 使用文档
###  ● 获取联网权限
在Android项目中的AndroidManifest.xml文件中添加联网权限。
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

###  ● 配置邮件服务器的参数
快速配置，目前只支持QQ，Foxmail，网易邮箱
```java
Email.Config config = new Email.Config()
        .setMailType(Email.MailType.QQ)     //选择邮箱类型
        .setAccount("from@qq.com")          //发件人的邮箱
        .setPassword("password");           //发件人邮箱的密码或者授权码
```
自定义配置，自行填写你使用的邮箱的服务器host和port
```java
Email.Config config = new Email.Config()
        .setSMTP("smtp.qq.com", 465)        //设置SMTP发件服务器主机地址和端口
        .setIMAP("imap.qq.com", 993)        //设置IMAP收件服务器主机地址和端口
        .setPOP3("pop.qq.com", 995)         //设置POP3收件服务器主机地址和端口
        .setAccount("from@qq.com")          //发件人的邮箱
        .setPassword("password");           //发件人邮箱的密码或者授权码
```

**注：下面示例代码中的全部回调接口已是从子线程切换回UI线程，可以在里面直接更新UI。**

###  ● 发送邮件
发送邮件setCc( )和setBcc( )方法非必选，可省略；setText( )和setContent( )必需二选一。
```java
Email.getSendService(config)
        .setTo("to@qq.com")             //收件人的邮箱地址
        .setCc("cc@qq.com")             //抄送人的邮箱地址（非必选）
        .setBcc("bcc@qq.com")           //密送人的邮箱地址（非必选）
        .setNickname("小学生")          //设置发信人的昵称
        .setSubject("这是一封测试邮件")  //邮件主题
        .setText("Hello World !")       //邮件正文，若是发送HTML类型的正文用setContent()
        .send(new Email.GotSendCallback() {
            @Override
            public void success() {
                Log.i(TAG, "发送成功！");
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 读取邮箱中的邮件
使用IMAP协议或POP3协议读取邮箱中的内容，以下使用IMAP协议为例
```java
Email.getReceiveService(config)
        .getIMAPService()           //如果你想使用POP3协议，这里改为getPOP3Service()
        .receive(new Email.GotReceiveCallback() {
            @Override
            public void complete(int total) {
                //读取完邮箱的全部邮件会回调这个方法
                Log.i(TAG, "全部邮件数量：" + total);
            }

            @Override
            public void receiving(Message message) {
                //每读取一封邮件立即回调该方法，返回该封邮件的数据
                Log.i(TAG, "标题：" + message.getSubject() + " 时间：" + message.getDate());
            }

            @Override
            public void received(List<Message> messageList) {
                //读取完邮箱的全部邮件会回调这个方法，一次性返回全部邮件的数据
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 获取邮箱中全部邮件的UID
UID是邮箱创建的邮件序号，每个用户邮箱账号的序列号都是独一无二的。使用UID来同步邮件速度很快，每次同步最新的UID下来，再与之前缓存的UID进行比较即可分析哪些邮件是新的，哪些邮件是已被删除的。
```java
Email.getReceiveService(config)
        .getIMAPService()
        .getUIDList(new Email.GotUIDListCallback() {
            @Override
            public void success(long[] uidList) {
                for (long i : uidList) {
                    Log.i(TAG, "UID：" + i);
                }
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 通过UID获取一封邮件数据
下面代码中的getMessage()方法的第一个参数是指该封邮件的UID
```java
Email.getReceiveService(config)
        .getIMAPService()
        .getMessage(870, new Email.GotMessageCallback() {
            @Override
            public void success(Message message) {
                Log.i(TAG, "主题：" + message.getSubject());
                Log.i(TAG, "发信人：" + message.getFrom());
                Log.i(TAG, "收信人：" + message.getTo());
                Log.i(TAG, "日期：" + message.getDate());
                Log.i(TAG, "内容：" + message.getContent());
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 通过一组UID获取多封邮件数据
```java
long[] uidList= new long[]{15, 40, 869, 870};
Email.getReceiveService(config)
        .getIMAPService()
        .getMessageList(uidList, new Email.GotMessageListCallback() {
            @Override
            public void success(List<Message> messageList) {
                //messageList是对应该组UID的邮件数据
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 读取邮箱中全部邮件的数量
使用IMAP协议或POP3协议读取邮箱中的内容，以下使用IMAP协议为例
```java
Email.getReceiveService(config)
        .getIMAPService()           //如果你想使用POP3协议，这里改为getPOP3Service()
        .getMessageCount(new Email.GotCountCallback() {
            @Override
            public void success(int total) {
                Log.i(TAG, "邮箱中邮件总数：" + total);
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 读取邮箱中未读邮件的数量
```java
Email.getReceiveService(config)
        .getIMAPService()
        .getUnreadMessageCount(new Email.GotCountCallback() {
            @Override
            public void success(int total) {
                Log.i(TAG, "未读邮件数：" + total);
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

###  ● 检查邮配置和账号密码是否正确
```java
Email.getExamineService(config)
        .connect(new Email.GotConnectCallback() {
            @Override
            public void success() {
                Log.i(TAG, "连接成功！");
            }

            @Override
            public void failure(String msg) {
                Log.i(TAG, "错误信息：" + msg);
            }
        });
```

# 开启服务与获取授权码
若使用QQ邮箱（其他邮箱参考QQ邮箱），开启服务的步骤：登录QQ邮箱，进入【设置】-【帐户】，把下列服务开启，然后获取授权码。如下图：
<img src="https://github.com/mailhu/email/blob/master/image/image_1.PNG"  height="200" width="600">
<img src="https://github.com/mailhu/email/blob/master/image/image_2.PNG"  height="250" width="600">

# 混淆
```
-dontwarn org.apache.**
-dontwarn com.sun.**
-dontwarn javax.activation.**
-keep class org.apache.** { *;}
-keep class com.sun.** { *;}
-keep class javax.activation.** { *;}
-keep class com.smailnet.email.** { *;}
```

# 更新日志
* Email for Android 3.0.0
  + 对该邮件框架内部的全部代码进行重构。
  + 重新设计该框架的API和回调接口，使其更简单易用
  + 增加获取全部邮件数量的API和未读邮件数量的API
  + 增加通过邮箱UID来同步邮件的API
  + 增加通过一个UID或一组UID来获取邮件的数据
  + 废弃之前版本的子线程切换回UI线程的API设计，3.0.0版本后切回UI线程无需传入Activity参数
  + 3.0.0版本的API在子线程中执行完任务后自动切回UI线程，不再提供是否切换的选择
  + 3.0.0版本不向下兼容，后续更新的大版本都会在该版本基础上进行优化和完善

* Email for Android 2.4.1
  + 修复解析邮件内容出现的bug

* Email for Android 2.4.0
  + 增加检测垃圾邮件发送者的接口

* Email for Android 2.3.2
  + 再次修复ContentUtil类异常

* Email for Android 2.3.1
  + 修复ContentUtil类异常

* Email for Android 2.3
  + 增加设置发信人昵称的接口
  + 增加设置抄送人，密送人的接口
  + 增加设置文本型邮件正文的setText( )接口
  + 设置收件人的接口使用setTo( )代替setReceiver( )，2.3版本后建议使用setTo( )

* Email for Android 2.2
  + 对发送邮件和接收邮件等接口增加一些新特性
  + 优化部分代码

* Email for Android 2.1
  + 增加使用IMAP协议接收邮件的接口
  + 增加检查Host和Port的工具类

* Email for Android 2.0.1
  + 修改个别类和方法的命名

* Email for Android 2.0
  + 增加使用POP3协议接收邮件接口
  + 增加检查邮件服务器是否可连接的接口
  + 重构EmailConfig类
  + 重构使用SMTP协议发送邮件的类
  + 2.0版本是全新版本（不向下兼容）

* Email for Android 1.1
  + 优化和重构代码
  + 增删和修改个别接口

* Email for Android 1.0
  + 只需简单配置，即可发送一封电子邮件  

  
# 联系我
**邮箱：zguanhu@foxmail.com**

# License
```
Copyright 2018 张观湖

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```