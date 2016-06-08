package com.calvin.baserecyclerviewadapter.model;

public class NormalModel {
    public String title;
    public String detail;
    public String avatorPath;
    public boolean selected;

    public NormalModel() {
    }

    public NormalModel(String title, String detail, String avatorPath) {
        this.title = title;
        this.detail = detail;
        this.avatorPath = avatorPath;
    }
}