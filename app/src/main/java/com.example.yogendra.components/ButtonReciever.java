package com.example.yogendra.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.yogendra.nplayer.MainPlayer;
/**
 * Created by Yogendra on 21-02-2016.
 */
public class ButtonReciever extends BroadcastReceiver {
    private static final String YES_ACTION = "com.example.yogendra.nplayer.YES";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ButtonReceiver","Recieving");

        String actionR=intent.getAction();

        if (YES_ACTION.equals(actionR)){
            Log.i("ReceivingBroadcast","");
        }
    }
}
