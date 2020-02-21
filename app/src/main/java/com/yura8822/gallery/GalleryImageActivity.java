package com.yura8822.gallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;
import com.yura8822.database.GalleryDBHelper;

import java.util.List;

public class GalleryImageActivity extends AppCompatActivity {
    private static final String TAG = "GalleryImageActivity";

    private static final String EXTRA_IMAGE_ID = "image_ID";

    private GalleryDBHelper mGalleryDBHelper;

    private List<Image> mImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        mGalleryDBHelper = new GalleryDBHelper(getApplicationContext());

        //init list
        mImageList = mGalleryDBHelper.findAll();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_galerry);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        // specify an adapter
        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(mImageList, this);
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
