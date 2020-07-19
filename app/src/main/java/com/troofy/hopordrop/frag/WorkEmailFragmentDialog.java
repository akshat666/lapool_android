package com.troofy.hopordrop.frag;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.util.SystemUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkEmailFragmentDialog extends DialogFragment {

    private EditText emailIDTxt;
    private EditText codeTxt;


    public WorkEmailFragmentDialog() {
        // Required empty public constructor
    }

    public static final String TAG = "WorkEmailFragmentDialog";

    private WorkEmailDialogInterface dialogInterface;

    public static interface WorkEmailDialogInterface {
        public void verifyEmail(String email, String code);

        public Activity getActivityFromParent();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout. email_code_dialog, null);
        emailIDTxt = (EditText)view.findViewById(R.id.emailIDTxt);
        codeTxt = (EditText) view.findViewById(R.id.codeTxt);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        for(Fragment fragment : getActivity().getSupportFragmentManager().getFragments()){
            if(fragment instanceof NetworkFragment){
                dialogInterface = (WorkEmailDialogInterface) fragment;
            }
        }

        builder
                .setView(view)
                .setTitle("Enter new email")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (TextUtils.isEmpty(codeTxt.getText().toString().trim()) || !SystemUtils.isValidEmail(emailIDTxt.getText())) {
                            Toast.makeText(getActivity(), "Enter a valid Email and Code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialogInterface.verifyEmail(emailIDTxt.getText().toString().trim(),codeTxt.getText().toString().trim() );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
