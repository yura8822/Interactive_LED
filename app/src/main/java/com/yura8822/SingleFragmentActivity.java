package com.yura8822;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yura8822.bluetooth.BluetoothFragment;
import com.yura8822.device_search.DeviceListActivity;
import com.yura8822.main.DrawingFragment;

public abstract class SingleFragmentActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothConnected,
        DrawingFragment.OnSendListener {
    private static final String TAG = "SingleFragmentActivity";
    private static final String BLUETOOTH_FRAGMENT_TAG = "com.yura8822.SingleFragmentActivity.BluetoothFragment";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;


    private BluetoothFragment mBluetoothFragment;

    private boolean mBluetoothEnabled;
    private boolean mBluetoothConnected;

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        mBluetoothFragment = (BluetoothFragment) fm.findFragmentByTag(BLUETOOTH_FRAGMENT_TAG);
        if (mBluetoothFragment == null){
            mBluetoothFragment = new BluetoothFragment();
            fm.beginTransaction()
                    .add(mBluetoothFragment, BLUETOOTH_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        if (mReceiver != null){
            //register the receiver to determine the status of the bluetooth adapter
            registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            Log.d(TAG, "register receiver");
        }
        updateMenu();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mReceiver != null){
            unregisterReceiver(mReceiver);
            Log.d(TAG, "unregister receiver");
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_single_fragment, menu);

        MenuItem  bluetoothOn = menu.findItem(R.id.bluetooth_on);
        MenuItem bluetoothOff = menu.findItem(R.id.bluetooth_off);
        MenuItem bluetoothConnecned = menu.findItem(R.id.bluetooth_connected);
        if (mBluetoothEnabled){
            bluetoothOn.setVisible(true);
            bluetoothOff.setVisible(false);
        }else {
            bluetoothOn.setVisible(false);
            bluetoothOff.setVisible(true);
        }

        if (mBluetoothConnected){
            bluetoothConnecned.setVisible(true);
        }else {
            bluetoothConnecned.setVisible(false);
        }

        if (this.getClass() == DeviceListActivity.class){
            menu.findItem(R.id.device_list).setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.device_list:{
                checkPermissionsAndStartDeviceList();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStateConnected() {
        updateMenu();
    }

    private void updateMenu(){
        ActionBar actionBar = getSupportActionBar();
        mBluetoothEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        mBluetoothConnected = mBluetoothFragment.getStateConnected();

        if (actionBar != null){
            if (mBluetoothConnected && mBluetoothEnabled){
                actionBar.setSubtitle(R.string.state_connected_device);
            }else if (!mBluetoothConnected && mBluetoothEnabled){
                actionBar.setSubtitle(R.string.state_non_connected_device);
            }else {
                actionBar.setSubtitle(R.string.sate_bluetooth_off);
            }
        }
        SingleFragmentActivity.this.invalidateOptionsMenu();
    }

    @Override
    public void sendToBluetooth(int[][] colorList) {
        send(colorList);
    }

    private void send(int[][] colorList){
        mBluetoothFragment.sendMessage(colorList);
    }

    // BroadcastReceiver that listens for bluetooth adapter status
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    updateMenu();
                    break;
            }
        }
    };

    private void checkPermissionsAndStartDeviceList(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }else {
            startDeviceList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startDeviceList();
                }
                break;
            }
        }
    }

    private void startDeviceList(){
        startActivity(new Intent(this, DeviceListActivity.class));
    }

    protected void stopBluetooth(){
        mBluetoothFragment.stop();
    }

    protected void connectDevice(Intent data){
        mBluetoothFragment.connectDevice(data);
    }

}
