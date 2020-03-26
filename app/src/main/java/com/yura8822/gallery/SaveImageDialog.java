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

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageDialog extends DialogFragment {
    private static final String TAG = "SaveImageDialog";

    public static final String DIALOG_SAVE_IMAGE = "save image";

    private EditText mEditTextName;
    private ImageView mImageView;

    public interface GenerateImage{
        void setViewForGenerateImage(ImageView imageView);
    }

    public interface ConversionImage{
        void saveImage(String nameImage);
    }

    private GenerateImage mGenerateImage;
    private ConversionImage mConversionImage;

    public SaveImageDialog() {
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
                        //conversion of an picture array   into a string for saving in the database
                        if (mConversionImage != null){
                            String imageName = mEditTextName.getText().toString();
                            if (!imageName.isEmpty()){
                                mConversionImage.saveImage(imageName);
                            }else {
                                SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                                imageName = sd.format(new Date());
                                mConversionImage.saveImage(imageName);
                            }

                        }else {
                            Log.d(TAG, "you must implement the interface setConversionImage(ConversionImage conversionImage)");
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveImageDialog.this.getDialog().cancel();
                    }
                });

        return builderDialog.create();
    }

    public void setGenerateImage(GenerateImage generateImage) {
        mGenerateImage = generateImage;
    }

    public void setConversionImage(ConversionImage conversionImage) {
        mConversionImage = conversionImage;
    }
}
