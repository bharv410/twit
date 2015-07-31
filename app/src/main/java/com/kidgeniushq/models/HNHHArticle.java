package com.kidgeniushq.models;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public class HNHHArticle {

    String caption, info, link;

    long id;

    public HNHHArticle(String blank){
        this.caption = "";
        this.link = "";
        this.info = "";
    }
    public HNHHArticle(String title, String url, String description, long insertId){
        this.caption = title;
        this.link = url;
        this.info = description;
        this.id = insertId;
    }

    public String getLink() {
        return link;
    }

    public String getCaption() {
        return caption;
    }

    public String getInfo() {
        return info;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
