package com.yura8822.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.yura8822.R;

public class ColorPickerDialog extends DialogFragment {
    private final static String TAG = "ColorPickerDialog";
    public final static String DIALOG_COLOR_PICKER = "com.yura8822.dialog_color_picker";
    private final static String EXTRA_COLOR = "com.yura8822.extra_color";

    private ColorPicker mColorPicker;

    public ColorPickerDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_color_picker, null);

        //add toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_color_picker_dialog);
        toolbar.setTitle(R.string.color_picker);
        toolbar.setLogo(R.drawable.ic_select_color);

        //get instance ColorPicker
        mColorPicker = view.findViewById(R.id.color_picker);
        mColorPicker.setListenerColorPicker(listenerColorPicker);

        // Inflate and set the layout for the dialog
        AlertDialog.Builder colorPickerDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK, mColorPicker.getResultColor());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ColorPickerDialog.this.getDialog().cancel();
                    }
                });
        Log.d(TAG, " onCreateDialog()");
        return colorPickerDialog.create();
    }

    private ColorPicker.ListenerColorPicker listenerColorPicker = new ColorPicker.ListenerColorPicker() {
        @Override
        public void colorSelected(int color) {
            sendResult(Activity.RESULT_OK, color);
            ColorPickerDialog.this.getDialog().cancel();
        }
    };

    private void sendResult(int resultCode, int color) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_COLOR, color);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public static int getColor(Intent data){
        return data.getIntExtra(EXTRA_COLOR, 0);
    }
}
