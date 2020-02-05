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

        //test data;
        insert(db, "image_1", "12234567898");
        insert(db, "image_2", "568474759309278");
        insert(db, "image_3", "d482935776y62893");
        insert(db, "image_4", "45348838587855676");
        insert(db, "image_5", "573848579988595");
        insert(db, "image_6", "573848579988595");
        insert(db, "image_7", "573848579988595");
        insert(db, "image_8", "573848579988595");
        insert(db, "image_9", "573848579988595");
        insert(db, "image_10", "573848579988595");
        insert(db, "image_11", "573848579988595");
        insert(db, "image_12", "573848579988595");
        insert(db, "image_13", "573848579988595");
        insert(db, "image_14", "573848579988595");
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
