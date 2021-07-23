package com.yhjoo.dochef.model;

public class Notification {
    private final int _id;
    private final int type;
    private final String intent_name;
    private final String intent_data;
    private final String contents;
    private final String image;
    private final long dateTime;
    private final int read;

    public Notification(int _id, int type, String intent_name, String intent_data, String contents, String image, long dateTime, int read) {
        this._id = _id;
        this.type = type;
        this.intent_name = intent_name;
        this.intent_data = intent_data;
        this.contents = contents;
        this.image = image;
        this.dateTime = dateTime;
        this.read = read;
    }

    public int get_id() {
        return _id;
    }

    public int getType() {
        return type;
    }

    public String getIntent_name() {
        return intent_name;
    }

    public String getIntent_data() {
        return intent_data;
    }

    public String getContents() {
        return contents;
    }

    public String getImage() {
        return image;
    }

    public long getDateTime() {
        return dateTime;
    }

    public int getRead() {
        return read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "_id=" + _id +
                ", type=" + type +
                ", intent_name='" + intent_name + '\'' +
                ", intent_data='" + intent_data + '\'' +
                ", contents='" + contents + '\'' +
                ", image='" + image + '\'' +
                ", dateTime=" + dateTime +
                ", read=" + read +
                '}';
    }
}