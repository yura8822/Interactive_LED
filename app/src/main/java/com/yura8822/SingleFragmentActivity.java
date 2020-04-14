package com.yura8822;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yura8822.bluetooth.BluetoothFragment;
import com.yura8822.database.DataBaseLab;
import com.yura8822.main.DrawingActivity;
import com.yura8822.main.DrawingFragment;

public abstract class SingleFragmentActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothConnected,
        DrawingFragment.OnSendListener {
    private static final String TAG = "SingleFragmentActivity";
    private static final String BLUETOOTH_FRAGMENT_TAG = "com.yura8822.SingleFragmentActivity.BluetoothFragment";



    private BluetoothFragment mBluetoothFragment;

    private boolean mBluetoothEnabled;
    private int mBluetoothConnected;

    private String mDeviceName;

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
        MenuItem bluetoothConnected = menu.findItem(R.id.bluetooth_connected);
        if (mBluetoothEnabled){
            bluetoothOn.setVisible(true);
            bluetoothOff.setVisible(false);
        }else {
            bluetoothOn.setVisible(false);
            bluetoothOff.setVisible(true);
        }

        if (mBluetoothConnected == BluetoothFragment.CONNECTED){
            bluetoothConnected.setVisible(true);
        }else {
            bluetoothConnected.setVisible(false);
        }

        if (this.getClass() != DrawingActivity.class){
            menu.findItem(R.id.device_list).setVisible(false);
            menu.findItem(R.id.save_img_dialog).setVisible(false);
            menu.findItem(R.id.gallery).setVisible(false);
        }

        return true;
    }

    //interface methods OnBluetoothConnected
    @Override
    public void onStateConnected() {
        updateMenu();
    }

    @Override
    public void getNameConnectedDevice(String name) {
        mDeviceName = name;
    }

    private void updateMenu(){
        ActionBar actionBar = getSupportActionBar();
        mBluetoothEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        mBluetoothConnected = mBluetoothFragment.getStateConnected();

        if (actionBar != null){
            if (mBluetoothEnabled){
                switch (mBluetoothConnected){
                    case BluetoothFragment.CONNECTED:{
                        String title = getResources().getString(R.string.state_connected_device);
                        actionBar.setSubtitle(String.format(title, mDeviceName));
                        //save connected device
                        DataBaseLab dataBaseLab = DataBaseLab.get(getApplicationContext());
                        dataBaseLab.deleteDevice();
                        dataBaseLab.insertDevice(mBluetoothFragment.getAddressBT());
                        break;
                    }
                    case BluetoothFragment.CONNECTING:{
                        actionBar.setSubtitle(R.string.state_connecting_device);
                        break;
                    }
                    case BluetoothFragment.NONE:{
                        actionBar.setSubtitle(R.string.state_non_connected_device);
                        break;
                    }
                }
            }else {
                actionBar.setSubtitle(R.string.sate_bluetooth_off);
            }
        }
        SingleFragmentActivity.this.invalidateOptionsMenu();
    }

    //interface method OnSendListener
    @Override
    public void sendToBluetooth(int[][] colorList) {
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


    protected void stopBluetooth(){
        mBluetoothFragment.stop();
    }

    protected void connectDevice(Intent data){
        mBluetoothFragment.connectDevice(data);
    }

    protected void connectDevice(String address){
        mBluetoothFragment.connectDevice(address);
    }

    protected boolean isBluetoothEnabled() {
        return mBluetoothEnabled;
    }
}
