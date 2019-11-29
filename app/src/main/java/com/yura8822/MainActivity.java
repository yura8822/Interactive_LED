package com.yura8822;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.yura8822.bluetooth.BluetoothFragment;

public class MainActivity extends AppCompatActivity implements PixelGird.ListenerPixelGird {
    private final String RESTART_FRAFMENT = "RESTART_FRAGMENT";

    private Toolbar mToolbar;
    private BluetoothFragment mBluetoothFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_state);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mBluetoothFragment = new BluetoothFragment();
            fragmentTransaction.replace(R.id.fragment_container, mBluetoothFragment, RESTART_FRAFMENT);
            fragmentTransaction.commit();
        }else {
            mBluetoothFragment = (BluetoothFragment)getSupportFragmentManager()
                    .findFragmentByTag(RESTART_FRAFMENT);
        }

    }

    @Override
    public void sendArrayGird(int[][] colorList) {
        mBluetoothFragment.sendMessage(colorList);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RESTART_FRAFMENT, true);
    }
}
