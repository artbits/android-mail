<img src="https://github.com/mailhu/email/blob/master/image/title.png"  height="200" width="600">

# Email for Android
[![](https://jitpack.io/v/mailhu/email.svg)](https://jitpack.io/#mailhu/email)

&emsp;&emsp;Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写发送电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务器，即可使用，所见即所得哦！
</br></br></br>

# Update log
### &ensp;Email for Android 1.1
1. 优化内部代码
2. 增删和修改个别API接口

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
    implementation 'com.github.mailhu:email:1.1'
}
```
</br></br></br>

# Instructions
步骤一、在Android项目中的AndroidManifest.xml文件中添加联网权限。
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
步骤二、发送邮件前，先配置发件服务器和登录邮箱，在Java代码中。
```java
//配置服务器和登录邮箱
EmailConfig emailConfig = new EmailConfig()
        .setSendHost("smtp.qq.com")             //设置发件服务器地址，网易邮箱为smtp.163.com
        .setSendPort(465)                       //设置发件服务器端口，网易邮箱为25
        .setAuth(true)                          //设置是否验证
        .setAccount("1234567@qq,com")           //你的邮箱地址
        .setPassword("abcdefg")                 //你的邮箱密码，QQ邮箱该处填授权码
        .login(this, new GetLoginCallback() {   //this是调用该代码的Activity
            @Override
            public void loginSuccess() {
                //登录成功（这里可更新UI）
            }

            @Override
            public void loginFailure(String errorMsg) {
                //登录失败，errorMsg是错误信息（这里可更新UI）
            }
        });
```
步骤三、在Java代码中配置完发件服务器，然后编写发送邮件的代码。
```java
//配置服务器和登录邮箱成功后，获取到emailConfig.getConfigData()，才能成功运行下面的代码
EmailSendClient emailSendClient = new EmailSendClient(emailConfig.getConfigData())
        .setReceiver("9876543@qq.com")              //收件人的邮箱地址
        .setTitle("邮件测试")                        //邮件标题
        .setText("Hello World !")                   //邮件正文
        .sendAsync(this, new GetSendCallback() {    //this是调用该代码的Activity
            @Override
            public void sendSuccess() {
                //发送成功（这里可更新UI）
            }

            @Override
            public void sendFailure(String errorMsg) {
                //发送失败，errorMsg是错误信息（这里可更新UI）
            }
        });
```
步骤四、若使用QQ邮箱发送邮件，登录QQ邮箱，进入【设置】-【帐户】，把下列服务开启，然后获取授权码。如下图：

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