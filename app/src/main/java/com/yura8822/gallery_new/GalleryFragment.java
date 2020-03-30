package com.yura8822.gallery_new;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;
import com.yura8822.database.Image;
import com.yura8822.database.ImageLab;
import com.yura8822.utils.ImageUtils;

import java.util.List;


public class GalleryFragment extends Fragment {
    private Toolbar mToolbarBottom;
    private RecyclerView mRecyclerViewImages;
    private GalleryAdapter mAdapter;
    private List<Image> mImages;

    public GalleryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mToolbarBottom = view.findViewById(R.id.toolbar_gallery);
        mToolbarBottom.setTitleTextColor(Color.DKGRAY);
        mToolbarBottom.inflateMenu(R.menu.fragment_gallery);
        mToolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_image_button:{
                        return true;
                    }
                    default:{
                        return false;
                    }
                }
            }
        });

        mImages = ImageLab.get(getContext().getApplicationContext()).getImages();
        mRecyclerViewImages = view.findViewById(R.id.recycler_view_gallery);
        mRecyclerViewImages.setHasFixedSize(true);
        mRecyclerViewImages.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new GalleryAdapter(mImages);
        mRecyclerViewImages.setAdapter(mAdapter);

        return view;
    }

    private void showToolbar(){

    }

    private void hideTiilbar(){

    }

    private void updateTitleToolbar(int count){
        String title = getActivity().getResources().getString(R.string.title_bottom_toolbar_gallery);
        mToolbarBottom.setTitle(String.format(title, count));
    }

    private class GalleryHolder extends RecyclerView.ViewHolder{
        private TextView mNameView;
        private ImageView mImageView;
        private CardView mCardView;

        private Image mImage;

        private View.OnClickListener mOnClickListener;
        private View.OnLongClickListener mOnLongClickListener;

        GalleryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_image, parent, false));
            mNameView = itemView.findViewById(R.id.item_view_name);
            mImageView = itemView.findViewById(R.id.item_view_image);
            mCardView = itemView.findViewById(R.id.item_view_card);
        }

        void bind(Image image){
            Resources resources = getActivity().getResources();
            mImage = image;
            mNameView.setText(image.getName());
            mImageView.setImageDrawable(ImageUtils.byteArrayToDrawable(resources, mImage.getImage()));
            if (mImage.isChecked()){
                mCardView.setCardBackgroundColor(resources.getColor(R.color.colorSelectedCard));
            }else {
                mCardView.setCardBackgroundColor(Color.WHITE);
            }
        }

        void setOnClickListener(View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            mOnLongClickListener = onLongClickListener;
        }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryHolder>{
        private List<Image> mImages;

        public GalleryAdapter(List<Image> images) {
            mImages = images;
        }

        @NonNull
        @Override
        public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new GalleryHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
            Image image = mImages.get(position);
            holder.bind(image);
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }
}
