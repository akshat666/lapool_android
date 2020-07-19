/*
 * ************************************************************************
 *  *
 *  * TROOFY LABS - CONFIDENTIAL
 *  * __________________
 *  *  Copyright (c) 2016.
 *  *
 *  *  All Rights Reserved.
 *  *
 *  * NOTICE:  All information contained herein is, and remains
 *  * the property of Troofy Labs(OPC) Private Limited and its suppliers,
 *  * if any.  The intellectual and technical concepts contained
 *  * herein are proprietary to Troofy Labs(OPC) Private Limited
 *  * and its suppliers and may be covered by U.S. and Foreign Patents,
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * from Troofy Labs(OPC) Private Limited.
 *
 */

package com.troofy.hopordrop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.troofy.hopordrop.R;

public class EmergencyContactActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private TextView contact1;
    private TextView contact2;
    private int PICK_CONTACT_1 = 1;
    private int PICK_CONTACT_2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.emergency_contact);

        sharedPref = getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        contact1 = (TextView) findViewById(R.id.contact1);
        contact1.setText(sharedPref.getString(getString(R.string.contactname1),"Tap to add")+" - "+sharedPref.getString(getString(R.string.contactnumber1),"Emergency Contact 1"));
        contact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT_1);
            }
        });

        contact2 = (TextView) findViewById(R.id.contact2);
        contact2.setText(sharedPref.getString(getString(R.string.contactname2),"Tap to add")+" - "+sharedPref.getString(getString(R.string.contactnumber2),"Emergency Contact 2"));
        contact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT_2);
            }
        });


        TextView delCon1 = (TextView) findViewById(R.id.delContact1Btn);
        delCon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(getString(R.string.confirmation))
                        .setMessage(R.string.remove_contact_confirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                clearContact(1);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        TextView delCon2 = (TextView) findViewById(R.id.delContact2Btn);
        delCon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.remove_contact_confirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                clearContact(2);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void clearContact(int contact) {
        switch (contact){
            case 1:
                sharedPref.edit().remove(getString(R.string.contactname1)).apply();
                sharedPref.edit().remove(getString(R.string.contactnumber1)).apply();
                contact1.setText(R.string.tapToAddEmgCont);
                break;
            case 2:
                sharedPref.edit().remove(getString(R.string.contactname2)).apply();
                sharedPref.edit().remove(getString(R.string.contactnumber2)).apply();
                contact2.setText(R.string.tapToAddEmgCont);
                break;
            default:break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_CONTACT_1){
            if(resultCode == Activity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                sharedPref.edit().putString(getString(R.string.contactname1),name).apply();
                sharedPref.edit().putString(getString(R.string.contactnumber1),number).apply();
                contact1.setText(name+": "+number);
            }
        }else if(requestCode == PICK_CONTACT_2){
            if(resultCode == Activity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sharedPref = getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
                sharedPref.edit().putString(getString(R.string.contactname2),name).apply();
                sharedPref.edit().putString(getString(R.string.contactnumber2),number).apply();
                contact2.setText(name+": "+number);
            }
        }

    }
}
