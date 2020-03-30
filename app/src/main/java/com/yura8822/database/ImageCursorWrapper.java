package com.yura8822.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import static com.yura8822.database.DBContract.*;

public class ImageCursorWrapper extends CursorWrapper {
    public ImageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Image getImage(){
        long id = getLong(getColumnIndex(ImageGalleryTable.Colls.ID));
        String name = getString(getColumnIndex(ImageGalleryTable.Colls.NAME));
        byte[] imageByteArray = getBlob(getColumnIndex(ImageGalleryTable.Colls.IMAGE));
        long date = getLong(getColumnIndex(ImageGalleryTable.Colls.DATE));
        return new Image(id, name, imageByteArray, date);
    }
}

