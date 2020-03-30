package com.yura8822.utils;

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

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static byte[] intArrayToByteArray(int[][] array){
      byte[] bytes = new byte[array.length * array[0].length * 3];
      int index = -1;
        for (int[] ints : array) {
            for (int anInt : ints) {
                bytes[++index] = (byte) Color.red(anInt);
                bytes[++index] = (byte) Color.green(anInt);
                bytes[++index] = (byte) Color.blue(anInt);
            }
        }
      return bytes;
    }

    public static int[][] byteArrayToIntArray(int quantityRows, int quantityColumns, byte[] bytes){
        int[][] colorList = new int[quantityRows][quantityColumns];
        int index = -1;
        for (int i = 0; i < colorList.length; i++){
            for (int j = 0; j < colorList[i].length; j++){
                colorList[i][j] = Color.rgb(
                        bytes[++index] & 255,
                        bytes[++index] & 255,
                        bytes[++index] & 255);
            }
        }
        return colorList;
    }

    public static Drawable byteArrayToDrawable(Resources resources, byte[] bytes) {
        return new DrawablePixelGird(resources, bytes);
    }

    private static class DrawablePixelGird extends Drawable{
        private int quantityColumns;
        private int quantityRows;
        private int cellSpacing;

        private int cellSize;
        private int[][] colorList;

        private Rect mRectCell;
        private int color;
        private Paint paintRect;

        private DrawablePixelGird(Resources resources, byte[] bytes){
            this.quantityColumns = resources.getInteger(R.integer.quantity_columns);
            this.quantityRows = resources.getInteger(R.integer.quantity_rows);
            this.cellSpacing = resources.getInteger(R.integer.cellSpacing)/2;

            // cell size calculation
            int sizeW = (int) resources.getDimension(R.dimen.image_card_width);
            int sizeH = (int) resources.getDimension(R.dimen.image_card_height);
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
            colorList = byteArrayToIntArray(quantityRows, quantityColumns, bytes);

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
    }
}



