package com.yura8822;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class PixelGird extends View {

    public interface ListenerPixelGird{
        void sendArrayGird(int[][] colorList);
    }

    private Context activity;

    private ListenerPixelGird listenerPixelGird;

    private int quantityColumns;
    private int quantityRows;

    private int width;
    private int height;

    private int[][]  colorList;

    private int cellSize;

    private Paint paintRect;



    public PixelGird(Context context) {
        super(context);
        this.activity = context;
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
        } finally {
            typedArray.recycle();
        }

        colorList = new int[quantityRows][quantityColumns];

        paintRect = new Paint();
        paintRect.setColor(Color.BLACK);
        paintRect.setStyle(Paint.Style.STROKE);

        this.activity = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateDimensions();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawField(canvas);

        if (listenerPixelGird == null){ //temp!!!
            listenerPixelGird = (ListenerPixelGird) activity;
        }

        if (listenerPixelGird != null){
            listenerPixelGird.sendArrayGird(colorList);
        }
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
                    this.colorList[currentTouchI][currentTouchJ] = 999;
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

        invalidate();

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
                this.colorList[oldI][oldJ] = 999;
            }
        }
    }

    private void calculateDimensions(){
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();

        // cell size calculation
        int cellSizeWidth = width / quantityColumns;
        int cellSizeHeight = height / quantityRows;

        if (cellSizeWidth > cellSizeHeight){
            cellSize = cellSizeHeight;
        }else {
            cellSize = cellSizeWidth;
        }

        // calculation of the sides of the screen
        width = quantityColumns * cellSize;
        height = quantityRows * cellSize;

        this.setMeasuredDimension(width, height);
    }

    private void drawField(Canvas canvas){
        // creating and drawing squares
        int top = 0 - cellSize;
        int left;
        int right;
        int bottom = 0;

        for (int i = 0; i < quantityRows; i++){
            top += cellSize;
            left = 0 - cellSize;
            right = 0;
            bottom += cellSize;

            for(int j = 0; j < quantityColumns; j++){
                left += cellSize;
                right += cellSize;

                //color selection
                if (colorList[i][j] > 0){
                    paintRect.setStyle(Paint.Style.FILL);
                }else {
                    paintRect.setStyle(Paint.Style.STROKE);
                }

                canvas.drawRect(new Rect(left, top, right, bottom), paintRect);
            }
        }
    }

    public int getQuantityColumns() {
        return quantityColumns;
    }

    public void setQuantityColumns(int quantityColumns) {
        this.quantityColumns = quantityColumns;
    }

    public int getQuantityRows() {
        return quantityRows;
    }

    public void setQuantityRows(int quantityRows) {
        this.quantityRows = quantityRows;
    }
}
