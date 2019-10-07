package com.yura8822;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PixelGird extends View {
    private int quantityColumns;
    private int quantityRows;

    int width;
    int height;

    private Rect[][] rectsList;
    private int[][]  colorList;

    private int cellSize;

    private Paint paintRect;

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
        } finally {
            typedArray.recycle();
        }

        rectsList = new Rect[quantityRows][quantityColumns];
        colorList = new int[quantityRows][quantityColumns];

        paintRect = new Paint();
        paintRect.setColor(Color.BLACK);
        paintRect.setStyle(Paint.Style.STROKE);
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

        for (int i = 0; i < rectsList.length; i++){
            top += cellSize;
            left = 0 - cellSize;
            right = 0;
            bottom += cellSize;

            for(int j = 0; j < rectsList[i].length; j++){
                left += cellSize;
                right += cellSize;

                rectsList[i][j] = new Rect(left, top, right, bottom);
                canvas.drawRect(rectsList[i][j], paintRect);
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
