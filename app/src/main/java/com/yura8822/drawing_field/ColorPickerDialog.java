package com.yura8822.drawing_field;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    public final static String DIALOG_COLOR_PICKER = "color_picker";

    public interface FragmentListenerColorPicker{
        void fragmentColorSelected(int color);
    }

    private FragmentListenerColorPicker fragmentListenerColorPicker;

    private ColorPicker mColorPicker;

    public ColorPickerDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder colorPickerDialog = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_color_picker, null);

        //add toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_color_picker_dialog);
        toolbar.setTitle(R.string.color_picker);
        toolbar.setLogo(R.drawable.baseline_color_lens_black_18dp);

        //get instance ColorPicker
        mColorPicker = view.findViewById(R.id.color_picker);
        mColorPicker.setListenerColorPicker(listenerColorPicker);

        // Inflate and set the layout for the dialog
        colorPickerDialog.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (fragmentListenerColorPicker != null){
                            fragmentListenerColorPicker.fragmentColorSelected(mColorPicker.getResultColor());
                            Log.d(TAG, "onClick() -> "  + String.valueOf(mColorPicker.getResultColor()));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
            if (fragmentListenerColorPicker != null){
                fragmentListenerColorPicker.fragmentColorSelected(color);
                Log.d(TAG, "colorSelected() -> " + String.valueOf(color));
            }
            ColorPickerDialog.this.getDialog().cancel();
        }
    };

    public void setFragmentListenerColorPicker(FragmentListenerColorPicker fragmentListenerColorPicker) {
        this.fragmentListenerColorPicker = fragmentListenerColorPicker;
        Log.d(TAG, "setFragmentListenerColorPicker()");
    }
}
