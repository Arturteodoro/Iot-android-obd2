package com.example.exerciciodooois

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.math.log

class Discoverability : BroadcastReceiver() {
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if(action!=null){
            if(action==BluetoothAdapter.ACTION_SCAN_MODE_CHANGED){
                when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR)){
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE ->{
                        Log.d("Bluetooth Discoverability","Connectable")
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE->{
                        Log.d("Bluetooth Discoverability","Discoverable")
                    }
                    BluetoothAdapter.SCAN_MODE_NONE->{
                        Log.d("Bluetooth Discoverability","NONE")
                    }
                }
            }
        }
    }

}