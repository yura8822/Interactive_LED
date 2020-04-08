package com.yura8822.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.yura8822.R;
import com.yura8822.database.Image;
import com.yura8822.database.ImageLab;
import com.yura8822.gallery_image.GalleryFragment;
import com.yura8822.utils.ImageUtils;

import java.util.Date;

public class DrawingFragment extends Fragment {
    private static final String TAG = "DrawingFragment";
    private final static int REQUEST_COLOR = 0;
    public final static int REQUEST_SAVE_IMG = 1;
    public final static int REQUEST_LOAD_IMAGE = 2;

    public interface OnSendListener {
        void sendToBluetooth(int[][] colorList);
    }

    private OnSendListener mOnSendListener;

    private Toolbar mBottomToolbar;
    private ColorPickerDialog mColorPicker;
    private PixelGird mPixelGird;
    private PaletteLastColors mPaletteLastColors;

    public DrawingFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnSendListener = (OnSendListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnSendListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);
        // init bottom tools menu
        mBottomToolbar = view.findViewById(R.id.drawing_tools);
        mBottomToolbar.setLogo(R.drawable.square_current_color);
        ActionMenuView actionMenu = view.findViewById(R.id.drawing_tools_menu);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.fragment_drawing, actionMenu.getMenu());
        actionMenu.setOnMenuItemClickListener(mItemClickListener);

        mPixelGird = view.findViewById(R.id.pixel_gird);
        //registering the listener to send the grid array in case of changes
        mPixelGird.setListenerPixelGird(new PixelGird.ListenerPixelGird() {
            @Override
            public void sendArrayGird(int[][] colorList) {
                mOnSendListener.sendToBluetooth(colorList);
            }
        });
        mPaletteLastColors = view.findViewById(R.id.palette_last_colors);
        //listener registration for palette colors
        mPaletteLastColors.setListenerPaletteLastColors(new PaletteLastColors.ListenerPaletteLastColors() {
            @Override
            public void selectColor(int color) {
                updateSelectedColor(color);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null){
            return;
        }
        switch (requestCode){
            case REQUEST_COLOR:{
                int color = ColorPickerDialog.getColor(data);
                mPaletteLastColors.addColor(color);
                updateSelectedColor(color);
                break;
            }
            case REQUEST_SAVE_IMG:{
                String nameImg = SaveImageDialog.getNameImg(data);
                Image image = new Image();
                image.setName(nameImg);
                image.setDate(new Date().getTime());
                image.setImage(ImageUtils.intArrayToByteArray(mPixelGird.getColorList()));
                ImageLab.get(getContext().getApplicationContext()).insertImage(image);
                break;
            }
            case REQUEST_LOAD_IMAGE:{
                long id = GalleryFragment.getImageID(data);
                Image image = ImageLab.get(getContext().getApplicationContext()).getImageById(id);
                int[][] colorList = ImageUtils.byteArrayToIntArray(getResources(), image.getImage());
                mPixelGird.setColorList(colorList);
            break;
            }
        }
    }

    private void updateSelectedColor(int color){
        mBottomToolbar.getLogo().setColorFilter(color, PorterDuff.Mode.LIGHTEN);
        mPixelGird.setColor(color);
    }

    ActionMenuView.OnMenuItemClickListener mItemClickListener = new ActionMenuView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.select_color:{
                    mColorPicker = new ColorPickerDialog();
                    mColorPicker.setTargetFragment(DrawingFragment.this, REQUEST_COLOR);
                    mColorPicker.show(getActivity().getSupportFragmentManager(), ColorPickerDialog.DIALOG_COLOR_PICKER);
                    return true;
                }
                case R.id.eraser:{
                    updateSelectedColor(Color.BLACK);
                    return true;
                }
                case R.id.erase_drawing:{
                    mPixelGird.resetColorList();
                    return true;
                }
                default:{
                    return false;
                }
            }
        }
    };
}
