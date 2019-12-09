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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.yura8822.bluetooth.BluetoothFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final String BUNDLE_BLUETOOTH_FRAGMENT = "BluetoothFragment";

    private Toolbar toolbar;
    private BluetoothFragment mBluetoothFragment;

    MenuItem mBT_on;
    MenuItem mBT_disabled;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            mBluetoothFragment = new BluetoothFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mBluetoothFragment)
                    .commit();
        }else {
            mBluetoothFragment = (BluetoothFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState,BUNDLE_BLUETOOTH_FRAGMENT );
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Paint"));
        tabLayout.addTab(tabLayout.newTab().setText("Animation"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new Fragment()).commit();
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new Fragment()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, BUNDLE_BLUETOOTH_FRAGMENT, mBluetoothFragment);
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
}





