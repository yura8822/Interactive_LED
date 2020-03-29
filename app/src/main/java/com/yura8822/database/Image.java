package com.yura8822.database;

public class Image {

    private long id;
    private String name;
    private byte[] imageByteArray;
    private long date;
    private boolean isChecked;

    public Image() {
    }

    public Image(long id, String name, byte[] image, long date) {
        this.id = id;
        this.name = name;
        this.imageByteArray = image;
        this.date = date;
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return imageByteArray;
    }

    public void setImage(byte[] image) {
        this.imageByteArray = image;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
