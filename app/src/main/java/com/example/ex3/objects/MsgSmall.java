package com.example.ex3.objects;

public class MsgSmall {
    public static class Sender{
        private String username;

        public Sender(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
    private int id;
    private String content;
    private String created;
    private Sender sender;

    public MsgSmall(int id, String content, String created, Sender sender) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.sender = sender;
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

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
