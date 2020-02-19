package com.yura8822.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;

import java.util.List;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.MyViewHolder> {
    private static final String TAG = "GalleryImageAdapter";

    public interface LisetenerViewHolder{
        void onClick(long id);
    }
    private LisetenerViewHolder mLisetenerViewHolder;

    private TextView mTextViewName;
    private ImageView mImageView;
    private Context mContext;

    private List<Image> mImageList;

    public GalleryImageAdapter(List<Image> list, Context context){
        this.mImageList = list;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        CardView cardView = holder.mCardView;
        mTextViewName = cardView.findViewById(R.id.text_view_name);
        mImageView = cardView.findViewById(R.id.view_image);

        //get Image
        final Image image = mImageList.get(position);

        mTextViewName.setText(image.getName());
        Drawable drawable = ImageUtils.StringToDrawble(mContext, image.getImage());
        mImageView.setImageDrawable(drawable);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLisetenerViewHolder != null){
                    mLisetenerViewHolder.onClick(image.getId());
                }else
                {
                    Log.e(TAG, "set field mLisetenerViewHolder via GalleryImageAdapter.setLisetenerViewHolder function");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public void setLisetenerViewHolder(LisetenerViewHolder lisetenerViewHolder) {
        mLisetenerViewHolder = lisetenerViewHolder;
    }
}
