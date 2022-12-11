package com.example.ourtravel.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "In SMS_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive() called");

//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

    }
}