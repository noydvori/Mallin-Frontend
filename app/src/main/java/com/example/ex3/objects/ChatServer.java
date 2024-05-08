package com.example.ex3.objects;

import com.example.ex3.entities.Msg;

public class ChatServer {
    private int id;

    private UserServer user;

    private MsgServer lastMessage;

    public ChatServer(int id, UserServer user, MsgServer lastMessage) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserServer getUser() {
        return user;
    }

    public void setUser(UserServer contact) {
        this.user = contact;
    }

    public MsgServer getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MsgServer lastMessage) {
        this.lastMessage = lastMessage;
    }
}
