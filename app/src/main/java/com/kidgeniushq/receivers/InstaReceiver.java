package com.kidgeniushq.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kidgeniushq.services.InstaService;
import com.kidgeniushq.services.WakefulIntentService;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class InstaReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
        context.startService(new Intent(context, InstaService.class));
    }
}
