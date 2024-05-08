package com.example.ex3.entities;

import androidx.room.PrimaryKey;

import java.util.List;

public class CurChat {
    @PrimaryKey
    private int id;

    private User contact;

    private List<Msg> messages;

    public CurChat(int id, User contact, List<Msg> messages) {
        this.id = id;
        this.contact = contact;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    public List<Msg> getListMsg() {
        return messages;
    }
}
