package com.yura8822.database;

import android.provider.BaseColumns;

public final class GalleryDBContract {

    public GalleryDBContract() {
    }

    public static final class ImageGalleryTable {
        public static final String TABLE_NAME = "image_gallery";

        public static final class Colls implements BaseColumns {
            public static final String ID = "_id";
            public static final String NAME = "name";
            public static final String IMAGE = "image";
            public static final String DATE = "date";
        }

        public static final String SQL_CREATE_IMAGE_GALLERY = "CREATE TABLE " + TABLE_NAME + " (" +
                Colls.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Colls.NAME + " TEXT," +
                Colls.IMAGE + " BLOB,"+
                Colls.DATE + " INTEGER)";
    }
}
