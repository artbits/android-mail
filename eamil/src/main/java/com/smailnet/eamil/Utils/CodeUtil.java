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

import javax.mail.internet.MimeUtility;

/**
 * 邮箱地址编码转换工具
 */
public class CodeUtil {

    public static String conver(String str) {
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
}
