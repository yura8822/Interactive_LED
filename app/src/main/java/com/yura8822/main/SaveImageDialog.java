package com.yura8822.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.yura8822.R;
import com.yura8822.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveImageDialog extends DialogFragment {
    private static final String TAG = "SaveImageDialog";
    public static final String DIALOG_SAVE_IMAGE = "com.yura8822.SaveImageDialog";
    private static final String EXTRA_SAVED_IMAGE_NAME = "com.yura8822.extra_saved_image_name";

    private EditText mEditTextName;
    private ImageView mImageView;

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
        toolbar.setTitle(R.string.title_save_img);
        toolbar.setLogo(R.drawable.ic_save_img);

        //init view
        mEditTextName = view.findViewById(R.id.name_image);
        mImageView = view.findViewById(R.id.save_image);

        View pixelGirdView = getActivity().findViewById(R.id.pixel_gird);
        mImageView.setImageBitmap(ImageUtils.getBitmapFromView(pixelGirdView));

        // Inflate and set the layout for the dialog
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nameImg = mEditTextName.getText().toString();
                        if (nameImg.isEmpty()){
                            SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss",
                                    Locale.getDefault());
                            nameImg = sd.format(new Date());
                        }
                        sendResult(Activity.RESULT_OK, nameImg);
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

    private void sendResult(int resultCode, String nameImg) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SAVED_IMAGE_NAME, nameImg);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public static String getNameImg(Intent data){
        return data.getStringExtra(EXTRA_SAVED_IMAGE_NAME);
    }
}
