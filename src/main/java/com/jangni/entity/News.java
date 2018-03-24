package com.jangni.entity;

/**
 * @Description:    消息bean
 * @Autor: Jangni
 * @Date: Created in  2018/3/24/024 23:26
 */
public class News {
    
    private String title;
    private String context;
    private String author;
    private String createDate;

    public News(String title, String context, String author, String createDate) {
        this.title = title;
        this.context = context;
        this.author = author;
        this.createDate = createDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
