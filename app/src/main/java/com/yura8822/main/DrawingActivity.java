package com.yura8822.main;

import androidx.fragment.app.Fragment;

import com.yura8822.SingleFragmentActivity;
import com.yura8822.database.ImageLab;

public class DrawingActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DrawingFragment();
    }

    @Override
    protected void onDestroy() {
        stopBluetooth();
        ImageLab.get(getApplicationContext()).closeDBHelper();
        super.onDestroy();
    }
}
