package com.example.ex3.entities;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ex3.converters.MsgConverter;
import com.example.ex3.converters.UserConverter;

import java.sql.Struct;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "chat")
@TypeConverters({UserConverter.class, MsgConverter.class})
public class Chat {

    @PrimaryKey
    private int id;

    private User contact;

    @Nullable
    private Msg lastMessage;

    public Chat(int id, User contact, Msg lastMessage) {
        this.id = id;
        this.contact = contact;
        this.lastMessage = lastMessage;
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

    public Msg getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Msg lastMsg) {
        this.lastMessage = lastMessage;
    }

    public User getUser() {
        return contact;
    }
}
