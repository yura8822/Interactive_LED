package com.yura8822.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;
import com.yura8822.database.GalleryDBHelper;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryImageActivity extends AppCompatActivity {
    private static final String TAG = "GalleryImageActivity";

    private static final String EXTRA_IMAGE_ID = "image_ID";

    private static final int SELECT = 0;
    private static final int DELETE = 1;
    private int mode;

    private GalleryDBHelper mGalleryDBHelper;
    private Toolbar mToolbarUp;
    private Toolbar mToolbarDown;
    private RecyclerView mRecyclerView;

    private ImageAdapter mImageAdapter;
    private List<Image> mImages;
    private ImageButton mButtonDelete;
    private TextView mTextCount;

    private int actionBarHeight;
    private int mCountMarkedDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);
        //calculate size toolbar
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        //init view
        mToolbarUp = findViewById(R.id.toolbar_up_gallery);
        mToolbarDown = findViewById(R.id.toolbar_down_gallery);
        mToolbarDown.setVisibility(View.GONE);

        //init delete button
        mButtonDelete = findViewById(R.id.delete_image_button);
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGalleryDBHelper.deleteByListId(mImages);
                for (int i = 0; i < mImages.size(); i++){
                    if (mImages.get(i).isChecked()){
                        Log.d(TAG, "name " + mImages.get(i).getName());
                        mImages.remove(i);
                        i--;
                        mImageAdapter.notifyItemRemoved(i);
                    }
                }
                hideToolbarDown();
            }
        });
        //init text view for output number of marked cards to delete
        mTextCount = findViewById(R.id.count_selected_textView);

        mGalleryDBHelper = new GalleryDBHelper(getApplicationContext());

        //init list
        mImages = mGalleryDBHelper.findAll();

        mRecyclerView = findViewById(R.id.recycler_view_galerry);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        // specify an adapter
        mImageAdapter = new ImageAdapter(GalleryImageActivity.this, mImages);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    @Override
    public void onBackPressed() {
        switch (mode){
            case SELECT:
                super.onBackPressed();
                break;
            case DELETE:
                hideToolbarDown();
                break;
        }
    }

    private void showToolbarDown(){
        mToolbarDown.setVisibility(View.VISIBLE);
        mRecyclerView.setPadding(0,0,0, actionBarHeight*2);
        mCountMarkedDeleted = 1;
        mTextCount.setText(String.valueOf(mCountMarkedDeleted));
    }

    private void hideToolbarDown(){
        mode = SELECT;
        mToolbarDown.setVisibility(View.GONE);
        mRecyclerView.setPadding(0, 0, 0, actionBarHeight);
        for (Image value : mImages){
            value.setChecked(false);
        }
        mImageAdapter.notifyDataSetChanged();
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
        private CardView mCardView;

        private Image mImage;

        private View.OnClickListener mOnClickListener;
        private View.OnLongClickListener mOnLongClickListener;


        ImageHolder(LayoutInflater inflater, ViewGroup parent, Context context){
            super(inflater.inflate(R.layout.card_image_item, parent,false));
            mContext = context;
            mTextViewName = itemView.findViewById(R.id.text_view_name);
            mImageView = itemView.findViewById(R.id.view_image);
            mCardView = itemView.findViewById(R.id.card_view_image);
        }

        void bind(Image image){
            mImage = image;
            mTextViewName.setText(mImage.getName());
            mImageView.setImageDrawable(ImageUtils.StringToDrawble(mContext, mImage.getImage()));
            //register listener
            itemView.setOnClickListener(mOnClickListener);
            itemView.setOnLongClickListener(mOnLongClickListener);

            if (image.isChecked()){
                mCardView.setCardBackgroundColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
            }else
                mCardView.setCardBackgroundColor(Color.WHITE);
        }

         void setOnClickListener(View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

         void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            mOnLongClickListener = onLongClickListener;
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder>{
        private List<Image> mImageList;
        private Context mContext;

        ImageAdapter(Context context, List<Image> imageList) {
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
                                mCountMarkedDeleted--;
                            }else {
                                image.setChecked(true);
                                mCountMarkedDeleted++;
                            }
                            break;
                    }
                    notifyItemChanged(position);
                    mTextCount.setText(String.valueOf(mCountMarkedDeleted));
                }
            });
            holder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mode = DELETE;
                    image.setChecked(true);
                    notifyItemChanged(position);
                    showToolbarDown();
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
