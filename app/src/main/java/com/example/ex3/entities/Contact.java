package com.example.ex3.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ex3.adapters.LastMsgConverter;
import com.example.ex3.adapters.UserInfoConverter;
import com.example.ex3.objects.LastMsg;
import com.example.ex3.objects.UserInfo;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey
    private int id;
    @NonNull
    private String username;
    private String displayName;
    private String profilePic;
    private int messageId;
    private String content;
    private String created;



    public Contact(int id, UserInfo userInfo, LastMsg lastMsg) {
        this.id = id;
        this.username = userInfo.getUsername();
        this.displayName = userInfo.getDisplayName();
        this.profilePic = userInfo.getProfilePic();
        if (lastMsg != null) {
            this.messageId = lastMsg.getId();
            this.content = lastMsg.getContent();
            this.created = lastMsg.getCreated();
        } else {
            // Assign default values or handle null case as needed
            this.messageId = -1;
            this.content = "";
            this.created = "";
        }
    }
    public Contact() {

    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public String getUsername() {
        return this.username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}