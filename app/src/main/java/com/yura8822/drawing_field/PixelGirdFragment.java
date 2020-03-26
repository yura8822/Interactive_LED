package com.yura8822.drawing_field;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yura8822.R;


public class PixelGirdFragment extends Fragment {
    private static final String TAG = "PixelGirdFragment";

    public interface FragmentListenerPixelGird{
        void sendBluetooth(int[][] colorList);
    }

    private FragmentListenerPixelGird fragmentListenerPixelGird;

    private PixelGird mPixelGird;

    public PixelGirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pixel_gird, container, false);
        mPixelGird = view.findViewById(R.id.pixel_gird_frame);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPixelGird.setListenerPixelGird(new PixelGird.ListenerPixelGird() {
            @Override
            public void sendArrayGird(int[][] colorList) {
                if (fragmentListenerPixelGird != null){
                    fragmentListenerPixelGird.sendBluetooth(colorList);
                }
            }
        });
    }

    public void setFragmentListenerPixelGird(FragmentListenerPixelGird fragmentListenerPixelGird) {
        this.fragmentListenerPixelGird = fragmentListenerPixelGird;
        Log.d(TAG, "setFragmentListenerPixelGird()");
    }

    public void setColor(int color){
        mPixelGird.setColor(color);
    }

    //clear canvas
    public void resetPaint(){
        mPixelGird.resetColorList();
    }

    public PixelGird getPixelGird() {
        return mPixelGird;
    }

    public int[][] getArrayPixelGird(){
        return mPixelGird.getColorList();
    }

    public void loadImage(int[][] colorList){
        mPixelGird.setColorList(colorList);
    }
}
