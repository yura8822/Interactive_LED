package com.yura8822.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "GalleryDBHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gallery.db";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.ImageGalleryTable.SQL_CREATE_IMAGE_GALLERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String name, String image){
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.ImageGalleryTable.Colls.NAME, name);
            contentValues.put(DBContract.ImageGalleryTable.Colls.IMAGE, image);

            db.insert(DBContract.ImageGalleryTable.TABLE_NAME, null, contentValues);
            db.close();
        }catch (SQLiteException e){
            Log.e(TAG, "insert(String, String)");
        }
    }

    public void deleteByListId(List<Image> imageList){
        SQLiteDatabase db = this.getReadableDatabase();
        //Define 'where' part of query
        String selection = DBContract.ImageGalleryTable.Colls.ID + " LIKE ?";
        try {
            //find marked for deletion and delete
            for (Image value : imageList){
                if (value.isChecked()){
                    // Specify arguments in placeholder order.
                    String[] selectionArgs = {String.valueOf(value.getId())};
                    int detectedRow = db.delete(DBContract.ImageGalleryTable.TABLE_NAME,
                            selection, selectionArgs);
                    Log.d(TAG, "delete row = " + value.getName() + "  detectedRow = " + detectedRow);
                }
            }
            db.close();
        }catch (SQLiteException e){
            Log.e(TAG, "deleteByListId(List<Image>)");
        }
    }

    public Image findById(long id){
        Image image = new Image();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {DBContract.ImageGalleryTable.Colls.ID,
                DBContract.ImageGalleryTable.Colls.NAME,
                DBContract.ImageGalleryTable.Colls.IMAGE

        };
        // Filter results WHERE "id" = //long value
        String selection = DBContract.ImageGalleryTable.Colls.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        try{
            Cursor cursor = db.query(DBContract.ImageGalleryTable.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();


            image.setId(cursor.getLong(cursor.getColumnIndex(DBContract.ImageGalleryTable.Colls.ID)));
            image.setName(cursor.getString(cursor.getColumnIndex(DBContract.ImageGalleryTable.Colls.NAME)));
//            image.setImage(cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.IMAGE)));

            db.close();
            cursor.close();

        }catch (SQLiteException e){
            Log.e(TAG, "findById(long)");
        }

        return image;
    }

    public List<Image> findAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Image> imageArrayList = new ArrayList<>();

        String[] projection = {DBContract.ImageGalleryTable.Colls.ID,
                DBContract.ImageGalleryTable.Colls.NAME,
                DBContract.ImageGalleryTable.Colls.IMAGE

        };
        String sortOrderBy = DBContract.ImageGalleryTable.Colls.ID + " DESC";

        try{
            Cursor cursor = db.query(DBContract.ImageGalleryTable.TABLE_NAME,
                    projection, null, null, null, null, sortOrderBy);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++){
                Image image = new Image();
                image.setId(cursor.getLong(cursor.getColumnIndex(DBContract.ImageGalleryTable.Colls.ID)));
                image.setName(cursor.getString(cursor.getColumnIndex(DBContract.ImageGalleryTable.Colls.NAME)));
//                image.setImage(cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.IMAGE)));
                imageArrayList.add(image);
                cursor.moveToNext();
            }

            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Log.e(TAG, "findAll()");
        }

        return imageArrayList;
    }
}
