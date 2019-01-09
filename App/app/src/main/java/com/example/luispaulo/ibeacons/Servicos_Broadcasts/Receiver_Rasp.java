package com.example.luispaulo.ibeacons.Servicos_Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Luis Paulo on 05/07/2018.
 */

public class Receiver_Rasp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase("execute service")) {
            context.stopService(new Intent(context, Service_RaspII.class));
            context.startService(new Intent(context, Service_RaspII.class));
            Log.d("CAPTURA", "Receiver Ativado.");
        }
    }
}
