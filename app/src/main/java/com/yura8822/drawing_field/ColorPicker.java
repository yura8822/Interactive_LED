package com.yura8822.drawing_field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorPicker extends View {

    private final String TAG = "ColorPicker";

    private final float STROKE_WIDTH_COEFFICIENT = 0.06f;

    private int centerCircleX;
    private int centerCircleY;
    private float radiusOuterCircle;
    private int size;

    private Paint paintOuterCircle;

    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.RED};

    public ColorPicker(Context context) {
        super(context);
        init();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.size = Math.min(width, height);
        setMeasuredDimension(size, size);

        calculateCenterCircle();
        initPaintOuterCircle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(color);
        canvas.drawCircle(centerCircleX, centerCircleY,radiusOuterCircle,paintOuterCircle);
    }

    private void calculateCenterCircle(){
        this.centerCircleX = size/2;
        this.centerCircleY = centerCircleX;
        this.radiusOuterCircle = size*(0.5f - STROKE_WIDTH_COEFFICIENT);

    }

    private void initPaintOuterCircle(){
        paintOuterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuterCircle.setStyle(Paint.Style.STROKE);
        paintOuterCircle.setStrokeWidth(size * STROKE_WIDTH_COEFFICIENT * 2);

        Shader shader = new SweepGradient(centerCircleX, centerCircleY, colors, null);
        paintOuterCircle.setShader(shader);
    }


    private int[] argb = new int[] {255, 0, 0, 0};
    private float[] hsv = new float[] {0, 1f, 1f};
    private int color;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                float x =  Math.abs(event.getX() - centerCircleX);
                float y =  Math.abs(event.getY() - centerCircleY);
                float angle = getAngle(x,y);

                Log.d(TAG,"X = "+
                        String.valueOf(event.getX() - centerCircleX) +
                        " Y = " +
                        String.valueOf(event.getY() - centerCircleY));

                Log.d(TAG, "angle = " + String.valueOf(angle));


                return true;

            case MotionEvent.ACTION_MOVE:
                 x =  Math.abs(event.getX() - centerCircleX);
                 y =  Math.abs(event.getY() - centerCircleY);
                 angle = getAngle(event.getX() - centerCircleX,event.getY() - centerCircleY);

                 hsv[0] = angle;
                 color = Color.HSVToColor(hsv);

                Log.d(TAG,"X = "+
                        String.valueOf(event.getX() - centerCircleX) +
                        " Y = " +
                        String.valueOf(event.getY() - centerCircleY));

                Log.d(TAG, "angle = " + String.valueOf(angle));

                invalidate();


            case MotionEvent.ACTION_UP:

        }
        return true;
    }

    protected float getAngle(float x, float y) {
        float deg = 0;
        if (x != 0) deg = y / x;
        deg = (float) Math.toDegrees(Math.atan(deg));
        if (x < 0) deg += 180;
        else if (x > 0 && y < 0) deg += 360;
        return deg;
    }


}
