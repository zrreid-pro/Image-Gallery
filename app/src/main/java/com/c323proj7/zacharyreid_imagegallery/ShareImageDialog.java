package com.c323proj7.zacharyreid_imagegallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ShareImageDialog extends AppCompatDialogFragment {
    private ShareImageDialog.ShareDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.share_dialog_layout, null);
        builder.setView(view);
        builder.setTitle("Share Image");
        final EditText editText = view.findViewById(R.id.editText_saveDialog);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = editText.getText().toString();
                listener.shareDialogSignal(fileName);
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
        listener = (ShareImageDialog.ShareDialogListener) context;
    }

    public interface ShareDialogListener {
        void shareDialogSignal(String fileName);
    }
}
