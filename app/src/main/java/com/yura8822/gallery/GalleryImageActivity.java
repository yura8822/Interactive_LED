package com.yura8822.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    private static final String EXTRA_IMAGE_ID = "image_ID";

    private GalleryDBHelper mGalleryDBHelper;

    private String[] names;
    private String[] images;
    private long[] id;

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


            //init array
            id = new long[cursor.getCount()];
            names = new String[cursor.getCount()];
            images = new String[cursor.getCount()];

            cursor.moveToFirst();

            for (int i = 0; i < names.length; i++){
                id[i] = cursor.getLong(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.ID));
                names[i] = cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.NAME));
                images[i] = cursor.getString(cursor.getColumnIndex(GalleryDBContract.ImageGalleryTable.Colls.IMAGE));
                cursor.moveToNext();
            }

            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Log.e(TAG, "sql read error");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_galerry);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        // specify an adapter
        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(names, images, id, this);
        //set click listener for adapter
        galleryImageAdapter.setLisetenerViewHolder(new GalleryImageAdapter.LisetenerViewHolder() {
            @Override
            public void onClick(long id) {
                setImageStringForResult(id);
            }
        });
        recyclerView.setAdapter(galleryImageAdapter);
    }


    private void setImageStringForResult(long imageID){
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_ID, imageID);
        setResult(RESULT_OK, data);
        this.finish();
    }

    public static long getImageID(Intent result){
        return result.getLongExtra(EXTRA_IMAGE_ID, 0);
    }


}
