package com.yura8822.toolbar;


import android.bluetooth.BluetoothAdapter;
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

import com.yura8822.R;
import com.yura8822.bluetooth.DeviceListActivity;


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
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
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
        mBT_on = menu.findItem(R.id.bluetooth_on);
        mBT_disabled = menu.findItem(R.id.bluetooth_disabled);
        mBT_connected = menu.findItem(R.id.bluetooth_connected);
        changeMenuItemVisibilityBT_ON_OFF();
        changeMenuItemVisibilityBT_CONNECTED();

        if (getActivity().getClass() == DeviceListActivity.class){
            menu.findItem(R.id.device_list_activity).setVisible(false);
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    changeMenuItemVisibilityBT_ON_OFF();
                    break;

                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    changeMenuItemVisibilityBT_CONNECTED();
                    break;
            }

        }
    };

    private void changeMenuItemVisibilityBT_ON_OFF(){
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            mBT_on.setVisible(true);
            mBT_disabled.setVisible(false);
        }else {
            mBT_on.setVisible(false);
            mBT_disabled.setVisible(true);
        }
    }

    private void changeMenuItemVisibilityBT_CONNECTED(){
        int state = BluetoothAdapter.getDefaultAdapter().getState();

        if (state == BluetoothAdapter.STATE_CONNECTED){
            mBT_connected.setVisible(true);
        }
        else{
            mBT_connected.setVisible(false);
        }
    }
}

