package com.chalmers.outphonenum;

/**
 * Created by Chalmers on 2016-05-02 14:19.
 * email:qxinhai@yeah.net
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("TAG", "SmsReceiver..");
        Bundle bundle = intent.getExtras();
        Object[] objects = (Object[]) bundle.get("pdus");
        for (Object obj : objects) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String body = smsMessage.getDisplayMessageBody();
            String address = smsMessage.getDisplayOriginatingAddress();
            long date = smsMessage.getTimestampMillis();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String dateStr = format.format(date);

//            System.out.println(address + " 于  " + dateStr + "给你发了以下内容: " + body);
            Log.d("TAG", "发送人: " + address);
            Log.d("TAG", "内容: " + body);

            String number = "";
            if (address.length() > 11) {
                for (int i = address.length() - 1; i >= 0; i--) {
                    if (Character.isDigit(address.charAt(i))) {
                        number = address.charAt(i) + number;
                        if (number.length() == 11) {
                            break;
                        }
                    }
                }
            }else{
                number = address;
            }

            Log.d("TAG", "number-->" + number);

            MyDatabaseHelper helper = new MyDatabaseHelper(context);
            SQLiteDatabase readableDatabase = helper.getReadableDatabase();
            Cursor cursor = readableDatabase.query("blacklist", null, "number=? and type = ?", new String[]{number, String.valueOf(1)}, null, null, null);

            Log.d("TAG","Cursor: "+cursor.moveToFirst());
            Log.d("TAG","cursor count = " + cursor.getCount());
            if ( cursor.moveToFirst() ) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Uri uri = Uri.parse("content://sms/inbox");// 收信箱
                        Cursor cursor2 = context.getContentResolver().query(uri, new String[]{"_id","body"}, null, null, "date desc");
                        boolean t = cursor2.moveToFirst();
                        Log.d("TAG", "t=" + t);
                        if ( t) {
                            Log.d("TAG","count = " + cursor2.getCount());
                            int id = cursor2.getInt(cursor2.getColumnIndex("_id"));
                            String msg = cursor2.getString(cursor2.getColumnIndex("body"));
                            Log.d("TAG","id = " + id);
                            Log.d("TAG","msg = " + msg);
                            context.getContentResolver().delete(uri, "_id="+id, null);
                            Log.d("TAG", "删除成功...");
                        }
                        cursor2.close();
                    }
                }).start();
//                abortBroadcast();
            }
            cursor.close();
        }
    }

}