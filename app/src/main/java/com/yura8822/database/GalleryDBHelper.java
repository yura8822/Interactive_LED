package com.yura8822.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GalleryDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gallery.db";

    public GalleryDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GalleryDBContract.ImageGalleryTable.SQL_CREATE_IMAGE_GALLERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SQLiteDatabase db, String name, String image){
        ContentValues contentValues = new ContentValues();
        contentValues.put(GalleryDBContract.ImageGalleryTable.Colls.NAME, name);
        contentValues.put(GalleryDBContract.ImageGalleryTable.Colls.IMAGE, image);
        db.insert(GalleryDBContract.ImageGalleryTable.TABLE_NAME, null, contentValues);
    }
}
