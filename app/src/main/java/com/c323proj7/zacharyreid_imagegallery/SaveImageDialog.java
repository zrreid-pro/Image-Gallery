package com.c323proj7.zacharyreid_imagegallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class SaveImageDialog extends AppCompatDialogFragment {
    private SaveDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_dialog_layout, null);
        builder.setView(view);
        builder.setTitle("Save Image");
        final EditText editText = view.findViewById(R.id.editText_saveDialog);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = editText.getText().toString();
                listener.saveDialogSignal(fileName);
            }
        });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (SaveDialogListener) context;
    }

    public interface SaveDialogListener {
        void saveDialogSignal(String fileName);
    }
}
