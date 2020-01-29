package com.yura8822.gallery;

import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.MyViewHolder> {
    private static final String TAG = "GalleryImageAdapter";

    private String[] names;
    private String[] images;

    public GalleryImageAdapter(String[] names, String[] images){
        this.names = names;
        this.images = images;
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
                .inflate(R.layout.card_image_gallery, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardView cardView = holder.mCardView;
        TextView textViewName = cardView.findViewById(R.id.text_view_name);
        TextView textViewImage = cardView.findViewById(R.id.text_view_image);

        textViewName.setText(names[position]);
        textViewImage.setText(images[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }


}
