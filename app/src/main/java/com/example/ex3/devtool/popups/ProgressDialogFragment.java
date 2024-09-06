package com.example.ex3.devtool.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.ex3.R;

public class ProgressDialogFragment extends DialogFragment {

    private int textResource; // Default text resource

    // Method to set the text resource from outside the fragment
    public void setTextResource(int textResource) {
        this.textResource = textResource;
        if (getDialog() != null && getDialog().isShowing()) {
            updateText();
        }
    }

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
            updateText(); // Update the text when the dialog is started
        }
    }

    // Method to update the text inside the dialog
    private void updateText() {
        if (getDialog() != null) {
            TextView progressText = getDialog().findViewById(R.id.progressText);
            if (progressText != null) {
                progressText.setText(textResource);
            }
        }
    }
}
