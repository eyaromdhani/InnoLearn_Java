package org.example.Services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppService {

    public static final String ACCOUNT_SID = "your_account_sid";
    public static final String AUTH_TOKEN = "your_auth_token";
    public static final String FROM_NUMBER = "whatsapp:+14155238886"; // Twilio sandbox

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendResetMessage(String toPhone, String token) {
        String resetUrl = "https://innolearn.tn/reset?token=" + token;
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + toPhone),
                new PhoneNumber(FROM_NUMBER),
                "Reset your password here: " + resetUrl
        ).create();
        System.out.println("WhatsApp message SID: " + message.getSid());
    }
}
