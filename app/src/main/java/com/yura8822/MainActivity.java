package com.yura8822;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.yura8822.bluetooth.BluetoothFragment;
import com.yura8822.bluetooth.DeviceListActivity;

public class MainActivity extends AppCompatActivity {

    private MenuItem mBT_on;
    private MenuItem mBT_disabled;
    private MenuItem mBT_searching;
    private MenuItem mBT_connected;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BluetoothFragment mBluetoothFragment = new BluetoothFragment();
        fragmentTransaction.replace(R.id.fragment_container, mBluetoothFragment);
        fragmentTransaction.commit();

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mBT_on = menu.findItem(R.id.bluetooth_on);
        mBT_disabled = menu.findItem(R.id.bluetooth_disabled);
        mBT_searching = menu.findItem(R.id.bluetooth_searching);
        mBT_connected = menu.findItem(R.id.bluetooth_connected);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mBT_on.setVisible(false);
        mBT_disabled.setVisible(false);
        mBT_searching.setVisible(false);
        mBT_connected.setVisible(false);
        controlVisibleMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.device_list_activity:
            {
                Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                controlVisibleMenu();
            }
        }
    };

    private void controlVisibleMenu(){
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            mBT_on.setVisible(true);
            mBT_disabled.setVisible(false);
            mToolbar.setSubtitle(R.string.bluetooth_on);
        }else
        {
            mBT_on.setVisible(false);
            mBT_disabled.setVisible(true);
            mToolbar.setSubtitle(R.string.bluetooth_disabled);
        }
    }
}
