package com.example.flutter_with_native;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import kotlin.jvm.internal.Intrinsics;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "samples.flutter.dev/battery";
    int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private boolean mScanning;

    private String deviceNames = "!!!!!!!!!";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    Semaphore semaphore = new Semaphore(0);

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // This method is invoked on the main thread.
                            if (call.method.equals("getBatteryLevel")) {

                                Log.d("BluetoothSiin", "블루투스 어댑터 생성");
                                Handler tempHandler = new Handler();
                                //int batteryLevel = getBatteryLevel();
                                String abc = bluetoothSetting();
                                result.success(abc);
//                                tempHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            semaphore.acquire();
//
//                                        } catch (InterruptedException e) {
//                                            throw new RuntimeException(e);
//                                        }
//
//                                    }
//                                });

//                                if (batteryLevel != -1) {
//                                    result.success(batteryLevel);
//                                } else {
//                                    result.error("UNAVAILABLE", "Battery level not available.", null);
//                                }
                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }

    // 클래스를 선언
    private PermissionSupport permission;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(permission!=null){
//        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
//            permission.requestPermission();
//        }
//        }
    }


    private String bluetoothSetting() {
        String result = "블루투스 생성 여부";
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding

            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            }, 1);
            result = "Doesn't have BLUETOOTH_CONNECT permission";
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding

                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
                result = "Doesn't have BLUETOOTH_CONNECT permission";
            } else {
                result = "yes yes baby";
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //여기서 스캔하면 되나??

        scanLeDevice(bluetoothAdapter.isEnabled());
        return result + " " + bluetoothAdapter.isEnabled();
    }

    private LeDeviceListAdapter leDeviceListAdapter;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    Log.d("BluetoothSiin", "스캔 시작" );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leDeviceListAdapter.addDevice(device);
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
    private Handler handler;

    private void scanLeDevice(final boolean enable) {
        Context context;
        context = this;
        handler = new Handler();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    Log.d("BluetoothSiin", "스캔 끝" );
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//                    //여기다 여기
//                    bluetoothAdapter.startLeScan(leScanCallback);
//                }
//            }, SCAN_PERIOD);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            bluetoothAdapter.startLeScan(leScanCallback);
          //handler.post(leScanCallback->{},100)

        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    Runnable scan= new Runnable() {
        @Override
        public void run() {

        }
    };

    private int getBatteryLevel() {
        int batteryLevel = -1;
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return batteryLevel;
    }
}
