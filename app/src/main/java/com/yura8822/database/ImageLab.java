package com.yura8822.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yura8822.database.DBContract.ImageGalleryTable;

import java.util.ArrayList;
import java.util.List;

public class ImageLab {
    private static ImageLab sImageLab;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private ImageLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new DBHelper(mContext).getWritableDatabase();
    }

    public static ImageLab get(Context context){
        if (sImageLab == null){
            sImageLab = new ImageLab(context);
        }
        return sImageLab;
    }

    public List<Image> getImages(){
        List<Image> images = new ArrayList<>();
        try (ImageCursorWrapper cursorWrapper = queryCursorImages(null, null)) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                images.add(cursorWrapper.getImage());
                cursorWrapper.moveToNext();
            }
        }
        return images;
    }

    public Image getImageById(long id){
        try (ImageCursorWrapper cursorWrapper = queryCursorImages(ImageGalleryTable.Colls.ID + " = ?",
                new String[]{String.valueOf(id)})) {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToNext();
            return cursorWrapper.getImage();
        }
    }

    public void insertImage(Image image){
        ContentValues contentValues = getContentValues(image);
        mDatabase.insert(ImageGalleryTable.TABLE_NAME, null, contentValues);
    }

    public void deleteImage(long id){
        mDatabase.delete(ImageGalleryTable.TABLE_NAME,
                ImageGalleryTable.Colls.ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteImages(List<Image> images){
        for (Image image : images){
            if (image.isChecked()){
                mDatabase.delete(ImageGalleryTable.TABLE_NAME,
                        ImageGalleryTable.Colls.ID + " = ?",
                        new String[]{String.valueOf(image.getId())});
            }
        }
    }

    private ImageCursorWrapper queryCursorImages(String where, String[] whereArgs){
        String[] projection = {ImageGalleryTable.Colls.ID,
                ImageGalleryTable.Colls.NAME,
                ImageGalleryTable.Colls.IMAGE,
                ImageGalleryTable.Colls.DATE
        };
        Cursor cursor = mDatabase.query(
                ImageGalleryTable.TABLE_NAME,
                projection,
                where,
                whereArgs,
                null,
                null,
                ImageGalleryTable.Colls.DATE + " DESC"
        );
        return new ImageCursorWrapper(cursor);
    }

    private ContentValues getContentValues(Image image){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ImageGalleryTable.Colls.NAME, image.getName());
        contentValues.put(ImageGalleryTable.Colls.DATE, String.valueOf(image.getDate()));
        contentValues.put(ImageGalleryTable.Colls.IMAGE, image.getImage());
        return contentValues;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public void closeDBHelper(){
        if (mDatabase == null){
            mDatabase.close();
        }
    }
}
