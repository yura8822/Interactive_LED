package com.yura8822.drawing_field;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yura8822.R;

import java.util.Arrays;

public class PaletteLastColors extends View {
    private static final String TAG = "PaletteLastColors";

    public interface ListenerPaletteLastColors{
        void selectColor(int color);
    }

    private ListenerPaletteLastColors mListenerPaletteLastColors;

    private int quantityColumnsPL;
    private int quantityRowsPL;

    private int width;
    private int height;

    private int cellSpacingPL;

    private int cellSize;

    private Paint paintRect;
    private Paint paintRectBorder;

    private int color;

    private int[][]colorList;

    public PaletteLastColors(Context context) {
        super(context);
    }

    public PaletteLastColors(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PaletteLastColors,
                0, 0);
        try {
            quantityColumnsPL = typedArray.getInteger(R.styleable.PaletteLastColors_quantityColumnsPL, 8);
            quantityRowsPL = typedArray.getInteger(R.styleable.PaletteLastColors_quantityRowsPL, 2);
            cellSpacingPL = typedArray.getInteger(R.styleable.PaletteLastColors_cellSpacingPL,1);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        // cell size calculation
        if (quantityColumnsPL > quantityRowsPL){
            cellSize = sizeW / quantityColumnsPL;
            if (quantityRowsPL * cellSize > sizeH) cellSize = sizeH / quantityColumnsPL;
            Log.d(TAG, "quantityColumnsPL > quantityRowsPL");
        }else if (quantityColumnsPL < quantityRowsPL){
            cellSize = sizeH / quantityRowsPL;
            if (quantityColumnsPL * cellSize > sizeW) cellSize = sizeW / quantityColumnsPL;
            Log.d(TAG, "quantityColumnsPL < quantityRowsPL");
        }else {
            cellSize = Math.min(sizeW, sizeH) / quantityColumnsPL;
            Log.d(TAG, "quantityColumnsPL == quantityRowsPL");
        }

        //calculation of the sides of the screen
        width = quantityColumnsPL * cellSize;
        height = quantityRowsPL * cellSize;

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawField(canvas);
    }

    private void init(){
        colorList = new int[quantityRowsPL][quantityColumnsPL];

        //init paint for cell
        paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRect.setStyle(Paint.Style.FILL_AND_STROKE);

        //init paint for cell boarder
        paintRectBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRectBorder.setStyle(Paint.Style.STROKE);
        paintRectBorder.setColor(Color.BLACK);
        paintRectBorder.setStrokeWidth(cellSize/20);

        //initialize the array with colors
        initArray();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentTouchI = (int)event.getY() / cellSize;
        int currentTouchJ = (int)event.getX() / cellSize;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (checkCoordinates(currentTouchI, currentTouchJ)){
                    //define the selected cell, and set the value of the argument of the interface method
                    int color = colorList[currentTouchI][currentTouchJ];
                    mListenerPaletteLastColors.selectColor(color);
                }

                return true;
            }

            case MotionEvent.ACTION_MOVE: {
            }

            case MotionEvent.ACTION_UP:{
                break;
            }
        }

        return true;
    }

    private boolean checkCoordinates(int i, int j){
        if (i >= 0 && j >= 0
                && i < quantityRowsPL && j < quantityColumnsPL){
            return true;
        }
        return false;
    }

    private void drawField(Canvas canvas){
        // creating and drawing squares
        int top = 0 - cellSize;
        int left;
        int right;
        int bottom = 0;

        for (int i = 0; i < quantityRowsPL; i++){
            top += cellSize + cellSpacingPL;
            left = 0 - cellSize;
            right = 0;
            bottom += cellSize - cellSpacingPL;

            for(int j = 0; j < quantityColumnsPL; j++){
                left += cellSize + cellSpacingPL;
                right += cellSize - cellSpacingPL;

                //color selection
                paintRect.setColor(colorList[i][j]);
                canvas.drawRoundRect(new RectF(left, top, right, bottom), cellSize/5,
                        cellSize/5, paintRect);

                //draw border cell
                canvas.drawRoundRect(new RectF(left, top, right, bottom), cellSize/5,
                        cellSize/5, paintRectBorder);

                left -= cellSpacingPL;
                right += cellSpacingPL;
            }

            top -= cellSpacingPL;
            bottom += cellSpacingPL;
        }
    }

    private void initArray(){
        float colorIterator = 360f / (quantityColumnsPL * quantityRowsPL);
        float[] hsv = new float[] {0, 1f, 1f};

        for (int i = 0; i < colorList.length; i++){
            for (int j = 0; j < colorList[i].length; j++){
                hsv[0] += colorIterator;
                colorList[i][j] = Color.HSVToColor(hsv);
            }
        }
    }

    //add recently selected color to the list
    public void addColor(int color){
        this.color = color;

        for (int i = colorList.length-1; i >= 0 ; i--){
            for (int j = colorList[i].length-1; j >= 0; j--){
                if (j != 0) colorList[i][j] = colorList[i][j - 1];
                else if (i != 0) colorList[i][j] = colorList[i - 1][colorList[i].length-1];
            }
        }
        this.colorList[0][0] = color;
        invalidate();
    }

    public void setListenerPaletteLastColors(ListenerPaletteLastColors listenerPaletteLastColors) {
        mListenerPaletteLastColors = listenerPaletteLastColors;
    }
}
