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

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

/**
 * 邮件内容编码转换工具
 */
public class ContentUtil {

    /**
     * Mime类型判断
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static String getContent(Message message) throws MessagingException, IOException {
        StringBuilder bodytext = new StringBuilder();
        if (message.isMimeType("text/plain")) {
            bodytext.append(String.valueOf(message.getContent()));
        } else if (message.isMimeType("text/html")) {
            bodytext.append(String.valueOf(message.getContent()));
        } else if (message.isMimeType("multipart/*")) {
            Object content = message.getContent();
            if (content instanceof Multipart){
                Multipart multipart = (Multipart) content;
                for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                    bodytext.append(multipart.getBodyPart(i).getContent());
                }
            }
        }
        return bodytext.toString();
    }
}
