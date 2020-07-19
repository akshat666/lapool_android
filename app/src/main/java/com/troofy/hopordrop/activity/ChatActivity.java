package com.troofy.hopordrop.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.ArrayMap;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.adapter.ChatArrayAdapter;
import com.troofy.hopordrop.bean.ChatMessage;
import com.troofy.hopordrop.bean.MessageBean;
import com.troofy.hopordrop.request.MessageGetRequest;
import com.troofy.hopordrop.request.MessagePostRequest;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by akshat666
 */
public class ChatActivity extends SpiceBaseActivity {

    private static final String TAG = "ChatActivity";

    private long authID;
    private long tripID = 0;
    private String token;
    private long msgID;
    private TextView chatHeading;
    private Context context;

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageView buttonSend;
    private boolean side = false;
    private String receivedMsg;
    private long chatAuthID;
    private SharedPreferences defaultSharedPref;
    private String chatName;
    private ScheduledExecutorService scheduleTaskExecutor;
    private Button returnTripBtn;
    private boolean isDataFetching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        this.context = this;
        defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        this.authID = defaultSharedPref.getLong("authID", 0);
        this.tripID = defaultSharedPref.getLong(getString(R.string.tripIdStr),0);
        this.token = defaultSharedPref.getString(getString(R.string.token),null);
        this.chatName = defaultSharedPref.getString(getString(R.string.chatNameStr),null);
        this.chatAuthID = defaultSharedPref.getLong(getString(R.string.chatAuthIDStr),0);

        //If trip is over
        if (tripID == 0) {
            Toast.makeText(context,
                    R.string.trip_ended_by_user, Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(ChatActivity.this, PoolMainActivity.class);
            startActivity(i);
            finish();
        }

        getSupportActionBar().setTitle(getString(R.string.chat_with)+" "+chatName);

        //For chatting
        buttonSend = (ImageView) findViewById(R.id.send);

        returnTripBtn = (Button) findViewById(R.id.returnTripBtn);
        returnTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, TripActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_chat);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(chatText.getText().toString().trim() == null || chatText.getText().toString().trim().equalsIgnoreCase("")){
                        return false;
                    }
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(chatText.getText().toString().trim() == null || chatText.getText().toString().trim().equalsIgnoreCase("")){
                    return;
                }
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        this.receivedMsg = getIntent().getStringExtra("msg");
        if(receivedMsg != null){
            receiveChatMessage(receivedMsg);
        }

        //First load all the chat history for this trip
        if(!isDataFetching){
            loadChatHistory();
        }

    }

    private void loadChatHistory() {
        isDataFetching = true;
        //Set chatAuthID as 0 so that we get all the chat history
        MessageGetRequest messageGetRequest = new MessageGetRequest(tripID, msgID, -1, token, authID,context);
        messageGetRequest.setRetryPolicy(null);
        //messageGetRequest.setRetryPolicy(null);
        spiceManager.execute(messageGetRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new MessageHistoryRequestListener());


    }

    private void receiveChatMessage(String message){
        chatArrayAdapter.add(new ChatMessage(false, message, chatName));
        side = !side;
    }

    private boolean sendChatMessage() {
        String msg = chatText.getText().toString().trim();
        chatArrayAdapter.add(new ChatMessage(true, chatText.getText().toString().trim(),getString(R.string.me)));
        chatText.setText("");
        //Send push message
        side = !side;

        MessageBean messageBean = new MessageBean();
        messageBean.setFrom(authID);
        messageBean.setTo(chatAuthID);
        messageBean.setTripID(tripID);
        messageBean.setMessage(msg);
        Genson gen = new Genson();
        MessagePostRequest request = new MessagePostRequest(gen.serialize(messageBean), token,authID, this);
        request.setRetryPolicy(null);
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new MessagePostRequestListener());
        return true;
    }

    private class MessagePostRequestListener implements com.octo.android.robospice.request.listener.RequestListener<Long> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(Long msgID) {


        }
    }

    private class MessageGetRequestListener implements RequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
        }

        @Override
        public void onRequestSuccess(List list) {
            if(list == null || list.isEmpty()){
                return;
            }
            for(ArrayMap arrayMap: (List<ArrayMap>)list){
                msgID = Long.valueOf(arrayMap.get("id").toString());
                receiveChatMessage(arrayMap.get("message").toString());
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private class MessageHistoryRequestListener implements RequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(List list) {
            isDataFetching = false;
            if(list != null && !list.isEmpty()){
                for(ArrayMap arrayMap: (List<ArrayMap>)list){
                    msgID = Long.valueOf(arrayMap.get("id").toString());
                    if(Long.parseLong(arrayMap.get("from").toString()) == authID){
                        chatArrayAdapter.add(new ChatMessage(true, arrayMap.get("message").toString(),getResources().getString(R.string.me)));
                    }else{
                        receiveChatMessage(arrayMap.get("message").toString());
                    }
                }
            }

            pollMessages();

        }
    }

    public void pollMessages(){
        //Poll for new messages
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a task to run every 5 secs
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                MessageGetRequest messageGetRequest = new MessageGetRequest(tripID, msgID, chatAuthID, token, authID,context);
                spiceManager.execute(messageGetRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new MessageGetRequestListener());

//                // If you need update UI, simply do this:
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        // update your UI component here.
//
//                    }
//                });
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scheduleTaskExecutor != null){
            scheduleTaskExecutor.shutdownNow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If trip is over
        this.tripID = defaultSharedPref.getLong(getString(R.string.tripIdStr),0);
        if (tripID == 0) {
            Toast.makeText(context,
                    R.string.trip_ended_by_user, Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(ChatActivity.this, PoolMainActivity.class);
            startActivity(i);
            finish();
        }
        if(!isDataFetching){
            loadChatHistory();
        }
    }
}
