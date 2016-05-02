package com.chalmers.outphonenum;

/**
 * Created by Chalmers on 2016-05-02 14:19.
 * email:qxinhai@yeah.net
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("TAG","SmsReceiver..");
        Bundle bundle = intent.getExtras();
        Object[] objects = (Object[]) bundle.get("pdus");
        for(Object obj : objects){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
            String body = smsMessage.getDisplayMessageBody();
            String address = smsMessage.getDisplayOriginatingAddress();
            long date = smsMessage.getTimestampMillis();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String dateStr = format.format(date);

            System.out.println(address +" 于  " + dateStr + "给你发了以下内容: " + body);

            abortBroadcast();
            Log.d("TAG","拦截短信...");
        }
    }

}