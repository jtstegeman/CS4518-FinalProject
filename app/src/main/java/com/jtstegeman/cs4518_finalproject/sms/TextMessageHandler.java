package com.jtstegeman.cs4518_finalproject.sms;

import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Created by kyle on 2/23/18.
 */

public class TextMessageHandler {

    /**
     * Send an SMS message to each of the phone numbers supplied.
     * @param phoneNos The phone numbers to send the message to.
     * @param message The message to send.
     */
    public static void sendSMS(List<String> phoneNos, String message){
        for (String phoneNo : phoneNos) {
            sendSMS(phoneNo, message);
        }
    }

    /**
     * Send an SMS message to the phone number supplied.
     * @param phoneNo The phone number to send the message to.
     * @param message The message to send.
     */
    public static void sendSMS(String phoneNo, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        if (message.length() > 70) {
            List<String> contents = smsManager.divideMessage(message);
            for (String str : contents) {
                smsManager.sendTextMessage(phoneNo, null, str, null, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
        Log.i("TextMessageHandler", "Sent SMS to " + phoneNo);

    }
}
