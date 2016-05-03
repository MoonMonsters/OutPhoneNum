package com.chalmers.outphonenum;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class PhoneStateService extends Service {

	private String PHONESTATE = "android.intent.action.PHONE_STATE";
	private static PhoneStateReceiver receiver = new PhoneStateReceiver();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		IntentFilter filter = new IntentFilter();
		filter.addAction(PHONESTATE);
        registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}