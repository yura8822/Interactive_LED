package com.yura8822.gallery_new;

import androidx.fragment.app.Fragment;

import com.yura8822.SingleFragmentActivity;

public class GalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GalleryFragment();
    }
}
