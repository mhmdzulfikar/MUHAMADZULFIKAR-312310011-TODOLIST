package com.example.uastugas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Cek jika broadcast adalah perubahan status baterai
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra("level", -1);
            Toast.makeText(context, "Battery Level: " + level + "%", Toast.LENGTH_SHORT).show();
        }
    }
}
