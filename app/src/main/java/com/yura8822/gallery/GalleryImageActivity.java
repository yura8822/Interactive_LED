package com.yura8822.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        mGalleryDBHelper = new GalleryDBHelper(getApplicationContext());

        //init list
        List<Image> imageList = mGalleryDBHelper.findAll();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_galerry);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        // specify an adapter
        ImageAdapter imageAdapter = new ImageAdapter(GalleryImageActivity.this, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public static Intent newIntent(Context context){
        return new Intent(context, GalleryImageActivity.class);
    }

    public static long getImageID(Intent result){
        return result.getLongExtra(EXTRA_IMAGE_ID, 0);
    }

    private void setImageStringForResult(long imageID){
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_ID, imageID);
        setResult(RESULT_OK, data);
        this.finish();
    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mTextViewName;
        private ImageView mImageView;

        private Image mImage;

        private View.OnClickListener mOnClickListener;
        private View.OnLongClickListener mOnLongClickListener;


        public ImageHolder (LayoutInflater inflater, ViewGroup parent, Context context){
            super(inflater.inflate(R.layout.card_image_item, parent,false));
            mContext = context;
            mTextViewName = itemView.findViewById(R.id.text_view_name);
            mImageView = itemView.findViewById(R.id.view_image);
        }

        public void bind(Image image){
            mImage = image;
            mTextViewName.setText(mImage.getName());
            mImageView.setImageDrawable(ImageUtils.StringToDrawble(mContext, mImage.getImage()));
            //register listener
            itemView.setOnClickListener(mOnClickListener);
            itemView.setOnLongClickListener(mOnLongClickListener);

            if (image.isChecked()){
                itemView.setBackgroundColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
            }else
                itemView.setBackgroundColor(Color.WHITE);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            mOnLongClickListener = onLongClickListener;
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder>{
        private static final int SELECT = 0;
        private static final int DELETE = 1;
        private int mode;

        List<Image> mImageList;
        Context mContext;

        public ImageAdapter(Context context, List<Image> imageList) {
            mContext = context;
            mImageList = imageList;
        }

        @NonNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(GalleryImageActivity.this);
            return new ImageHolder(inflater, parent, mContext);
        }

        @Override
        public void onBindViewHolder(@NonNull final ImageHolder holder, final int position) {
            final Image image = mImageList.get(position);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (mode){
                        case SELECT:
                            setImageStringForResult(image.getId());
                            break;
                        case DELETE:
                            if (image.isChecked()){
                                image.setChecked(false);
                                notifyItemChanged(position);

                            }else {
                                image.setChecked(true);
                                notifyItemChanged(position);
                            }


                            for (Image value: mImageList){
                                Log.d(TAG, " "+ value.isChecked());
                            }
                            Log.d(TAG, " ------------------------------------");

                            break;
                    }
                }
            });
            holder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mode = DELETE;
                    image.setChecked(true);
                    notifyItemChanged(position);
                    return true;
                }
            });
            holder.bind(image);
        }

        @Override
        public int getItemCount() {
            return mImageList.size();
        }
    }




}
