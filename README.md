# EmailKit for Android
[![](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.google.cn/)
[![](https://jitpack.io/v/mailhu/email.svg)](https://jitpack.io/#mailhu/email)

EmailKit for Android 是以JavaMail类库为基础进行封装的框架，它比JavaMail更简单易用，在使用它开发电子邮件客户端时，还能避免对电子邮件协议不熟悉的烦恼。目前EmailKit支持的电子邮件协议为 SMTP 和 IMAP，它支持的功能有发送邮件，读取邮件，加载邮件，同步邮件，对邮件消息的移动，删除，存草稿等操作。同时对部分邮箱服务提供商所提供的邮箱支持新邮件消息推送，邮件搜索等功能。把它依赖到你的Android项目中，你只需简单配置邮件服务器的参数，调用一些简易的方法，即可完成你所需的功能，所见即所得。

## 链接
+ EmailKit最新文档，请到Wiki里**仔细耐心地**查阅 [最新文档](https://github.com/mailhu/emailkit/wiki)
+ 若你对该框架有什么疑问或提issue前可以先查看一下 [FAQ](https://github.com/mailhu/emailkit/blob/master/doc/FAQ.md)
+ 4.x版本代码和文档改动较大，需要查阅3.x文档请点击 [这里](https://github.com/mailhu/emailkit/blob/master/doc/3.x.md)
+ 2.x版本的文档已没太大意义，仅做考古使用，点击进入 [考古](https://github.com/mailhu/emailkit/blob/master/doc/2.x.md)
+ 你想了解这个框架的历史，可以查看 [更新日志](https://github.com/mailhu/emailkit/blob/master/doc/log.md)

## 安装引入
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
    implementation 'com.github.mailhu:emailkit:4.0.0'
}
```
注：因为该库内部使用了Java 8新特性，可能你的项目依赖该框架在构建时出现如下错误：
```
Invoke-customs are only supported starting with Android O (--min-api 26)
```
你可以在项目的app模块下的build.gradle里加添如下代码：
```gradle
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## 简单示例
以下代码展示如何发送一封简单电子邮件，详细使用方法和想了解更多功能，请查阅Wiki文档。
```java
//初始化框架
EmailKit.initialize(this);

//配置发件人邮件服务器参数
EmailKit.Config config = new EmailKit.Config()
        .setMailType(EmailKit.MailType.FOXMAIL)     //选择邮箱类型，快速配置服务器参数
        .setAccount("from@foxmail.com")             //发件人邮箱
        .setPassword("*********");                  //密码或授权码

//设置一封草稿邮件
Draft draft = new Draft()
        .setNickname("小学生")                      //发件人昵称
        .setTo("to@qq.com")                        //收件人邮箱
        .setSubject("这是一封测试邮件")             //邮件主题
        .setText("Hello world !");                 //邮件正文

//使用SMTP服务发送邮件
EmailKit.useSMTPService(config)
        .send(draft, new EmailKit.GetSendCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "发送成功！");
            }

            @Override
            public void onFailure(String errMsg) {
                Log.i(TAG, "发送失败，错误：" + errMsg);
            }
        });
```

## 混淆
```
-dontwarn com.sun.**
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-keep class com.sun.** { *;}
-keep class javax.mail.** { *;}
-keep class javax.activation.** { *;}
-keep class com.smailnet.emailkit.** { *;}
```

## 联系我
**E-mail：**<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=Zx0AEgYJDxInAQgfCgYOC0kECAo" style="text-decoration:none;"><img src="http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_01.png"/></a>

**微信扫一扫：**

<img src="https://github.com/mailhu/email/blob/master/image/WeChat.png"  height="100" width="100">


## License
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