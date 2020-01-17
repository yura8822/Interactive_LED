package com.yura8822.drawing_field;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yura8822.R;

public class PaletteLastColorsFragment extends Fragment {
    private static final String TAG = "PaletteColorsFragment";

    public interface FragmentListenerPaletteLastColors{
        void setColorPainting(int color);
    }

    private FragmentListenerPaletteLastColors mFragmentListenerPaletteLastColors;

    private PaletteLastColors mPaletteLastColors;

    public PaletteLastColorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_palette_last_colors, container, false);
        mPaletteLastColors = view.findViewById(R.id.palette_last_colors);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPaletteLastColors.setListenerPaletteLastColors(new PaletteLastColors.ListenerPaletteLastColors() {
            @Override
            public void selectColor(int color) {
                mFragmentListenerPaletteLastColors.setColorPainting(color);
            }
        });
    }

    public void updatePaletteLastColors(int color){
        mPaletteLastColors.addColor(color);
        Log.d(TAG, "updatePaletteLastColors");
    }

    public void setFragmentListenerPaletteLastColors(FragmentListenerPaletteLastColors fragmentListenerPaletteLastColors) {
        mFragmentListenerPaletteLastColors = fragmentListenerPaletteLastColors;
        Log.d(TAG, "mFragmentListenerPaletteLastColors");
    }
}
