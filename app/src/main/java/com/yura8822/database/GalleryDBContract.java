package com.yura8822.database;

public final class GalleryDBContract {

    public GalleryDBContract() {
    }

    public static final class ImageGalleryTable {
        private static final String TABLE_NAME_1 = "image_gallery";

        public static final class Colls {
            public static final String ID = "_id";
            public static final String NAME = "name";
            public static final String IMAGE = "image";
        }

        public static final String SQL_CREATE_IMAGE_GALLERY = "CREATE TABLE " + TABLE_NAME_1 + " (" +
                Colls.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Colls.NAME + " TEXT," +
                Colls.IMAGE+ " TEXT)";
    }
}
