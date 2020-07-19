package com.troofy.hopordrop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.troofy.hopordrop.R;

import java.util.ArrayList;

public class ErrorActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        ArrayList<String> messages = getIntent().getStringArrayListExtra("message");

        if(messages != null)
        {
            for(String msg : messages){

            }
        }

        this.context = this;

        Button tryBtn = (Button) findViewById(R.id.tryAgainBtn);
        tryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
