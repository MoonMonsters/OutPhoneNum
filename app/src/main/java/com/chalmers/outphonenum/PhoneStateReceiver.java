package com.chalmers.outphonenum;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
		Log.d(TAG,"onReceive");
	}

	/**
	 * 挂断电话
	 */
	private void endCall()
	{
		Log.d(TAG,"进入endCall()");
		Class<TelephonyManager> c = TelephonyManager.class;
		try
		{
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			Log.e(TAG, "End call.");
			iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
			iTelephony.endCall();
		}
		catch (Exception e)
		{
			Log.e(TAG, "Fail to answer ring call.", e);
		}
	}

	PhoneStateListener psl = new PhoneStateListener() {

		// 开始时间
		private long startTime = 0;
		// 结束时间
		private long endTime = 0;

		boolean flag = false;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			// 空闲（挂断）
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d(TAG,"CALL_STATE_IDLE");
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
				Log.d(TAG,"CALL_STATE_OFFHOOK");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				if(incomingNumber.equals("15700721904")){
					endCall();
				}
				Log.d("TAG",incomingNumber);
				break;
			}
		}
	};
}