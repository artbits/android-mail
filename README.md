<img src="https://github.com/mailhu/email/blob/master/image/title.png"  height="200" width="600">

# Email for Android
[![](https://jitpack.io/v/mailhu/email.svg)](https://jitpack.io/#mailhu/email)

&emsp;&emsp;Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即可使用，所见即所得哦！
</br></br></br>

# Install
步骤一、将 JitPack 存储库添加到构建文件中：
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
    implementation 'com.github.mailhu:email:1.0'
}
```
</br></br></br>

# Instructions
步骤一、在Android项目中的AndroidManifest.xml文件中添加联网权限。
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
步骤二、发送邮件前，先配置发件服务器，在Java代码中。
```java
//配置发件服务器
EmailSendConfig emailSendConfig = new EmailSendConfig()
        .setHost("smtp.qq.com")             //设置服务器地址，网易邮箱为smtp.163.com
        .setPost(465)                       //设置端口，网易邮箱为25
        .setAuth(true)                      //设置是否验证
        .setAccount("xxx@foxmail.com")      //你的邮箱地址
        .setPassword("abcdefg")             //你的邮箱密码，QQ邮箱该处填授权码
        .sava();                            //保存配置
```
步骤三、在Java代码中配置完发件服务器，然后编写发送邮件的代码。
```java
//邮件发送
EmailSendClient emailSendClient = new EmailSendClient(emailSendConfig);
emailSendClient
        .setReceiver("yyy@foxmail.com")         //收件人的邮箱地址
        .setFrom("xxx@foxmail.com")             //发件人的邮箱地址
        .setTitle("邮件测试")                    //邮件标题
        .setText("Hello World !")               //邮件正文
        .sendAsync(new getResultCallback() {    //异步发送邮件，然后回调发送结果
            @Override
            public void getResult(boolean result) {
                Log.i("oversee", (result) ? "发送成功" : "发送失败");
                }
        });
```
步骤四、完成上面三个步骤还不行，还需要检查一下，例如QQ邮箱是否开启POP3/SMTP等服务。登录网页版QQ邮箱，进入【设置】-【帐户】，把POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务选择开启，然后获取授权码，用于第三方邮箱登录。如下图：

<img src="https://github.com/mailhu/email/blob/master/image/image_1.PNG"  height="200" width="600">

<img src="https://github.com/mailhu/email/blob/master/image/image_2.PNG"  height="250" width="600">
</br></br></br>

# License
```
Copyright 2018 Lake Zhang

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
