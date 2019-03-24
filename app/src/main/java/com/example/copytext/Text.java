package com.example.copytext;

public class Text {

    private String type;
    private String content;
    private int id;

    public Text(String type, String content, int id) {
        this.type = type;
        this.content = content;
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return this.id;
    }
}
