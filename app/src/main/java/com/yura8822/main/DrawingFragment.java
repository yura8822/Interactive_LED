package com.yura8822.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yura8822.R;

public class DrawingFragment extends Fragment {
    private static final String TAG = "DrawingFragment";
    private final static int REQUEST_COLOR = 0;

    private Toolbar mBottomToolbar;
    private ColorPickerDialog mColorPicker;

    public DrawingFragment() {

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


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_COLOR){
            int color = ColorPickerDialog.getColor(data);
            updateSelectedColor(color);
        }
    }

    private void updateSelectedColor(int color){
        mBottomToolbar.getLogo().setColorFilter(color, PorterDuff.Mode.LIGHTEN);
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
                    Toast.makeText(getActivity(), "eraser", Toast.LENGTH_SHORT).show();
                    return true;
                }
                case R.id.erase_drawing:{
                    Toast.makeText(getActivity(), "erase drawing", Toast.LENGTH_SHORT).show();
                    return true;
                }
                default:{
                    return false;
                }
            }
        }
    };
}
