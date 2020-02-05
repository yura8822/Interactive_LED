package com.yura8822.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

import com.yura8822.R;
import com.yura8822.database.GalleryDBContract;
import com.yura8822.database.GalleryDBHelper;

public class GalleryImageActivity extends AppCompatActivity {
    private static final String TAG = "GalleryImageActivity";

    private GalleryDBHelper mGalleryDBHelper;

    private String[] names;
    private String[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        mGalleryDBHelper = new GalleryDBHelper(getApplicationContext());
        try{
            SQLiteDatabase db = mGalleryDBHelper.getReadableDatabase();

            String[] projection = {GalleryDBContract.ImageGalleryTable.Colls.ID,
                    GalleryDBContract.ImageGalleryTable.Colls.NAME,
                    GalleryDBContract.ImageGalleryTable.Colls.IMAGE

            };
            Cursor cursor = db.query(GalleryDBContract.ImageGalleryTable.TABLE_NAME,
                    projection, null, null, null, null, null);

            names = new String[cursor.getCount()];
            images = new String[cursor.getCount()];

            cursor.moveToFirst();

            for (int i = 0; i < names.length; i++){
                names[i] = cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.NAME));
                images[i] = cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.IMAGE));
                cursor.moveToNext();
            }

            db.close();
            cursor.close();
        }catch (SQLiteException e){
            Log.e(TAG, "sql lite error");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_galerry);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // specify an adapter
        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(names, images);
        recyclerView.setAdapter(galleryImageAdapter);
    }
}
