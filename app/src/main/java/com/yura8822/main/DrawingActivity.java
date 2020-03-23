package com.yura8822.main;

import androidx.fragment.app.Fragment;

import com.yura8822.SingleFragmentActivity;

public class DrawingActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DrawingFragment();
    }
}
