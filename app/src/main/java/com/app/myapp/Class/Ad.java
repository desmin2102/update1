package com.app.myapp.Class;


public class Ad{
    private String id;
    private String note;
    private String mediaurl;
    private String adName;

    public Ad() {

    }
    public Ad(String id, String note, String mediaurl,String adName ) {
        this.id = id;
        this.note = note;
        this.mediaurl = mediaurl;
        this.adName=adName;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
