package com.yura8822.gallery;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.yura8822.R;

public class SaveImageFragment extends DialogFragment {
    private static final String TAG = "SaveImageFragment";

    public static final String DIALOG_SAVE_IMAGE = "save image";

    private EditText mEditTextName;
    private ImageView mImageView;

    public interface GenerateImage{
        void setViewForGenerateImage(ImageView imageView);
    }

    private GenerateImage mGenerateImage;

    public SaveImageFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_save_image, null);

        //add toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_save_image_dialog);
        toolbar.setTitle(R.string.save_image);
        toolbar.setLogo(R.drawable.baseline_save_alt_black_18dp);

        //init view
        mEditTextName = view.findViewById(R.id.name_image);
        mImageView = view.findViewById(R.id.save_image);

        //set image from View in saveImageDialog
        if (mGenerateImage != null){
            mGenerateImage.setViewForGenerateImage(mImageView);
        }else{
            Log.d(TAG, "you must implement the interface setGenerateImage(GenerateImage generateImage)");
        }


        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        builderDialog.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveImageFragment.this.getDialog().cancel();
                    }
                });

        return builderDialog.create();
    }

    public void setGenerateImage(GenerateImage generateImage) {
        mGenerateImage = generateImage;
    }
}
