package com.example.ex3.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ex3.converters.MsgConverter;
import com.example.ex3.converters.UserConverter;

@Entity(tableName = "Msg")
@TypeConverters({UserConverter.class})
public class Msg {

    @PrimaryKey
    private int id;

    private String created; // date

    private User sender;

    private String content;

    public Msg(int id, String created, User sender, String content) {
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getContact() {
        return sender;
    }
}
