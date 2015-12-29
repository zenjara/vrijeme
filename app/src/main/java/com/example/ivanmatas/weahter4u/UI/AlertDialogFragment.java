package com.example.ivanmatas.weahter4u.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.example.ivanmatas.weahter4u.R;

/**
 * Created by IvanMatas on 12/4/2015.
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error_title).setMessage(R.string.error_message).setPositiveButton(R.string.error_okbutton,null);
        AlertDialog dialog=builder.create();
        return  dialog;
    }
}
