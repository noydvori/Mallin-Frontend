package com.example.ex3.objects;


import java.util.Date;

public class LastMsg {
    private int id;
    private String content;
    private String created;

    public LastMsg(int id, String created, String content) {
        this.id = id;
        this.content = content;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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