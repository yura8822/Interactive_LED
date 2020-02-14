package com.yura8822.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yura8822.R;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static Bitmap createBitmapFromView(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }

    public static String imageArraryToString(int[][] array){
        StringBuilder sb = new StringBuilder();
        for (int[] out : array){
            for (int value : out){
                sb.append(value).append(" ");
            }
        }
        return sb.toString();
    }

    public static Drawable StringToDrawble(Context context, String imageString) {
        return new DrawblePixelGird(context, imageString);
    }

    private static class DrawblePixelGird extends Drawable{
        private int quantityColumns;
        private int quantityRows;
        private int cellSpacing;

        private int cellSize;
        private int[][] colorList;

        private Rect mRectCell;
        private int color;
        private Paint paintRect;

        private DrawblePixelGird(Context context, String imageString){
            Resources resources = context.getResources();
            quantityColumns = resources.getInteger(R.integer.quantity_columns);
            quantityRows = resources.getInteger(R.integer.quantity_rows);
            cellSpacing = resources.getInteger(R.integer.cellSpacing)/2;

            // cell size calculation
            int sizeW = (int) resources.getDimension(R.dimen.image_card);
            int sizeH = (int) resources.getDimension(R.dimen.image_card);
            Log.d(TAG, "sizeH = " + sizeH + " sizeW = " + sizeW);
            if (quantityColumns > quantityRows){
                cellSize = sizeW / quantityColumns;
                if (quantityRows * cellSize > sizeH) cellSize = sizeH / quantityColumns;
            }else if (quantityColumns < quantityRows){
                cellSize = sizeH / quantityRows;
                if (quantityColumns * cellSize > sizeW) cellSize = sizeW / quantityRows;
            }else {
                cellSize = Math.min(sizeW, sizeH) / quantityColumns;
            }

            //init color list array
            colorList = stringArrayToIntArray(quantityRows, quantityColumns, imageString);

            //init paint
            color = Color.BLACK;
            paintRect = new Paint();
            paintRect.setColor(color);
            paintRect.setStyle(Paint.Style.FILL);

            mRectCell = new Rect();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
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

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }

        private int[][] stringArrayToIntArray(int quantityRows, int quantityColumns, String imageString){
            int[][] colorList = new int[quantityRows][quantityColumns];
            String[] colorListString = imageString.split(" ");

            int count = 0;
            for (int i = 0; i < colorList.length; i++){
                for (int j = 0; j < colorList[i].length; j++){
                    colorList[i][j] = Integer.parseInt(colorListString[count++]);
                }
            }
            return colorList;
        }
    }
}



