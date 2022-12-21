package com.example.exerciciodooois

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.exerciciodooois.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var binding: ActivityMainBinding
    lateinit var receiver : BluetoothReceiver
    lateinit var receiver2 : Discoverability

    var permission: Boolean = false
    var REQUEST_ACCESS_COARSE_LOCATION = 101
    var it = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        enableDisableBt()
        enableDisableDiscoverability()

        receiver = BluetoothReceiver()
        receiver2 = Discoverability()

        binding.GetPairedDevices.setOnClickListener {
            getPairedDevices()
        }
        binding.DiscoverDevices.setOnClickListener {
            this.it =0
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                when(ContextCompat.checkSelfPermission(
                      baseContext,Manifest.permission.ACCESS_COARSE_LOCATION
                )){
                    PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Runtime Permission")
                        .setMessage("Give Permission")
                        .setNeutralButton("Okay",DialogInterface.OnClickListener { dialog, which ->
                            if(ContextCompat.checkSelfPermission(baseContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                                PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_ACCESS_COARSE_LOCATION)
                            }
                        })
                        .show()
                        .findViewById<TextView>(R.id.message)!!.movementMethod = LinkMovementMethod.getInstance()

                    PackageManager.PERMISSION_GRANTED->{
                        Log.d("discoverDevices","Permission Granted")
                    }
                }
            }
            discoverDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(discoverDeviceReceiver,filter)
        bluetoothAdapter.startDiscovery()
    }

    private val discoverDeviceReceiver = object : BroadcastReceiver(){
        @SuppressLint("MissingPermission", "LongLogTag")
        override fun onReceive(context: Context?, intent: Intent?) {
            var action = ""
            if(intent!=null){
                action = intent.action.toString()
            }
            when(action){
                BluetoothAdapter.ACTION_DISCOVERY_STARTED ->{
                    Log.d("Bluetooth Discovery Started","Discovery Started")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    Log.d("Bluetooth Discovery Finished","Discovery Finished")
                }
                BluetoothDevice.ACTION_FOUND->{
                    val device =
                        intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if(device!=null){
                        Log.d("Bluetooth Device Detail","${device.name}  ${device.address}")
                        it++
                        when(it){
                            1->{binding.device1.text = device.name
                                binding.AddressDevice1.text = device.address
                                binding.device1.setOnClickListener {
                                    device.createBond()
                                }
                            }
                            2->{binding.device2.text = device.name
                                binding.AddressDevice2.text = device.address
                                binding.device2.setOnClickListener {
                                    device.createBond()
                                }
                            }
                            3->{binding.device3.text = device.name
                                binding.AddressDevice3.text = device.address
                                binding.device3.setOnClickListener {
                                    device.createBond()
                                }
                            }
                        }
                        when(device.bondState){
                            BluetoothDevice.BOND_NONE->{
                                Log.d("Bluetooth Bond Status","${device.name} bond none")
                            }
                            BluetoothDevice.BOND_BONDING->{
                                Log.d("Bluetooth Bond Status","${device.name} bond bonding")
                            }
                            BluetoothDevice.BOND_BONDED->{
                                Log.d("Bluetooth Bond Status","${device.name} bonded")
                            }
                        }
                    }

                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getPairedDevices() {
        var arr = bluetoothAdapter.bondedDevices
        Log.d("bondedDevices",arr.size.toString())
        Log.d("bondedDevices",arr.toString())
        for(device in arr){
            Log.d("bondedDevices",device.name+"  "+device.address+"  "+device.bondState)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableDisableDiscoverability() {
        when{
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH_ADVERTISE)==PackageManager.PERMISSION_GRANTED->{

            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_ADVERTISE)->{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE),101)
            }
        }
        binding.Discover.setOnClickListener {
            val discoverIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,20)
            startActivity(discoverIntent)

            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            registerReceiver(receiver2,intentFilter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableDisableBt() {
            when{
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_GRANTED->{

                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT)->{
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),101)
                }
            }

            binding.OnOff.setOnClickListener {
                if(!bluetoothAdapter.isEnabled){
                    bluetoothAdapter.enable()
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivity(intent)

                    val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    registerReceiver(receiver,intentFilter)

                }
                if (bluetoothAdapter.isEnabled){
                    bluetoothAdapter.disable()

                    val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    registerReceiver(receiver,intentFilter)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(receiver2)
    }
}

//discover devices ==> Logcat -> Bluetooth
//On/Off ==> Logcat -> message
// Paired devices ==> Logcat -> bonded
