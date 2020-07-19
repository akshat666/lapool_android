package com.troofy.hopordrop.frag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.troofy.hopordrop.R;

/**
 * Created by akshat.
 */
public class NetworkDialogFragment extends DialogFragment{

    public static final String TAG = "NetworkDialogFragment";

    private NetworkDialogInterface dialogInterface;

    public static interface NetworkDialogInterface {
        public void tryAgain();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.no_network_msg).setTitle(R.string.no_network)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Try again for internet
                        dialogInterface.tryAgain();

                    }
                })
                .setNegativeButton(null,null);
        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        return alert;
    }


    /**
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        dialogInterface = (NetworkDialogInterface) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        dialogInterface = null;
        super.onDetach();
    }
}
