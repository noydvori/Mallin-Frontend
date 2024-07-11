package com.example.ex3.devtool.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ex3.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(false); // Prevents the dialog from being dismissed by clicking outside

        // Create the dialog
        Dialog dialog = builder.create();

        // Disable dismissal on touch outside
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

}

