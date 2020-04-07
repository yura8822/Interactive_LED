package com.yura8822.gallery_image;

import androidx.fragment.app.Fragment;

import com.yura8822.SingleFragmentActivity;

public class GalleryActivity extends SingleFragmentActivity {
    private GalleryFragment mGalleryFragment;

    @Override
    protected Fragment createFragment() {
        mGalleryFragment = new GalleryFragment();
        return mGalleryFragment;
    }

    @Override
    public void onBackPressed() {
        if (!mGalleryFragment.isDeleteModeEnabled()){
            super.onBackPressed();
        }
    }
}

