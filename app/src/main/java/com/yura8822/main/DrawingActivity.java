package com.yura8822.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yura8822.R;
import com.yura8822.SingleFragmentActivity;
import com.yura8822.database.DataBaseLab;
import com.yura8822.device_search.DeviceListActivity;
import com.yura8822.gallery_image.GalleryActivity;

import java.util.List;

public class DrawingActivity extends SingleFragmentActivity {
    private static final String TAG = "DrawingActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_DEVICE_ADDRESS = 1;
    private FragmentManager mFragmentManager;
    private static boolean mFirstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mFirstStart = true;
    }

    @Override
    protected Fragment createFragment() {
        return new DrawingFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstStart){
            List<String> device = DataBaseLab.get(getApplicationContext()).getDevice();
            if (device.size() == 1){
                connectDevice(device.get(0));
                Log.d(TAG,"Device mac : " + device.get(0) + " connected");
            }
            mFirstStart = false;
        }
    }

    @Override
    protected void onDestroy() {
        stopBluetooth();
        super.onDestroy();
    }

    private long mPreviousTime = 0;
    private Toast mToast;
    @Override
    public void onBackPressed() {
        if (mPreviousTime + 2000 < System.currentTimeMillis()){
            mPreviousTime = System.currentTimeMillis();
            mToast = Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT);
            mToast.show();
        }else {
            mToast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.device_list:{
                checkPermissionsAndStartDeviceList();
                return true;
            }

            case R.id.save_img_dialog:{
                SaveImageDialog saveImageDialog = new SaveImageDialog();
                saveImageDialog.setTargetFragment(getSupportFragmentManager().
                        findFragmentById(R.id.fragment_container), DrawingFragment.REQUEST_SAVE_IMG);
                saveImageDialog.show(getSupportFragmentManager(), SaveImageDialog.DIALOG_SAVE_IMAGE);
                return true;
            }

            case R.id.gallery:{
                Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                Intent intent = new Intent(this, GalleryActivity.class);
                fragment.startActivityForResult(intent, DrawingFragment.REQUEST_LOAD_IMAGE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK && data == null) {
            return;
        }
        if (requestCode == REQUEST_DEVICE_ADDRESS){
            connectDevice(data);
        }
    }

    private void checkPermissionsAndStartDeviceList(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }else {
            startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_DEVICE_ADDRESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_DEVICE_ADDRESS);
                }
                break;
            }
        }
    }
}
