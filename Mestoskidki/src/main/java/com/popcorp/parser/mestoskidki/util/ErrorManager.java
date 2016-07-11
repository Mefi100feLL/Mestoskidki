package com.popcorp.parser.mestoskidki.util;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.Error;

public class ErrorManager {

    private static SenderTLS tlsSender = new SenderTLS("mestoskidki.parser.popsuenko@gmail.com", "popsuenkoae16mestoskidki");

    public static void sendError(String subject, String error){
        if (Application.getErrorRepository().update(new Error(subject, error)) == 0){
            Application.getErrorRepository().save(new Error(subject, error));
            //tlsSender.send(subject, error, "mestoskidki.parser.popsuenko@gmail.com", "alexpopsuenko@gmail.com");
        }
    }

    public static void sendError(String error){
        sendError("Mestoskidki Error", error);
    }
}
