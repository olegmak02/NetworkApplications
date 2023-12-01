package com.lab4;

import java.nio.charset.StandardCharsets;

public class HttpResp {
    public static boolean isCachable(byte[] mes) {
        String message = new String(mes, StandardCharsets.UTF_8);
        if (!message.split(" ")[1].equals("200")) {
            return false;
        }

        for (String line: message.split("\r\n")) {
            if (line.startsWith("Cache-Control:") && !line.contains("public") && !line.contains("max-age")) {
                return false;
            }
        }
        return true;
    }
}
