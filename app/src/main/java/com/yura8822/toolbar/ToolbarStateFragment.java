package com.yura8822.toolbar;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yura8822.Constants;
import com.yura8822.R;
import com.yura8822.bluetooth.DeviceListActivity;

import java.lang.reflect.Method;


public class ToolbarStateFragment extends Fragment {

    private MenuItem mBT_on;
    private MenuItem mBT_disabled;
    private MenuItem mBT_connected;


    public ToolbarStateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        if (getActivity() != null){
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toolbar_state, container, false);
    }

    // Unregister broadcast listeners
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null){
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBT_on = menu.findItem(R.id.bluetooth_on);
        mBT_disabled = menu.findItem(R.id.bluetooth_disabled);
        mBT_connected = menu.findItem(R.id.bluetooth_connected);

        // check if Bluetooth is turned on and change the visibility of the icons
        if (bluetoothAdapter.isEnabled()){
            mBT_on.setVisible(true);
            mBT_disabled.setVisible(false);
        }else {
            mBT_on.setVisible(false);
            mBT_disabled.setVisible(true);
        }

        mBT_connected.setVisible(false);
        if (bluetoothAdapter.getBondedDevices().size() > 0){
            for (BluetoothDevice device: bluetoothAdapter.getBondedDevices()){
                if (device.getName().equals(Constants.DEVICE_NAME)) {
                    if (isConnected(bluetoothAdapter.getRemoteDevice(device.getAddress()))){
                        mBT_connected.setVisible(true);
                    }
                    break;
                }
            }
        }

        if (getActivity().getClass() == DeviceListActivity.class){
            menu.findItem(R.id.device_list_activity).setVisible(false);
        }

    }

    // BroadcastReceiver that listens for bluetooth adapter status
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    if (state == BluetoothAdapter.STATE_ON){
                        mBT_on.setVisible(true);
                        mBT_disabled.setVisible(false);
                    }else if (state == BluetoothAdapter.STATE_OFF){
                        mBT_on.setVisible(false);
                        mBT_disabled.setVisible(true);
                        mBT_connected.setVisible(false);
                    }
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    mBT_connected.setVisible(true);
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mBT_connected.setVisible(false);
                    break;
            }
        }
    };

    private static boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

