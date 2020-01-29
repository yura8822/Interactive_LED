package com.yura8822.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.yura8822.R;

public class GalleryImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        //Mock data
        String[] names = new String[] {"name1", "name2", "name3", "name4"};
        String[] images = new String[] {"image1", "image2", "image3", "image4"};

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
