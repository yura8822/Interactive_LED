package com.yura8822.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yura8822.database.DBContract.ImageGalleryTable;

import java.util.ArrayList;
import java.util.List;

import static com.yura8822.database.DBContract.DeviceTable;

public class DataBaseLab {
    private static DataBaseLab sDataBaseLab;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private static final String DEVICE_KEY = "com.yura8822.device_key";

    private DataBaseLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new DBHelper(mContext).getWritableDatabase();
    }

    public static DataBaseLab get(Context context){
        if (sDataBaseLab == null){
            sDataBaseLab = new DataBaseLab(context);
        }
        return sDataBaseLab;
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
        ContentValues contentValues = getContentValuesImage(image);
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

    private ContentValues getContentValuesImage(Image image){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ImageGalleryTable.Colls.NAME, image.getName());
        contentValues.put(ImageGalleryTable.Colls.DATE, String.valueOf(image.getDate()));
        contentValues.put(ImageGalleryTable.Colls.IMAGE, image.getImage());
        return contentValues;
    }

    public void insertDevice(String mac) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeviceTable.Colls.KEY, DEVICE_KEY);
        contentValues.put(DeviceTable.Colls.MAC, mac);
        mDatabase.insert(DeviceTable.TABLE_NAME, null, contentValues);
    }

    public void deleteDevice(){
        mDatabase.delete(DeviceTable.TABLE_NAME,
                DeviceTable.Colls.KEY + " = ?",
                new String[]{DEVICE_KEY});
    }

    public List<String> getDevice(){
        List<String> list = new ArrayList<>();
        String[] projection = {DeviceTable.Colls.MAC};
        try (Cursor cursor = mDatabase.query(
                DeviceTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(cursor.getColumnIndex(DeviceTable.Colls.MAC)));
                cursor.moveToNext();
            }
        }
        return list;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}
