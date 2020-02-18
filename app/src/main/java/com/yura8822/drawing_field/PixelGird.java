package com.yura8822.drawing_field;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yura8822.R;

import java.util.Arrays;

public class PixelGird extends View {

    private final static String TAG = "PixelGird";

    public interface ListenerPixelGird{
        void sendArrayGird(int[][] colorList);
    }

    private ListenerPixelGird listenerPixelGird;

    private int quantityColumns;
    private int quantityRows;

    private int width;
    private int height;

    private int cellSpacing;

    private int[][]  colorList;
    private int[][] previousColorList;

    private int cellSize;

    private Paint paintRect;

    private Rect mRectCell;

    private int color;

    public PixelGird(Context context) {
        super(context);
    }

    public PixelGird(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PixelGird,
                0, 0);
        try {
            quantityColumns = typedArray.getInteger(R.styleable.PixelGird_quantityColumns, 0);
            quantityRows = typedArray.getInteger(R.styleable.PixelGird_quantityRows, 0);
            cellSpacing = typedArray.getInteger(R.styleable.PixelGird_cellSpacing,1);
        } finally {
            typedArray.recycle();
        }

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        // cell size calculation
        if (quantityColumns > quantityRows){
            cellSize = sizeW / quantityColumns;
            if (quantityRows * cellSize > sizeH) cellSize = sizeH / quantityColumns;
            Log.d(TAG, "quantityColumnsPL > quantityRowsPL");
        }else if (quantityColumns < quantityRows){
            cellSize = sizeH / quantityRows;
            if (quantityColumns * cellSize > sizeW) cellSize = sizeW / quantityRows;
            Log.d(TAG, "quantityColumnsPL < quantityRowsPL");
        }else {
            cellSize = Math.min(sizeW, sizeH) / quantityColumns;
            Log.d(TAG, "quantityColumnsPL == quantityRowsPL");
        }

        //calculation of the sides of the screen
        width = quantityColumns * cellSize;
        height = quantityRows * cellSize;

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawField(canvas);
    }

    private void init(){
        // init array color list
        colorList = new int[quantityRows][quantityColumns];
        initArrayColor(colorList);
        previousColorList = new int[quantityRows][quantityColumns];
        initArrayColor(previousColorList);

        //init paint
        color = Color.BLACK;
        paintRect = new Paint();
        paintRect.setColor(color);
        paintRect.setStyle(Paint.Style.FILL);

        mRectCell = new Rect();
        Log.d(TAG, "init()");
    }

    private int lastTouchI;
    private int lastTouchJ;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentTouchI = (int)event.getY() / cellSize;
        int currentTouchJ = (int)event.getX() / cellSize;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (checkCoordinates(currentTouchI, currentTouchJ)){
                    this.colorList[currentTouchI][currentTouchJ] = color;
                }

                lastTouchI = currentTouchI;
                lastTouchJ = currentTouchJ;

                return true;
            }

            case MotionEvent.ACTION_MOVE: {
            }

            case MotionEvent.ACTION_UP:{

                for (int i = 0; i < event.getHistorySize(); i++){
                    int historicalI = (int)event.getHistoricalY(i) / cellSize;
                    int historicalJ = (int)event.getHistoricalX(i) / cellSize;


                    rectTo(historicalI, historicalJ, lastTouchI, lastTouchJ);

                    lastTouchI = historicalI;
                    lastTouchJ = historicalJ;
                }

                rectTo(currentTouchI, currentTouchJ, lastTouchI, lastTouchJ);

                break;
            }
        }
        lastTouchI = currentTouchI;
        lastTouchJ = currentTouchJ;

        //if the elements of the array have changed, then draw and send
        if (!Arrays.deepEquals(colorList, previousColorList)){
            for (int i = 0; i < colorList.length; i++){
                previousColorList[i] = colorList[i].clone();
            }
            if (listenerPixelGird != null) {
                listenerPixelGird.sendArrayGird(colorList);
            }
            invalidate();
        }

        return true;
    }

    private boolean checkCoordinates(int i, int j){
        if (i >= 0 && j >= 0
                && i < quantityRows && j < quantityColumns){
            return true;
        }
        return false;
    }

    //fills the field with rectangles from point to point
    private void rectTo(int currentI, int currentJ, int oldI, int oldJ){
        while (oldI != currentI || oldJ != currentJ){
            if (oldI > currentI) oldI--;
            else if (oldI < currentI) oldI++;

            if (oldJ > currentJ) oldJ--;
            else if (oldJ < currentJ) oldJ++;

            if (checkCoordinates(oldI, oldJ)){
                this.colorList[oldI][oldJ] = color;
            }
        }
    }

    private void drawField(Canvas canvas){
        // creating and drawing squares
        int top = 0 - cellSize;
        int left;
        int right;
        int bottom = 0;

        for (int i = 0; i < quantityRows; i++){
            top += cellSize + cellSpacing;
            left = 0 - cellSize;
            right = 0;
            bottom += cellSize - cellSpacing;

            for(int j = 0; j < quantityColumns; j++){
                left += cellSize + cellSpacing;
                right += cellSize - cellSpacing;

                //color selection
                paintRect.setColor(colorList[i][j]);
                mRectCell.left = left;
                mRectCell.top = top;
                mRectCell.right = right;
                mRectCell.bottom = bottom;
                canvas.drawRect(mRectCell, paintRect);

                left -= cellSpacing;
                right += cellSpacing;
            }

            top -= cellSpacing;
            bottom += cellSpacing;
        }
    }

    private void initArrayColor(int[][] arrayColor){
        for (int i = 0; i < arrayColor.length; i++){
            for (int j = 0; j < arrayColor[i].length; j++){
                arrayColor[i][j] = Color.BLACK;
            }
        }
    }

    public void setListenerPixelGird(ListenerPixelGird listenerPixelGird) {
        this.listenerPixelGird = listenerPixelGird;
        Log.d(TAG, "setListenerPixelGird()");
    }

    public void setColor(int color) {
        this.color = color;
    }

    //fill the array with black
    public void resetColorList(){
        initArrayColor(colorList);
        initArrayColor(previousColorList);
        if (listenerPixelGird != null) {
            listenerPixelGird.sendArrayGird(colorList);
        }
        invalidate();
    }


    public void setColorList(int[][] colorList) {
        for (int i = 0; i < this.colorList.length; i++){
            for (int j = 0; j < this.colorList[i].length; j++){
                this.colorList[i][j] = colorList[i][j];
                this.previousColorList[i][j] = colorList[i][j];
            }
        }

        if (listenerPixelGird != null) {
            listenerPixelGird.sendArrayGird(this.colorList);
        }
        invalidate();
    }

    public int[][] getColorList() {
        return colorList;
    }
}
