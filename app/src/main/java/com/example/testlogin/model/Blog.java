package com.example.testlogin.model;

public class Blog {
    public long id;          // autoincrement
    public String title;
    public String story;

    public Blog(long id, String title, String story) {
        this.id = id;
        this.title = title;
        this.story = story;
    }
    public Blog(String title, String story) { this(-1, title, story); }
}
