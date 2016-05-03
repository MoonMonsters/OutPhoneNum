package com.chalmers.outphonenum;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * 通话记录
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    private Context context = null;
    private String TAG = "TAG";

    TelephonyManager tm = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        tm = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
        Log.d(TAG, "onReceive");
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PhoneStateListener psl = new PhoneStateListener() {

        // 开始时间
        private long startTime = 0;
        // 结束时间
        private long endTime = 0;

        boolean flag = false;

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                // 空闲（挂断）
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "CALL_STATE_IDLE");
                    // 如果是可发送消息状态
                    if (flag) {
                        // 判断监听是否打开
                        // 延迟
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flag = false;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    flag = true;
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    MyDatabaseHelper helper = new MyDatabaseHelper(context);
                    SQLiteDatabase readableDatabase = helper.getReadableDatabase();
                    //从数据库中查询，判断该号码是否在黑名单中
                    Cursor cursor = readableDatabase.query("blacklist", null, "number=?", new String[]{incomingNumber}, null, null, null);

                    //如果存在
                    if ( cursor.moveToFirst()) {
                        //挂断
                        endCall();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //延迟三秒删除该条通话记录
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ContentResolver resolver = context.getContentResolver();
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                //挂断该电话后，加入把通话记录删除掉
                                Cursor c = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=? and (type=1 or type=3)", new String[]{incomingNumber}, "_id desc limit 1");
                                if (c.moveToFirst()) {
                                    int id = c.getInt(0);
                                    resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{String.valueOf(id)});
                                }
                            }
                        }).start();
                    }
                    break;
            }
        }
    };
}