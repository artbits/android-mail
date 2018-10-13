/*
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

package com.smailnet.eamil.Utils;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * 邮箱地址处理工具
 */
public class AddressUtil {

    /**
     * 对邮件地址进行编码处理
     * @param str
     * @return
     */
    public static String codeConver(String str) {
        try {
            if (str.startsWith("=?GB") || str.startsWith("=?gb")
                    || str.startsWith("=?UTF") || str.startsWith("=?utf")) {
                str = MimeUtility.decodeText(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 对邮件地址的数据类型转换
     * @param address
     * @return
     */
    public static Address[] getInternetAddress(String address){
        int length = (new String[]{address}).length;
        Address[] addresses = new InternetAddress[length];
        try {
            addresses[0] = new InternetAddress(address);
            return addresses;
        } catch (AddressException e) {
            e.printStackTrace();
            return null;
        }
    }
}
