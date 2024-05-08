package com.example.ex3.objects;

import com.example.ex3.entities.User;

public class MsgServer {
    private int id;

    private String created; // date

    private UserFull sender;

    private String content;

    public MsgServer(int id, String created, UserFull sender, String content) {
        this.id = id;
        this.created = created;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public UserFull getSender() {
        return sender;
    }

    public void setSender(UserFull sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
