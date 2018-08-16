package com.smailnet.eamil.Utils;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class ContentUtil {

    public static String getContent(Message message) throws MessagingException, IOException {
        StringBuilder bodytext = new StringBuilder();
        if (message.isMimeType("text/plain")) {
            bodytext.append((String) message.getContent());
        } else if (message.isMimeType("text/html")) {
            bodytext.append((String) message.getContent());
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0, counts = multipart.getCount(); i < counts; i++) {
                bodytext.append(multipart.getBodyPart(i).getContent());
            }
        }
        return bodytext.toString();
    }

}
