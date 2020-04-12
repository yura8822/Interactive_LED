package com.yura8822.database;

import android.provider.BaseColumns;

public final class DBContract {

    public DBContract() {
    }

    static final class ImageGalleryTable {
        static final String TABLE_NAME = "image_gallery";

        static final class Colls implements BaseColumns {
            static final String ID = "_id";
            static final String NAME = "name";
            static final String IMAGE = "image";
            static final String DATE = "date";
        }
        static final String SQL_CREATE_IMAGE_GALLERY = "CREATE TABLE " + TABLE_NAME + " (" +
                Colls.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Colls.NAME + " TEXT," +
                Colls.IMAGE + " BLOB,"+
                Colls.DATE + " INTEGER)";
    }

    static final class DeviceTable{
        static final String TABLE_NAME = "device";

        static final class Colls implements BaseColumns{
            static final String ID = "_id";
            static final String KEY = "key";
            static final String MAC = "mac";
        }
        static final String SQL_CREATE_DEVICE = "CREATE TABLE " + TABLE_NAME + " (" +
                Colls.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Colls.KEY + " TEXT," +
                Colls.MAC + " TEXT)";
    }


}
