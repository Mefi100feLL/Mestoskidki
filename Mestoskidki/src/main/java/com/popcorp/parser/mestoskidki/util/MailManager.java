package com.popcorp.parser.mestoskidki.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MailManager {

    private static SenderTLS tlsSender = new SenderTLS("mestoskidki.parser.popsuenko@gmail.com", "popsuenkoae16mestoskidki");

    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));

    public static void sendMail(String subject, String text){
        tlsSender.send(subject, text, "mestoskidki.parser.popsuenko@gmail.com", "alexpopsuenko@gmail.com");
    }

    public static void sendMail(String text){
        sendMail("Mestoskidki", text);
    }
}
