package com.yura8822.device_search;

import androidx.fragment.app.Fragment;

import com.yura8822.SingleFragmentActivity;

public class DeviceListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeviceListFragment();
    }
}
