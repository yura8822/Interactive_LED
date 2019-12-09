package com.yura8822.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    private boolean enabled;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        this.enabled = true;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (enabled){
            return super.onInterceptTouchEvent(ev);
        }else
            return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enabled){
            return super.onTouchEvent(ev);
        }else
            return false;
    }

    public void setSwipeEnabling(boolean enabled){
        this.enabled = enabled;
    }
}
