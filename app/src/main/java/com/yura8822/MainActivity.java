package com.yura8822;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.yura8822.bluetooth.BluetoothFragment;

public class MainActivity extends AppCompatActivity implements PixelGird.ListenerPixelGird {

    private Toolbar mToolbar;
    private BluetoothFragment mBluetoothFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_state);
        setSupportActionBar(mToolbar);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mBluetoothFragment = new BluetoothFragment();
        fragmentTransaction.replace(R.id.fragment_container, mBluetoothFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void sendArrayGird(int[][] colorList) {
        mBluetoothFragment.sendMessage(colorList);
    }
}
