package com.yura8822;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.yura8822.bluetooth.BluetoothFragment;
import com.yura8822.database.GalleryDBHelper;
import com.yura8822.drawing_field.ColorPickerDialog;
import com.yura8822.drawing_field.PaletteLastColorsFragment;
import com.yura8822.drawing_field.PixelGirdFragment;
import com.yura8822.gallery.GalleryImageActivity;
import com.yura8822.gallery.ImageUtils;
import com.yura8822.gallery.SaveImageDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private BluetoothFragment mBluetoothFragment;
    private PixelGirdFragment mPixelGirdFragment;
    private ColorPickerDialog mColorPickerDialog;
    private PaletteLastColorsFragment mPaletteLastColorsFragment;
    private SaveImageDialog mSaveImageDialog;

    private MenuItem mBT_on;
    private MenuItem mBT_disabled;

    private ImageView mImageViewCurrentColor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //disable toolbar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //initialize the view to determine the current color
        mImageViewCurrentColor = findViewById(R.id.image_current_color);

        mBluetoothFragment = new BluetoothFragment();

        mPixelGirdFragment = new PixelGirdFragment();
        //registering the listener to send the grid array in case of changes
        mPixelGirdFragment.setFragmentListenerPixelGird(new PixelGirdFragment.FragmentListenerPixelGird() {
            @Override
            public void sendBluetooth(int[][] colorList) {
                mBluetoothFragment.sendMessage(colorList);
            }
        });

        mColorPickerDialog = new ColorPickerDialog();
        //listener registration for color selection
        mColorPickerDialog.setFragmentListenerColorPicker(new ColorPickerDialog.FragmentListenerColorPicker() {
            @Override
            public void fragmentColorSelected(int color) {
                //set painting color
                mPixelGirdFragment.setColor(color);
                //set color to PaletteLastColor
                mPaletteLastColorsFragment.updatePaletteLastColors(color);
                //display selected color in toolbar
                mImageViewCurrentColor.setColorFilter(color);
                Log.d(TAG, "selected color = " + String.valueOf(color));
            }
        });

        mPaletteLastColorsFragment = new PaletteLastColorsFragment();
        ////listener registration for palette colors
        mPaletteLastColorsFragment.setFragmentListenerPaletteLastColors(new PaletteLastColorsFragment.FragmentListenerPaletteLastColors() {
            @Override
            public void setColorPainting(int color) {
                mPixelGirdFragment.setColor(color);
                //display selected color in toolbar
                mImageViewCurrentColor.setColorFilter(color);
            }
        });

        mSaveImageDialog = new SaveImageDialog();
        //listener registration for generate image
        mSaveImageDialog.setGenerateImage(new SaveImageDialog.GenerateImage() {
            @Override
            public void setViewForGenerateImage(ImageView imageView) {
                //set image from pixelGird in saveImageDialog
                imageView.setImageBitmap(ImageUtils.createBitmapFromView(mPixelGirdFragment.getPixelGird()));
            }
        });
        //listener registration for save image in data bases
        mSaveImageDialog.setConversionImage(new SaveImageDialog.ConversionImage() {
            @Override
            public void saveImage(String nameImage) {
                //parse array image to string
                String image = ImageUtils.imageArraryToString(mPixelGirdFragment.getArrayPixelGird());

                GalleryDBHelper galleryDBHelper = new GalleryDBHelper(getApplicationContext());
                SQLiteDatabase db = galleryDBHelper.getWritableDatabase();
                galleryDBHelper.insert(db, nameImage, image);

                db.close();

                Log.d(TAG, "saved image with name " + nameImage);
            }
        });

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.bluetooth_container, mBluetoothFragment);
        fm.replace(R.id.mode_container, mPixelGirdFragment);
        fm.replace(R.id.last_color_container, mPaletteLastColorsFragment);
        fm .commit();

        //initializing main tablayout to switch modes
        TabLayout tabLayoutMain = findViewById(R.id.tabs_main);
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Paint"));
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Animation"));
        tabLayoutMain.addOnTabSelectedListener(tabSelectedListenerMain);

        //initializing bottom tablayout to switch modes
        TabLayout tabLayoutBotom = findViewById(R.id.tabs_bottom);
        tabLayoutBotom.addTab(tabLayoutBotom.newTab().setIcon(R.drawable.baseline_color_lens_black_18dp));
        tabLayoutBotom.addTab(tabLayoutBotom.newTab().setIcon(R.drawable.eraser));
        tabLayoutBotom.addTab(tabLayoutBotom.newTab().setIcon(R.drawable.baseline_format_color_reset_black_18dp));
        tabLayoutBotom.addOnTabSelectedListener(tabSelectedListenerBottom);

        //register the receiver to determine the status of the bluetooth adapter
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (mReceiver != null){
            registerReceiver(mReceiver, filter);
            Log.d(TAG, "register receiver");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null){
            unregisterReceiver(mReceiver);
            Log.d(TAG, "unregister receiver");
        }
        //Stop all threads
        mBluetoothFragment.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mBT_on = menu.findItem(R.id.bluetooth_on);
        mBT_disabled = menu.findItem(R.id.bluetooth_disabled);

        //determine the status of the bluetooth when initializing the menu
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            mBT_on.setVisible(true);
            mBT_disabled.setVisible(false);
        }else {
            mBT_on.setVisible(false);
            mBT_disabled.setVisible(true);
        }

        menu.findItem(R.id.bluetooth_connected).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.gallery_image:
                Intent intent = new Intent(MainActivity.this, GalleryImageActivity.class);
                startActivity(intent);
                return true;

            case R.id.save_image_menu:
                mSaveImageDialog.show(getSupportFragmentManager(), SaveImageDialog.DIALOG_SAVE_IMAGE);
                return true;
        }
        return false;
    }

    // BroadcastReceiver that listens for bluetooth adapter status
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    if (state == BluetoothAdapter.STATE_ON){
                        mBT_on.setVisible(true);
                        mBT_disabled.setVisible(false);
                        Log.d(TAG, "onReceive() bluetooth on");
                    }else if (state == BluetoothAdapter.STATE_OFF){
                        mBT_on.setVisible(false);
                        mBT_disabled.setVisible(true);
                        Log.d(TAG, "onReceive() bluetooth disabled");
                    }
                    break;
            }
        }
    };

    private TabLayout.BaseOnTabSelectedListener tabSelectedListenerMain = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mode_container, mPixelGirdFragment).commit();
                    Log.d(TAG, "replace mPixelGirdFragment");
                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mode_container, new Fragment()).commit();
                    Log.d(TAG, "mode container item 2");
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private TabLayout.BaseOnTabSelectedListener tabSelectedListenerBottom = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    mColorPickerDialog.show(getSupportFragmentManager(), ColorPickerDialog.DIALOG_COLOR_PICKER);
                    break;
                case 1:
                    mPixelGirdFragment.setColor(Color.BLACK);
                    //display selected color in toolbar
                    mImageViewCurrentColor.setColorFilter(Color.BLACK);
                    break;
                case 2:
                    mPixelGirdFragment.resetPaint();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    mColorPickerDialog.show(getSupportFragmentManager(), ColorPickerDialog.DIALOG_COLOR_PICKER);
                    break;
                case 1:
                    mPixelGirdFragment.setColor(Color.BLACK);
                    //display selected color in toolbar
                    mImageViewCurrentColor.setColorFilter(Color.BLACK);
                    break;
                case 2:
                    mPixelGirdFragment.resetPaint();
                    break;
            }
        }
    };
}





