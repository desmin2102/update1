package com.app.myapp.Class;


public class Ad{
    private int id;
    private String note;
    private String mediaurl;

    public Ad()
    {

    }
    public Ad(int id, String note, String mediaurl) {
        this.id = id;
        this.note = note;
        this.mediaurl = mediaurl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMediaurl() {
        return mediaurl;
    }

    public void setMediaurl(String mediaurl) {
        this.mediaurl = mediaurl;
    }
}
