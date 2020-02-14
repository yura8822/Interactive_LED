package com.yura8822.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.MyViewHolder> {
    private static final String TAG = "GalleryImageAdapter";

    private TextView mTextViewName;
    private ImageView mImageView;
    private Context mContext;

    private String[] names;
    private String[] images;

    public GalleryImageAdapter(String[] names, String[] images, Context context){
        this.names = names;
        this.images = images;
        this.mContext = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;

        public MyViewHolder(@NonNull CardView mCardView) {
            super(mCardView);
            this.mCardView = mCardView;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_image_item, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardView cardView = holder.mCardView;
        mTextViewName = cardView.findViewById(R.id.text_view_name);
        mImageView = cardView.findViewById(R.id.view_image);

        mTextViewName.setText(names[position]);
        Drawable drawable = ImageUtils.StringToDrawble(mContext, images[position]);
        mImageView.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }


}
