package com.yura8822.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yura8822.R;
import com.yura8822.SingleFragmentActivity;
import com.yura8822.database.ImageLab;
import com.yura8822.device_search.DeviceListActivity;
import com.yura8822.gallery_new.GalleryActivity;

public class DrawingActivity extends SingleFragmentActivity {
    private static final String TAG = "DrawingActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;

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
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                Intent intent = new Intent(this, GalleryActivity.class);
                fragment.startActivityForResult(intent, DrawingFragment.REQUEST_LOAD_IMAGE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
            startActivity(new Intent(this, DeviceListActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(this, DeviceListActivity.class));
                }
                break;
            }
        }
    }
}
