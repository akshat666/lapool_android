package com.troofy.hopordrop.bean;

/**
 * Created by akshat666
 */
public class ChatMessage {
    public boolean left;
    public String message;
    public String title;

    public ChatMessage(boolean left, String message, String title) {
        super();
        this.left = left;
        this.message = message;
        this.title = title;
    }
}
