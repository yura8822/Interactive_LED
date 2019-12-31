package com.yura8822;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.yura8822.bluetooth.BluetoothFragment;
import com.yura8822.drawing_field.ColorPickerFragment;
import com.yura8822.drawing_field.PixelGirdFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private BluetoothFragment mBluetoothFragment;
    private PixelGirdFragment mPixelGirdFragment;

    private MenuItem mBT_on;
    private MenuItem mBT_disabled;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothFragment = new BluetoothFragment();
        mPixelGirdFragment = new PixelGirdFragment();
        //registering the listener to send the grid array in case of changes
        mPixelGirdFragment.setFragmentListenerPixelGird(new PixelGirdFragment.FragmentListenerPixelGird() {
            @Override
            public void sendBluetooth(int[][] colorList) {
                mBluetoothFragment.sendMessage(colorList);
            }
        });

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.bluetooth_container, mBluetoothFragment);
        fm.replace(R.id.mode_container, mPixelGirdFragment);
        fm .commit();

        //initializing main tablayout to switch modes
        TabLayout tabLayoutMain = findViewById(R.id.tabs_main);
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Paint"));
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Animation"));
        tabLayoutMain.addOnTabSelectedListener(tabSelectedListenerMain);

        //initializing bottom tablayout to switch modes
        TabLayout tabLayoutBotom = findViewById(R.id.tabs_bottom);
        tabLayoutBotom.addTab(tabLayoutBotom.newTab().setText("Color"));
        tabLayoutBotom.addTab(tabLayoutBotom.newTab().setText("Eraser"));
        tabLayoutBotom.addOnTabSelectedListener(tabSelectedListenerBottom);

        //register the receiver to determine the status of the bluetooth adapter
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (mReceiver != null){
            registerReceiver(mReceiver, filter);
            Log.d(TAG, "register receiver");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null){
            unregisterReceiver(mReceiver);
            Log.d(TAG, "unregister receiver");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mBT_on = menu.findItem(R.id.bluetooth_on);
        mBT_disabled = menu.findItem(R.id.bluetooth_disabled);

        //determine the status of the bluetooth when initializing the menu
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            mBT_on.setVisible(true);
            mBT_disabled.setVisible(false);
        }else {
            mBT_on.setVisible(false);
            mBT_disabled.setVisible(true);
        }

        menu.findItem(R.id.bluetooth_connected).setVisible(false);
        return super.onCreateOptionsMenu(menu);
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
                        Log.d(TAG, "onReceive() bluetooth on");
                    }else if (state == BluetoothAdapter.STATE_OFF){
                        mBT_on.setVisible(false);
                        mBT_disabled.setVisible(true);
                        Log.d(TAG, "onReceive() bluetooth disabled");
                    }
                    break;
            }
        }
    };

    private TabLayout.BaseOnTabSelectedListener tabSelectedListenerMain = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mode_container, mPixelGirdFragment).commit();
                    Log.d(TAG, "replace mPixelGirdFragment");
                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mode_container, new Fragment()).commit();
                    Log.d(TAG, "mode container item 2");
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private TabLayout.BaseOnTabSelectedListener tabSelectedListenerBottom = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    ColorPickerFragment mColorPickerFragment = new ColorPickerFragment();
                    mColorPickerFragment.show(getSupportFragmentManager(), ColorPickerFragment.DIALOG_COLOR_PICKER);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    ColorPickerFragment mColorPickerFragment = new ColorPickerFragment();
                    mColorPickerFragment.show(getSupportFragmentManager(), ColorPickerFragment.DIALOG_COLOR_PICKER);
                    break;
            }
        }
    };
}





